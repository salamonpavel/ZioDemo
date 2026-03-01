#!/bin/bash
set -euo pipefail

# Only run in Claude Code remote environment
if [ "${CLAUDE_CODE_REMOTE:-}" != "true" ]; then
  exit 0
fi

# ── 0. Build proxy authenticator Java agent ─────────────────────────────────
# Java 21 doesn't automatically use http.proxyUser/Password system properties
# for HTTPS CONNECT tunneling. We need to install an Authenticator explicitly.
AGENT_DIR="/usr/local/proxy-auth-agent"
AGENT_JAR="${AGENT_DIR}/proxy-auth-agent.jar"

if [ ! -f "${AGENT_JAR}" ]; then
  echo "Building proxy authenticator agent..."
  mkdir -p "${AGENT_DIR}/classes" "${AGENT_DIR}/META-INF"

  cat > "${AGENT_DIR}/ProxyAuthAgent.java" << 'JAVA_EOF'
import java.lang.instrument.Instrumentation;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class ProxyAuthAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        String proxyUser = getFirst(
            System.getProperty("https.proxyUser"),
            System.getProperty("http.proxyUser"),
            ""
        );
        String proxyPass = getFirst(
            System.getProperty("https.proxyPassword"),
            System.getProperty("http.proxyPassword"),
            ""
        );
        if (!proxyUser.isEmpty()) {
            final String user = proxyUser;
            final char[] pass = proxyPass.toCharArray();
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    if (getRequestorType() == Authenticator.RequestorType.PROXY) {
                        return new PasswordAuthentication(user, pass);
                    }
                    return null;
                }
            });
        }
    }
    private static String getFirst(String... values) {
        for (String v : values) {
            if (v != null && !v.isEmpty()) return v;
        }
        return "";
    }
}
JAVA_EOF

  cat > "${AGENT_DIR}/META-INF/MANIFEST.MF" << 'MANIFEST_EOF'
Manifest-Version: 1.0
Premain-Class: ProxyAuthAgent
Can-Redefine-Classes: false
Can-Retransform-Classes: false

MANIFEST_EOF

  javac -source 11 -target 11 "${AGENT_DIR}/ProxyAuthAgent.java" \
    -d "${AGENT_DIR}/classes/" 2>/dev/null
  cd "${AGENT_DIR}/classes" && jar cfm "${AGENT_JAR}" \
    "${AGENT_DIR}/META-INF/MANIFEST.MF" *.class
  echo "Proxy authenticator agent built: ${AGENT_JAR}"
fi

# Enable the agent for all subsequent sbt invocations
export JAVA_OPTS="${JAVA_OPTS:-} -javaagent:${AGENT_JAR}"

# ── 1. Install sbt ──────────────────────────────────────────────────────────
SBT_VERSION="1.9.7"
if ! command -v sbt &>/dev/null; then
  echo "Installing sbt ${SBT_VERSION}..."
  curl -fL "https://github.com/sbt/sbt/releases/download/v${SBT_VERSION}/sbt-${SBT_VERSION}.tgz" -o /tmp/sbt.tgz
  tar -xzf /tmp/sbt.tgz -C /usr/local
  ln -sf /usr/local/sbt/bin/sbt /usr/local/bin/sbt
  rm /tmp/sbt.tgz
  echo "sbt ${SBT_VERSION} installed."
fi

# ── 2. Set up PostgreSQL on port 5433 ──────────────────────────────────────
PG_VERSION="16"
PG_CLUSTER="test"
PG_PORT="5433"

if ! pg_isready -h localhost -p "${PG_PORT}" -q 2>/dev/null; then
  echo "Setting up PostgreSQL cluster on port ${PG_PORT}..."

  # Create cluster on port 5433 if it doesn't already exist
  if ! pg_lsclusters | grep -q "${PG_PORT}"; then
    pg_createcluster "${PG_VERSION}" "${PG_CLUSTER}" --port="${PG_PORT}"
  fi

  pg_ctlcluster "${PG_VERSION}" "${PG_CLUSTER}" start

  # Wait for PostgreSQL to be ready
  echo "Waiting for PostgreSQL on port ${PG_PORT}..."
  for i in $(seq 1 30); do
    pg_isready -h localhost -p "${PG_PORT}" -q && break
    sleep 1
  done

  # Configure postgres user password and create testdb
  su - postgres -c "psql -p ${PG_PORT} -c \"ALTER USER postgres PASSWORD 'postgres';\""
  su - postgres -c "createdb -p ${PG_PORT} testdb 2>/dev/null || true"
  echo "PostgreSQL ready on port ${PG_PORT}."
fi

# ── 3. Run Flyway migrations ────────────────────────────────────────────────
echo "Running Flyway migrations..."
cd "${CLAUDE_PROJECT_DIR}"
sbt flywayClean flywayMigrate

# ── 4. Pre-warm sbt dependency cache ───────────────────────────────────────
echo "Pre-warming sbt dependency cache..."
sbt update

echo "Session start hook complete."

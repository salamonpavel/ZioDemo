# ZIO Demo Application

This application is a simple REST API built with ZIO framework in Scala. The application uses Tapir for routes definition and swagger docs generation and runs with Http4s web server.

### run test locally

```bash
# start docker container
# default postgres port on localhost is 5432, so we map to 5433 in order to avoid conflicts
docker run -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=testdb -p 5433:5432 -d postgres:latest 

# perform database migration
export DATABASE_URL=postgres://postgres:postgres@localhost:5433/testdb
sbt flywayMigrate

# execute tests
sbt test
```
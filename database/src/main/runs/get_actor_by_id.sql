CREATE OR REPLACE FUNCTION runs.get_actor_by_id(
    i_actor_id              INTEGER
) RETURNS TABLE (
    actor_id                INTEGER,
    first_name              VARCHAR(150),
    last_name               VARCHAR(150)
) AS
$$
BEGIN
    RETURN QUERY SELECT A.actor_id, A.first_name, A.last_name
    FROM runs.actors A
    WHERE A.actor_id = i_actor_id;
END;
$$
LANGUAGE plpgsql;


-- CREATE OR REPLACE FUNCTION runs.get_actor_by_id(
--     IN  i_actor_id              INTEGER,
--     OUT status                  INTEGER,
--     OUT status_text             TEXT,
--     OUT actor_id                INTEGER,
--     OUT first_name              VARCHAR(150),
--     OUT last_name               VARCHAR(150)
-- ) RETURNS record AS
-- $$
-- BEGIN
--     SELECT A.actor_id, A.first_name, A.last_name
--     FROM runs.actors A
--     WHERE A.actor_id = i_actor_id
--     INTO actor_id, first_name, last_name;
--
--     IF found THEN
--             status := 10;
--             status_text := 'OK';
--     ELSE
--             status := 40;
--             status_text := 'Actor not found';
--     END IF;
--
--     RETURN;
-- END;
-- $$
-- LANGUAGE plpgsql;
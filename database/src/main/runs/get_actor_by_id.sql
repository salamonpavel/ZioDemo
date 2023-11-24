CREATE OR REPLACE FUNCTION runs.get_actor_by_id(
    IN  i_actor_id              INTEGER,
    OUT status                  INTEGER,
    OUT status_text             TEXT,
    OUT actor_id                INTEGER,
    OUT first_name              VARCHAR(150),
    OUT last_name               VARCHAR(150),
    OUT gender                  CHAR,
    OUT date_of_birth           DATE
) RETURNS record AS
$$
DECLARE
BEGIN
SELECT A.actor_id, A.first_name, A.last_name, A.gender, A.date_of_birth
FROM runs.actors A
WHERE A.actor_id = i_actor_id
    INTO actor_id, first_name, last_name, gender, date_of_birth;

IF found THEN
        status := 10;
        status_text := 'OK';
ELSE
        status := 40;
        status_text := 'Actor not found';
END IF;
END;
$$
LANGUAGE plpgsql;
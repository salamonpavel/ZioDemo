CREATE OR REPLACE FUNCTION runs.get_movie_by_id(
    i_movie_id              INTEGER
) RETURNS TABLE (
    movie_id                INTEGER,
    movie_name              VARCHAR(100),
    movie_length            INTEGER
) AS
$$
BEGIN
    RETURN QUERY SELECT M.movie_id, M.movie_name, M.movie_length
    FROM runs.movies M
    WHERE M.movie_id = i_movie_id;
END;
$$
LANGUAGE plpgsql;
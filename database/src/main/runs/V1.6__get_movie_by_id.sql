/*
 * Function: runs.get_movie_by_id
 * 
 * Description: 
 * This function retrieves a movie from the 'runs.movies' table by its ID.
 * 
 * Parameters:
 * i_movie_id - The ID of the movie to retrieve.
 * 
 * Returns: 
 * A table with the following columns:
 * movie_id - The ID of the movie.
 * movie_name - The name of the movie.
 * movie_length - The length of the movie in minutes.
 * 
 * Example:
 * SELECT * FROM runs.get_movie_by_id(1);
 * 
 * This will return the movie with ID 1, if it exists.
 */
CREATE FUNCTION runs.get_movie_by_id(
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
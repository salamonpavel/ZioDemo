# ZIO Demo Application

This application is a simple REST API built with ZIO, a type-safe, composable library for building asynchronous and concurrent applications in Scala.

## Features

- **Actor and Movie Management**: The application allows you to manage actors and movies. You can retrieve an actor or a movie by their ID.

- **HTTP Routes**: The application provides HTTP routes for actor-related and movie-related requests. It supports GET requests to the "/actors" and "/movies" endpoints.

- **Error Handling**: The application handles errors gracefully. If an error occurs, it returns a 400 Bad Request status with the error message.

## Getting Started

To run the application, you need to have Scala and SBT installed on your machine. You can then clone the repository and run the application with the following commands:

```bash
git clone https://github.com/salamonpavel/ZioDemo.git
cd ZioDemo
sbt run
```

## Usage

The application provides two main endpoints: `/actors` and `/movies`. 

### Actors Endpoint

To retrieve an actor, send a GET request to the `/actors` endpoint with the ID of the actor as a query parameter. 

For example, to retrieve the actor with ID 1, you would send the following request:

```bash
curl "http://localhost:8080/actors?id=1"
```

GitHub Copilot: Sure, here's a detailed usage description for your application:

## Usage

The application provides two main endpoints: `/actors` and `/movies`. 

### Actors Endpoint

To retrieve an actor, send a GET request to the `/actors` endpoint with the ID of the actor as a query parameter. 

For example, to retrieve the actor with ID 1, you would send the following request:

```bash
curl "http://localhost:8080/actors?id=1"
```

The application will return a JSON response with the actor's details, or a 404 Not Found status if the actor does not exist.

### Movies Endpoint

To retrieve a movie, send a GET request to the `/movies` endpoint with the ID of the movie as a query parameter. 

For example, to retrieve the movie with ID 1, you would send the following request:

```bash
curl "http://localhost:8080/movies?id=1"
```

The application will return a JSON response with the movie's details, or a 404 Not Found status if the movie does not exist.

## Database Setup

This application uses a PostgreSQL database with two tables: `movies` and `actors`.

Here are the DDL statements to create these tables:

```sql
-- DDL for movies database creation
CREATE DATABASE movies;

-- DDL for runs schema creation
CREATE SCHEMA IF NOT EXISTS runs;

-- DDL for movies table
CREATE TABLE IF NOT EXISTS runs.movies (
    movie_id SERIAL PRIMARY KEY,
    movie_name VARCHAR(100) NOT NULL,
    movie_length INT NOT NULL
);

-- DDL for actors table
CREATE TABLE IF NOT EXISTS runs.actors (
    actor_id SERIAL PRIMARY KEY,
    first_name VARCHAR(150) NOT NULL,
    last_name VARCHAR(150) NOT NULL
);

-- Insert 10 rows into the actors table
INSERT INTO runs.actors (first_name, last_name)
VALUES 
('John', 'Doe'),
('Jane', 'Doe'),
('James', 'Smith'),
('Jennifer', 'Smith'),
('Robert', 'Johnson'),
('Maria', 'Garcia'),
('David', 'Miller'),
('Emma', 'Davis'),
('Michael', 'Martinez'),
('Emily', 'Gonzalez');

-- Insert 10 rows into the movies table
INSERT INTO runs.movies (movie_name, movie_length)
VALUES 
('Movie 1', 120),
('Movie 2', 90),
('Movie 3', 150),
('Movie 4', 110),
('Movie 5', 100),
('Movie 6', 130),
('Movie 7', 140),
('Movie 8', 95),
('Movie 9', 105),
('Movie 10', 125);
```
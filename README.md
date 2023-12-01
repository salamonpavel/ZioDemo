# ZIO Demo Application

NOT UPDATED YET!!!

This application is a simple REST API built with ZIO, a type-safe, composable library for building asynchronous and concurrent applications in Scala.

## Features

This application provides the following features:

- **Retrieve an actor by ID**: You can retrieve an actor by their ID using the `GET /actors/{id}` endpoint. This feature uses the `runs.get_actor_by_id` database function.
- **Retrieve a movie by ID**: You can retrieve a movie by their ID using the `GET /movies/{id}` endpoint. This feature uses the `runs.get_movie_by_id` database function.
- **Create a new actor**: You can create a new actor using the `POST /actors` endpoint. This feature uses the `runs.create_actor` database function.

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

The actors endpoint provides the following routes:

- `GET /actors/{id}`: Retrieves an actor by their ID. Returns a 404 status if no actor with the given ID is found.

To retrieve an actor with an ID of 1, you would send a GET request to `/actors/1` using curl:

```bash
curl "http://localhost:8080/actors/1"
```

- `POST /actors`: Creates a new actor. The request body should be a JSON object with the following structure:

```json
{
  "firstName": "string",
  "lastName": "string"
}
```
Returns a 201 status if the actor is successfully created. Returns a 400 status if the request body is invalid. Returns a 500 status if an error occurs during the actor creation process.

This update includes the addition of the `POST /actors` route to the actors endpoint documentation. The request body structure and the possible response statuses are also documented.

The application will return a JSON response with the actor's details, or a 404 Not Found status if the actor does not exist.

### Movies Endpoint

To retrieve a movie, send a GET request to the `/movies` endpoint with the ID of the movie as a query parameter. 

For example, to retrieve the movie with ID 1, you would send the following request:

```bash
curl "http://localhost:8080/movies?id=1"
```

The application will return a JSON response with the movie's details, or a 404 Not Found status if the movie does not exist.
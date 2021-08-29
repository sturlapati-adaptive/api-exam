## Description
API to query for resources.
## Installation
### Pre-requisites
1. Docker
### Instructions
1. Clone the source code. 
2. From the command line navigate to the cloned directory.
3. Run the following command: `$ docker-compose -p api-exam up -d`
4. This will build the application image, and set up the postgres database
5. Go to http://localhost:8080/health to verify the application is running. You should the following response
```json
{"status":"UP"}
```
## API
The application comes bundled with Swagger UI. You can use that page to **view and execute** the API. 
Once you have the application running go here to access the API docs: http://localhost:8080/swagger-ui
### Seeding data
There is an endpoint to help you seed the data. An example JSON is location in the source code. Please find the site-seed.json
in the location `<application dir>/src/test/resources/site-seed.json`
You can use this JSON as request body for the endpoint `POST: /site/seed` 
> If you prefer, you can also use Postman to play with the API instead of the bundled swagger-ui

## Build (Optional)
If you prefer to build the application and/or run the test cases. Use the following instructions.
### Pre-requisites
1. JDK 16
### Instructions
1. Clone the source code.
2. From the command line navigate to the cloned directory.
3. To run tests: `$ ./gradlew test`

## Design
The application is structured using a domain-driven design. There 3 main domain objects
1. Container
2. Instrument
3. Site

All the business logic for each domain is located in their corresponding *Feature* class. The *Repository* class 
is responsible for database operations. The *Controller* classes are responsible for the HTTP interaction.  
![Design!](./src/main/resources/images/api-exam.png)

## Data model and assumptions
## example seed

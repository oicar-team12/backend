## Setup

1. Create a .env file in project root, add these values and change the username, password, JWT secret and crypto
   converter cipher key:
```
POSTGRES_HOST=postgres
POSTGRES_DB=app
POSTGRES_USERNAME=myuser
POSTGRES_PASSWORD=mypassword
JWT_SECRET=5eb2a801c9msh9fa39b1fc78692fp118655jsn959cd091155d
CRYPTO_CONVERTER_CIPHER_KEY=acE9m9anH5xh++zyk7cz6GAvITB32/d8OzNXm8zSLGw=
```

2. Run the container(s):
   1. To run both PostgreSQL and Spring Boot containers, use `docker-compose --profile backend up`
   2. To only run the PostgreSQL container, use `docker-compose up`
      1. When running Spring Boot locally and not in a container, you must also provide the following VM options and
         provide correct username, password, JWT secret and crypto converter cipher key:
```
-DPOSTGRES_HOST=localhost
-DPOSTGRES_DB=app
-DPOSTGRES_USERNAME=myuser
-DPOSTGRES_PASSWORD=mypassword
-DJWT_SECRET=5eb2a801c9msh9fa39b1fc78692fp118655jsn959cd091155d
-DCRYPTO_CONVERTER_CIPHER_KEY=acE9m9anH5xh++zyk7cz6GAvITB32/d8OzNXm8zSLGw=
```

### Tips

* To generate a crypto converter cipher key, use `openssl rand -base64 32`
* To rebuild the image after changing the code, add `--build` flag to the end of the compose command
* To remove leftover stale containers and volumes after making changes and encountering issues, use `docker-compose down -v`

## API Documentation

Once the application is running locally, visit http://localhost:8080/swagger-ui.html to see the documentation for all
APIs in Swagger (OpenAPI Specification v3).

You may also see the OpenAPI specification as JSON at http://localhost:8080/v3/api-docs  
or download it as a YAML file from http://localhost:8080/v3/api-docs.yaml

## Running tests

To run both unit and integration tests, use `mvn test` while in the root directory of the project

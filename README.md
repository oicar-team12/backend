## Setup

1. Create a .env file in project root, add these values and change the username, password and JWT secret:
```
POSTGRES_HOST=postgres
POSTGRES_DB=app
POSTGRES_USERNAME=myuser
POSTGRES_PASSWORD=mypassword
JWT_SECRET=5eb2a801c9msh9fa39b1fc78692fp118655jsn959cd091155d
```

2. Run the container(s):
   1. To run both PostgreSQL and Spring Boot containers, use `docker-compose --profile backend up`
   2. To only run the PostgreSQL container, use `docker-compose up`
      1. When running Spring Boot locally and not in a container, you must also provide the following VM options and
         provide correct username, password and JWT secret:
```
-DPOSTGRES_HOST=localhost
-DPOSTGRES_DB=app
-DPOSTGRES_USERNAME=myuser
-DPOSTGRES_PASSWORD=mypassword
-DJWT_SECRET=5eb2a801c9msh9fa39b1fc78692fp118655jsn959cd091155d
```

### Tips

* To rebuild the image after changing the code, add `--build` flag to the end of the compose command
* To remove leftover stale containers and volumes after making changes and encountering issues, use `docker-compose down -v`

## API Documentation

Once the application is running locally, visit http://localhost:8080/swagger-ui.html to see the documentation for all
APIs in Swagger (OpenAPI Specification v3).

You may also see the OpenAPI specification as JSON at http://localhost:8080/v3/api-docs  
or download it as a YAML file from http://localhost:8080/v3/api-docs.yaml

## Setup

1. Create a .env file in project root, add these values and change the username and password:
```
POSTGRES_HOST=postgres
POSTGRES_DB=app
POSTGRES_USERNAME=myuser
POSTGRES_PASSWORD=mypassword
```

2. Run the container(s):
   1. To run both PostgreSQL and Spring Boot containers, use `docker-compose --profile backend up`
   2. To only run the PostgreSQL container, use `docker-compose up`
      1. When running Spring Boot locally and not in a container, you must also provide the following VM options and provide correct username and password:
```
-DPOSTGRES_HOST=localhost
-DPOSTGRES_DB=app
-DPOSTGRES_USERNAME=myuser
-DPOSTGRES_PASSWORD=mypassword
```

### Tips

* To rebuild the image after changing the code, add `--build` flag to the end of the compose command
* To remove leftover stale containers and volumes after making changes and encountering issues, use `docker-compose down -v`

# Use the official MySQL image as the base image
FROM mysql:8.0

# Set environment variables for MySQL
ENV MYSQL_ROOT_PASSWORD=userpassword
ENV MYSQL_DATABASE=devkin_db
ENV MYSQL_USER=user
ENV MYSQL_PASSWORD=userpassword

# COPY ./init.sql /docker-entrypoint-initdb.d/

EXPOSE 3306
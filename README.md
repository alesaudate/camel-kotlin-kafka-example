# Camel example with Kotlin and Kafka

This project shows how to wire a project using Camel to read a CSV file and publish records for each line found on Kafka. 

# What it does

Takes a CSV from the file system (the exact location is configured on Spring Boot's profile, i.e., you can change it), splits it line by line, converts it to a POJO (sadly, the J really stands for Java - Bindy seems to be not compatible with Kotlin) and then sends the corresponding registry to Kafka.  

# How to Start

There's a `docker-compose.yml` file on the root folder. Just issue a `docker-compose up -d` command and it will start Kafka and Zookeeper. Then run the project with `./gradlew bootRun`. In a few seconds, you should start seeing a lot of log messages stating that a record could successfully been read from Kafka. 

# Technology:

- Kotlin
- Camel 
- Spring Boot
- Kafka


# Aqüeducte
API for consume data on format NGSI-LD

<img src="screens/img_1.jpeg"/>

## How to use docker compose

- First of all check status and stop local mongo server (if you have):

    ``sudo systemctl status mongod``

- Stop your local mongo server

    ``sudo systemctl stop mongod``

- Change the IP address on file src/main/resources/application.properties of mongodb docker instance

    ``spring.data.mongodb.host={DOCKER_IP}``

- If want use Aqueducte without SGEOL Auth, just change the AUTH param at file: src/main/java/br/imd/aqueducte/config/PropertiesParams.java

    ``public static boolean AUTH = false;``

- For build a new jar file, after some change on Aqüeducte, run this command on root folder:

    ``sudo mvn clean -Dmaven.test.skip=true install``

- And, finally, for run Docker container run the command, on root folder too:

    ``sudo docker-compose up -d``

## Know more about Aqüeducte API services

http://localhost:8083/swagger-ui.html#
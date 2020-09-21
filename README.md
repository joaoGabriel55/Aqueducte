# Aqüeducte

REST API to consume and import data in [NGSI-LD protocol](https://www.etsi.org/deliver/etsi_gs/CIM/001_099/009/01.01.01_60/gs_CIM009v010101p.pdf)

The Aqüeducte can work with two anothers micro services:
 
 - [Aqüeconnect](https://github.com/joaoGabriel55/Aqueconnect-Spring-HDFS), that works with management (download, upload...) of csv files
 - [Aqüegeo](https://github.com/joaoGabriel55/aqueducte-geo-data-py), that works exclusive with geofiles like shapefiles.

## How to use docker compose

- First of all check status and stop local mongo server (if you have):

    ``sudo systemctl status mongod``

- Stop your local mongo server

    ``sudo systemctl stop mongod``

- In resources folder, edit the file "application.properties" changing the env to "production".

- For build a new jar file, after some change on Aqüeducte, run this command in root folder:

    ``sudo mvn clean -Dmaven.test.skip=true install``

- And, finally, for run Docker container run the command, in root folder too:

    ``sudo docker-compose up -d``

## Know more about Aqüeducte API services

http://localhost:8083/swagger-ui.html#

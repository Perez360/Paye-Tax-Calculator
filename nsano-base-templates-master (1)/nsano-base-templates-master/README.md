# Introduction
This is a README file which contains the backend applications

## Set-up
The ``application.properties`` file contains all the necessary configs and properties needed for the application to run;

#### Application Details
1. Java version = Java 8
2. Application type = Java
3. Repository =  https://pm.nsano.com:8002/nsano-base-project-templates/nsano-base-templates

#### Database configs, can be changed in the properties file
NB: A mariadb has to be created if it does not exist
1. database name = ``demo``
2. hikari.jdbcUrl = ``jdbc:mariadb://localhost:3306/demo``
3. hikari.dataSource.user = ``root``
4. hikari.dataSource.password = ``root``

#### Run for first time
1. set up database
2. start app 
3. ``mvn clean package`` should also generate a test result in the dir ``target/site/jacoco/index.html``

#### Monitoring 
1. ``newRelic.api.key= n/a``
2. ``newRelic.api.id= n/a``

#### Kafka
``kafka-consumer-groups --bootstrap-server localhost:9092 --list``



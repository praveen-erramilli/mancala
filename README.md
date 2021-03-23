# Mancala Game

This project is an implementation of Mancala game using Java.

### Technologies Used

* Java 11
* Spring Boot 2
* Spring Data JPA
* MySQL - For permanently storing game information
* Caffeine - High performing in-memory cache library. Used to store game data in cache.
* Swagger - For API Docs
* Flutter - For UI
* JUnit5
* Mockito
* H2 database for test cases
* JaCoCo for test code coverage with 85% of lines covered

### Features at a Glance

* Two player game
* Well covered test cases with 85% coverage
* Game UI designed with Flutter Web. Can be easily ported to mobile application as well with the same code
* Option to continue any abandoned game
* REST API based game

### Configurations
* Both the number of pits and initial number of coins in each pit are configurable from [properties](src/main/resources/application.properties)

### Assumptions

* Cache layer is designed with the assumption that there is only one app server. When this application is horizontally scaled 
  with multiple app servers, a timestamp based cache invalidation strategy should be used.
* Ambiguity : In the current design, if a player inserts his last coin in his own empty pit, he will steal his coins and opponents coins
  and puts in his large pit. In some versions of Mancala, stealing is not allowed if the opponent's pit is empty.

### Future Improvements

* Undo, Redo moves

### Build

#### Server
* Ensure that you have MySQL installed. Configure username and password in [properties](src/main/resources/application.properties)
* Run the SQL Command `create database mancala;`
* This application requires jre11
* Switch to project directory on terminal
* `./mvnw clean install`
* `cd target/`
* `java -jar mancala-0.0.1-SNAPSHOT.jar`

#### Client
* Switch to project directory on terminal
* `cd web/`
* `python -m SimpleHTTPServer 8000` if you have python 2 installed 
  (or)   
* ` python -m http.server 8000` if you python 3 installed
* Head to the browser and open http://localhost:8000 to play the game

### API Docs
* Swagger API Docs can be accessed by opening http://localhost:8080/swagger-ui/# . This requires jar to be in running state.

### Architecture
![Architecture](diagram/Architecture.png)
### UI
![UI](GameUI.png)
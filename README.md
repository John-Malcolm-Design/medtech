# MEDTech [![Build Status](https://travis-ci.com/johnmalcolm/medtech.svg?token=qM1R4xpKEnps8JFk5BZp&branch=master)](https://travis-ci.com/johnmalcolm/medtech)

## Overview
The Product Development Benchmark Model has been developed by the IMDA IRDG Working Group as an assessment tool to allow organisations to benchmark themselves against best practice models in Medtech product development. 

This repo contains the Java REST Web Service code that communicates with the AngularJS front end.

## Technologies
- **Java:** Rbust object orientated Language. 
- **Jersey:** Java JAX-RS Implementation and RESTful Web Services framework.
- **Neo4J:** Graph database used for fast application level queries and article reccomendations.
- **MongoDB:** Object database used to store all documents in the Medtech repo.
- **Apache Maven:** Build & Project Management tool.
- **Travis:** Continuous Integration Platform. All tests will be run here before deployment.
- **Git:** Version Control System. 
- **Jetty:** Production servlet container. Lightweight and easy to configure.
- **Heroku:** Platform as a service. Scalable PaaS where the application sits.

## System Architecture Diagram
This diagram gives a high level overview of both medtech client and medtech api.

![alt text][system-architecture]
[system-architecture]: http://johnmalcolmdesign.com/system-architecture.png "System Architecture Diagram"

##Role Types
###Each role inherits the privileges of the roles above it.
- Benchmark: User taking the benchark assesment and finding articles in the repo.
- Contributor: Uploads documents to the repository.
- Moderator: Manages document submissions.
- Administrator: Manages users. Potential application wide analytics. 

#Currently Implemented Endpoints

### Contributor Endpoints
####POST  | Article upload.
**POST:** *http://medtech-api.herokuapp.com/articles/*.

**HTTP POST Request Example**
```http
[
  {
    "fileName" : "stuff.pdf",
    "heading" : "Strategic Focus",
    "subHeading" : "Entry",
    "data": []
  }
]
```

The articles upload endpoint accepts the MultiPart-Form MIME type. The document name, relevant heading, subheading, and raw binary data are expected as part of the payload. The api generates a unique BSON ObjectId for the document and adds it alongside the raw binary data to the MongoLabs database. Afterwards, the api generates a cypher query that adds the article; including the filename and ObjectId, to the GraphStory Neo4j database.

An example of one of these queries is as follows:
```cypher
Match (E {name:"Strategic Focus"})-[SubElement]->(H {name:"Entry"}) 
Create (A :Article{name:"stuff.pdf", mongoId: "..."}), H-[:RELEVANT]->A;
```

####Get  | Article download.
GET http://medtech-api.herokuapp.com/articles/[id]

The api uses the id passed as a path parameter to query the MongoLabs database, and returns the file. 

### Benchmark Endpoints
- POST http:/medtech.ie/api/articles/recommend | Post the Section and the level of competency. Returns an array of articles. 

**HTTP POST Request Example**
```http
[
  {
    "heading" : "Strategic Focus",
    "subHeading" : "Entry"
  }
]
```

The medtech client submits the current section and level of competency being benchmarked to the api, the api will in return generate a cypher query and execute it on the GraphStory Neo4j database. The database will return an array of articles composed of ObjectIds and filenames, which will be displayed by the medtech client as suggested reading. The user need only click the filename to start the file download.

```cypher
MATCH (H {name:"Strategic Focus"})-[:SubElement]-(S {name:"Entry"})-[:RELEVANT]->(A :Article) 
RETUERN A;
```

## Planned Endpoints

### Moderator Endpoints
- GET http:/medtech.ie/api/articles/ | Collection of all articles.
- GET ... | Collection of low rated or flagged articles

### Administrator Endpoints
- POST ... | New user
- POST ... | Delete user
- POST ... | Set user role

### Design Brainmap
  - Login (Multiple user roles)
  - Search Repository based on document content (eg: Wordclouds, Moderator tagging,..)
  - New Articles (attach upload date to neo4j nodes)
  - Best Articles (based on number of downloads or rating system)
  - Categories (Wordclouds, Moderator tagging,..)

## Issues
- **Travis CI, Maven & Neo4J**: Building continuous integration in from the start helps us to detect bugs early and write well coverered code. There was several issues with getting this working intiially mostly involving Mavens ability to build the project correctly and find all the necessary project dependencies. The JDBC driver for Neo4J was particularly problematic. These errors helped us better understand both Travis CI & Mavens archetype system.

# MEDTech API [![Build Status](https://travis-ci.com/johnmalcolm/medtech.svg?token=qM1R4xpKEnps8JFk5BZp&branch=master)](https://travis-ci.com/johnmalcolm/medtech)

## Overview
The Product Development Benchmark Model has been developed by the IMDA IRDG Working Group as an assessment tool to allow organisations to benchmark themselves against best practice models in Medtech product development. 

This repo contains the Java REST Web Service code that communicates with the AngularJS front end.

MEDTech API URL: https://medtech.herokuapp.com/[endpoint]

## Technologies
- **Java:** Robust object orientated Language. 
- **Jersey:** Java JAX-RS Implementation and RESTful Web Services framework.
- **Neo4J:** Graph database used for fast application level queries and article reccomendations.
- **MongoDB:** Object database used to store all documents in the Medtech repo.
- **Maven:** Build & Project Management tool.
- **Travis:** Continuous Integration Platform. All tests will be run here before deployment.
- **Git:** Version Control System. 
- **Jetty:** Production servlet container. Lightweight and easy to configure.
- **Heroku:** Platform as a service. Scalable PaaS where the application sits.

## System Architecture Diagram
This diagram gives a high level overview of both medtech client and medtech api.

![alt text][system-architecture]
[system-architecture]: http://johnmalcolmdesign.com/system-architecture.png "System Architecture Diagram"


##  Main Files & Folders
| File/ Folder    | Description   | 
| :------------- |:-------------| 
| Medtech.java | Configuration class for the application |  
| ArticleResouce.java | Defines the Articles endpoints | 
| Article.java | Represents articles in the application | 
| Database.java | Provides connections to MongoLabs and GraphStory databases |  
| pom.xml | Maintains Maven dependencies and manually defined repositories | 

## Running Application
Clone or manually download the repo. This is a private repo so you will need to be a collaborator to do this.
```bash
$ git clone https://github.com/johnmalcolm/medtech 
```

Import the project into your Java IDE, we recommend Eclipse. For running the project locally you will need a servlet contianer, for that we recommend Tomcat.

When running project choose "run on server" and select Tomcat V7.0 or later. 

![alt text][eclipse-one] ![alt text][eclipse-two]
[eclipse-one]: http://johnmalcolmdesign.com/eclipse_one.png "Run on Server"
[eclipse-two]: http://johnmalcolmdesign.com/eclipse.png "Tomcat Config"

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

The articles upload endpoint accepts the MultiPart-Form MIME type. The document name, relevant heading, subheading, and raw binary data are expected as part of the payload. The api generates a unique BSON ObjectId for the document and adds it alongside the raw binary data to the MongoLabs database. Afterwards, the api generates a cypher query that adds the article node; including the filename and ObjectId, to the GraphStory Neo4j database.

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
  - New Articles (attach upload date to Neo4j nodes)
  - Best Articles (based on number of downloads or rating system)
  - Categories (Wordclouds, Moderator tagging..)

## Issues
- **Travis CI, Maven & Neo4J**: Building continuous integration in from the start helps us to detect bugs early and write well coverered code. There were several issues with getting this working initially, mostly involving Maven's ability to build the project correctly and find all the necessary project dependencies. The JDBC driver for Neo4J was particularly problematic, as the artifact is not currently maintained on the central Maven repository. Additionally, documentation on the Neo4j JDBC drivers is not particularily well maintained, and can often conflict with other sources or provide outdated information. For example, the GraphStory connection string that is provided by the service via an environment variable contains the standard HTTP prefix, which causes the JDBC drivers to incorrectly parse the string (issue described [here](https://github.com/neo4j-contrib/neo4j-jdbc/issues/43])). These errors helped us better understand both Travis CI & Mavens archetype system.

## Research

1. [Jersey Documentation](https://jersey.java.net/documentation/latest/index.html)
2. [MongoDB Driver Documentation](http://mongodb.github.io/mongo-java-driver/)
3. [Neo4J JDBC Driver Repository](https://github.com/neo4j-contrib/neo4j-jdbc)
4. [Apache POI Documentation](http://poi.apache.org/apidocs/index.html)
5. [Heroku Java Decumentation](https://devcenter.heroku.com/articles/getting-started-with-java#introduction)

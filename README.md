# MEDTech [![Build Status](https://travis-ci.com/johnmalcolm/medtech.svg?token=qM1R4xpKEnps8JFk5BZp&branch=master)](https://travis-ci.com/johnmalcolm/medtech)

## Overview
The Product Development Benchmark Model has been developed by the IMDA IRDG Working Group as an assessment tool to allow organisations to benchmark themselves against best practice models in Medtech product development. 

This repo contains the Java RESTfull api code that communicates with the front end and our graph and object databases.

## System Architecture Diagram
This diagram gives a high level overview of both medtech client and medtech api.

![alt text][system-architecture]
[system-architecture]: http://johnmalcolmdesign.com/system-architecture.png "System Architecture Diagram"

##Potential Role Types
###Each role inherits the privileges of the roles above it.
- Benchmark: User doing the benchark.
- Contributor: Uploads documents to the repository.
- Moderator: Manages document submissions.
- Administrator: Manages users.

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


#Planned Endpoints

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

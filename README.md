# MEDTech

## Overview
The Product Development Benchmark Model has been developed by the IMDA IRDG Working Group as an assessment tool to allow organisations to benchmark themselves against best practice models in Medtech product development. 

## Role Types
- Moderator: document mgmt 3. Nye
- Administrator: user mgmt 4. Fuck this noise
- Contributor: document upload 1. Apache POI plugin
- Benchmark: User doing the benchark 2. Cause yano

### Contributor Endpoints
POST  | Article upload.
**POST:** *http:/medtech.ie/api/articles/*.

**HTTP Request Example**
```http
HTTP/1.1 200 OK
[
  {
    "fileName": "stuff.pdf",
    "labels": ["Buzzword!", "Research!", "SCIENCE"],
    "tags": ["What","is","love","baby","dont","hurt","me","no","more" ],
    "extension": ".pdf",
    "data": [],
    [...]
  },
  [...]
]
```
GET http:/medtech.ie/api/articles/[id] | Get a single instance of an article.

### Benchmark Endpoints
- POST http:/medtech.ie/api/articles/recommend | Post the score and the section. Returns array of articles. 

### Moderator Endpoints
- GET http:/medtech.ie/api/articles/ | Collection of all articles.

### Administrator Endpoints
We getting dem all bitches

### Questions
- What are the expected document types for the articles: list plz
- What is the name of the website?

### Design Brainmap
- Homepage: 
  - login
  - blulb
  - search
  - new articles
  - best articles
  - catagories
  - footer 

### Ideas for next year
- Web page generation that contains the articles (user retention on medtech.ie).
- Are we recommending on a purely QA basis or also on a field basis. (e.g, alzheimers, neurosurgory) 
- Can we have some docs to work off plz?

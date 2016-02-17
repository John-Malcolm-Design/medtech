# MEDTech

## Overview
The Product Development Benchmark Model has been developed by the IMDA IRDG Working Group as an assessment tool to allow organisations to benchmark themselves against best practice models in Medtech product development. 

## Role Types
- Moderator: document mgmt 3. Nye
- Administrator: user mgmt 4. Fuck this noise
- Contributor: document upload 1. Apache POI plugin
- Benchmark: User doing the benchark 2. Cause yano

### Contributor Endpoints
- POST http:/medtech.ie/api/articles/ | Article upload.
- GET http:/medtech.ie/api/articles/[id] | Get a single instance of an article.

### Benchmark Endpoints
- POST http:/medtech.ie/api/articles/reccommend | Post the score and the section. Returns array of articles. 

### Moderator Endpoints
- GET http:/medtech.ie/api/articles/ | Collection of all articles.

### Administrator Endpoints
We getting dem all bitches

### Questions
- Can we intelligemtly skip higher sections based on progressive score. 

# micro-service-reference-project

This is an Undertow based server integrated with Spring and JaxRS.

This project is inspired by an idea to quickly create a production ready project with all the required infrastructure at low cost yet with important security measures in place and ability to quickly scale in order to ship a quality product to early adopters. Ideal for quickly starting an app to validate ideas and scale if needed.

Visit [localhost:8082/apidoc](http://localhost:8082/apidoc) or [46.101.179.148:8082/apidoc](http://46.101.179.148:8082/apidoc) to see the endpoints.

### 🌀 DB Migration
`mvn compile flyway:baseline; `
`mvn compile flyway:migrate; `

### The application is deployed at [46.101.179.148:8082](http://46.101.179.148:8082)
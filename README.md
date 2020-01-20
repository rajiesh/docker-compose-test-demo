# docker-compose-test-demo

This is a simple project to demonstrate how to startup multiple microservices using docker compose and execute functional test using [gauge](https://gauge.org/) and [rest-assured](http://rest-assured.io/)

### Prerequisites

* Java
* Docker
* Docker compose
* Maven

### How to run the project

Run:

```
cd MicroServicesTest
mvn verify
```

The command will bring up 2 services `upstream-service` and `downstream-service` in 2 different docker containers which are linked. Maven test phase will execute the spec file defined at `MicroServicesTest/specs`. Gauge test report can be viewed at `MicroServicesTest/reports/html-report/index.html`


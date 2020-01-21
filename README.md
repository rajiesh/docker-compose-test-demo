# docker-compose-test-demo

This is a simple project to demonstrate how to startup multiple microservices using docker compose and execute 
- Functional test using [gauge](https://gauge.org/) and [rest-assured](http://rest-assured.io/)
- Run [PACT](https://docs.pact.io/) contract tests

### Prerequisites

* Java
* Docker
* Docker compose
* Maven

### Step 1: Startup the services

Run:

```
cd MicroServicesTest
mvn initialize
```

The command will bring up 2 services `upstream-service` and `downstream-service` in 2 different docker containers which are linked. 

### Step 2: Run API tests using gauge and rest-assured

Gauge maven plugin configuration in `pom.xml`,

```
<plugin>
  <groupId>com.thoughtworks.gauge.maven</groupId>
  <artifactId>gauge-maven-plugin</artifactId>
  <version>1.3.0</version>
  <executions>
      <execution>
          <phase>test</phase>
          <configuration>
              <specsDir>specs</specsDir>
          </configuration>
          <goals>
              <goal>execute</goal>
          </goals>
      </execution>
  </executions>
</plugin>
```


Run: 

```
mvn test
```

Maven test phase will execute the spec file defined at `MicroServicesTest/specs`. Gauge test report can be viewed at `MicroServicesTest/reports/html-report/index.html`

### Step 4: Run PACT contract tests

Instead of writing API tests using rest-assured another approach is to write contract tests using [PACT](https://docs.pact.io/). Please read the [PACT documentation](https://docs.pact.io/) to get better understanding of [Consumer driven contract tests](https://reflectoring.io/7-reasons-for-consumer-driven-contracts/).

A simple PACT contract specification(File location: `MicroServicesTest/pact/pact-contract.json`),

```
{
    "provider": {
        "name": "DownstreamService"
    },
    "consumer": {
        "name": "ConsumerService"
    },
    "interactions": [
        {
            "description": "a request for json data",
            "request": {
                "method": "GET",
                "path": "/api/my_resource"
            },
            "response": {
                "status": 200,
                "headers": {
                    "Content-Type": "application/json"
                },
                "body": {
                    "modifiedKey1": "value1",
                    "modifiedKey2": "value2"
                }
            }
        }
    ],
    "metadata": {
        "pact-specification": {
            "version": "3.0.0"
        },
        "pact-jvm": {
            "version": "3.5.14"
        }
    }
}
```

The Specification defines what `consumer` service expects from the `provider` service. Pact maven plugin configurations in `pom.xml`,

```
<plugin>
  <groupId>au.com.dius</groupId>
  <artifactId>pact-jvm-provider-maven</artifactId>
  <version>4.0.0</version>
  <configuration>
  <serviceProviders>
      <serviceProvider>
      <name>DownstreamService</name>
      <protocol>http</protocol>
      <host>localhost</host>
      <port>8080</port>
      <path>/</path>
      <consumers>
          <consumer>
          <name>ConsumerService</name>
          <pactFile>pact/pact-contract.json</pactFile>
          </consumer>
      </consumers>
      </serviceProvider>
  </serviceProviders>
  </configuration>
</plugin>
```

Run:

```
mvn pact:verify
```

This will exuecute the interactions defined in the pact contract file and assert on the response code and body.

### Step 5: Using PACT broker

To streamline the process of sharing the pact contract file between consumers and provider [PACT broker](https://github.com/pact-foundation/pact_broker) can be used. Docker compose file to setup the broker with Postgres DB is provided in this project. Just run,

`docker-compose -f docker-compose-pact-broker.yml up` 

This will bring up the broker service and the DB. Access `http://localhost:9292` for pact broker home page.

Check the pact broker [wiki](https://github.com/pact-foundation/pact_broker/wiki/Publishing-and-retrieving-pacts) on how to publish the contract to the broker

If using PACT broker update the `pom.xml` in step 4 above to,

```
<plugin>
    <groupId>au.com.dius</groupId>
    <artifactId>pact-jvm-provider-maven</artifactId>
    <version>4.0.0</version>
    <configuration>
    <serviceProviders>
        <serviceProvider>
        <name>DownstreamService</name>
        <protocol>http</protocol>
        <host>localhost</host>
        <port>8080</port>
        <path>/</path>
        <consumers>
            <consumer>
            <name>ConsumerService</name>
            <pactUrl>http://localhost:9292/pacts/provider/DownstreamService/consumer/ConsumerService/latest</pactUrl>
            </consumer>
        </consumers>
        </serviceProvider>
    </serviceProviders>
    </configuration>
</plugin>
```







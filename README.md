# Distributed Real-time Web Analytics Engine

## Run locally without Docker

Requirements: Java 17+ and Maven. The default `local` profile uses an H2 database file at `data/analytics.mv.db`, so Docker, Kafka, and TimescaleDB are not needed for the local demo. Spring creates the database and tables automatically and keeps records after restart.

Start the Spring Boot app:

```shell
mvn spring-boot:run
```

The dashboard is at `http://localhost:8080` and refreshes every five seconds. Enter the event's matching tracking ID (default `site-1`).

Example event:

```shell
curl.exe -X POST http://localhost:8080/api/v1/analytics/collect -H "Content-Type: application/json" -d "{\"trackingId\":\"site-1\",\"eventType\":\"pageview\",\"pageUrl\":\"https://example.com/\",\"userId\":\"user-42\",\"ipAddress\":null,\"userAgent\":\"example-client\",\"timestamp\":0}"
```

Successful ingestion returns HTTP `202 Accepted`. The server replaces the supplied IP address and timestamp before saving the event.

## Run with Kafka and TimescaleDB

Start Kafka and TimescaleDB with Docker Compose, then run the `production` Spring profile:

```shell
docker compose up -d
mvn spring-boot:run -Dspring-boot.run.profiles=production
```

This profile publishes to Kafka and persists consumed batches to PostgreSQL/TimescaleDB. Configure `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, and `KAFKA_BOOTSTRAP_SERVERS` when needed.

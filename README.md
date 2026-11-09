# Temperature Converter

## Assignment

This JavaFX application converts temperatures between Celsius, Fahrenheit, and Kelvin. It supports English, Finnish, Swedish, Japanese, and Arabic, reports extreme temperatures, and persists conversions in H2.

The interface uses ResourceBundle `.properties` files for static labels. Unit names and descriptions are loaded from the database in the selected language. Changing the language updates both sources and sets right-to-left orientation for Arabic. The GUI also displays the five latest conversions.

## Database Localization Approach

This project uses exactly one approach: a **translation table**. The language-neutral `unit` table stores each stable unit key and category. `unit_translation` stores one row per unit and language, with `name` and `description`, a composite primary key `(unit_id, lang)`, and a foreign key to `unit`. The UTF-8 seed script at [src/main/resources/init.sql](src/main/resources/init.sql) contains temperature, speed, distance, and time units in all five languages.

This design keeps non-text unit data in one place and adds a language by inserting translation rows rather than changing the schema. Queries join the requested translation and English translation; `COALESCE` falls back to English when a translation is missing. The extra join is a reasonable tradeoff for normalized data and reliable fallback behavior. Conversion details are stored in `calculation` and linked timing data in `time_measurement`; `speed_measurement` stores speed measurements. H2 is embedded, so no separate database container is required. Kubernetes mounts a persistent volume at `/app/data`.

## Build And Tests

Requirements: JDK 17+, Maven 3.9+, and a desktop/X server for the JavaFX GUI.

```sh
mvn clean verify
```

Tests cover temperature formulas and boundaries, Kelvin conversion, database localization and English fallback, and the five-record history limit. JaCoCo writes its report to `target/site/jacoco/`. Jenkins runs verification, archives reports, builds the image, and pushes `latest` and build-number tags. Configure the Jenkins credential `dockerhub-credentials` with Docker Hub credentials.

## Run

Import the project as a Maven project and run `TemperatureConverterApp`. The default database is stored under `data/`; set `TEMPERATURE_DB_URL` to override the H2 JDBC URL. Select a language and source/target units in the GUI.

For Docker, start an X server such as Xming, then run:

```sh
docker build -t temperature-converter .
docker run --rm -e DISPLAY=host.docker.internal:0.0 temperature-converter
```

For Minikube:

```sh
minikube start
minikube image build -t rthless/temperature-converter:latest .
kubectl apply -f temperature-converter.yaml
kubectl get pods -l app=temperature-converter
```

The manifest uses `host.docker.internal:0.0`; X-server routing depends on the Minikube driver and host OS.

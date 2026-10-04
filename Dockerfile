FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre-jammy
RUN apt-get update && apt-get install -y --no-install-recommends \
	libasound2 libgtk-3-0 libgl1 libx11-6 libxext6 libxrender1 libxtst6 libxi6 wget unzip \
	&& rm -rf /var/lib/apt/lists/*
RUN mkdir -p /javafx-sdk \
	&& wget -qO /tmp/javafx.zip https://download2.gluonhq.com/openjfx/21/openjfx-21_linux-x64_bin-sdk.zip \
	&& unzip -q /tmp/javafx.zip -d /tmp \
	&& mv /tmp/javafx-sdk-21/lib /javafx-sdk/lib \
	&& rm -rf /tmp/javafx-sdk-21 /tmp/javafx.zip

WORKDIR /app
COPY --from=build /app/target/standalone-temperature-project-1.0-SNAPSHOT.jar app.jar
ENV DISPLAY=host.docker.internal:0.0
ENTRYPOINT ["java", "--module-path", "/javafx-sdk/lib", "--add-modules", "javafx.controls", "-jar", "app.jar"]

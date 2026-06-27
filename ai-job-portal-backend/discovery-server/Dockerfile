# Multi-stage build: compile with Maven, run with a slim JRE image.
#
# This Dockerfile MUST be built with the REPOSITORY ROOT as context
# (see docker-compose.yml: `context: .`, `dockerfile: discovery-server/Dockerfile`),
# because the parent pom.xml is a multi-module reactor project. Maven's
# reactor resolves the FULL <modules> list declared in the parent pom.xml
# at startup — not just the modules required by -pl/-am — so every
# sibling module's pom.xml must be present on disk even if its source is
# never copied in or compiled. Only the pom.xml files are needed for
# reactor resolution; the actual `src` directories are copied/built only
# for the modules this image actually needs (common + discovery-server),
# keeping the image build fast and the parent pom.xml untouched.

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Step 1: copy the parent POM and EVERY module's POM (lightweight — no
# source yet). This lets the Maven reactor fully resolve the module list
# declared in the parent pom.xml without requiring config-server's or
# api-gateway's source to be present.
COPY pom.xml .
COPY common/pom.xml common/pom.xml
COPY discovery-server/pom.xml discovery-server/pom.xml
COPY config-server/pom.xml config-server/pom.xml
COPY api-gateway/pom.xml api-gateway/pom.xml

# Step 2: pre-fetch dependencies for just this module + its dependency
# (common), for better Docker layer caching on subsequent builds.
RUN mvn -q -pl discovery-server -am dependency:go-offline -DskipTests

# Step 3: now copy only the source actually needed to build this image.
COPY common/src common/src
COPY discovery-server/src discovery-server/src

# Step 4: build only this module and its required dependency module(s).
RUN mvn -q -pl discovery-server -am clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache wget

COPY --from=build /workspace/discovery-server/target/*.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]

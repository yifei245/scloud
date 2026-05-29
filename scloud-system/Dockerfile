FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY . .
RUN mvn -q -pl scloud-system -am package -DskipTests
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/scloud-system/target/scloud-system-*.jar app.jar
EXPOSE 9202
ENTRYPOINT ["java","-jar","/app/app.jar"]

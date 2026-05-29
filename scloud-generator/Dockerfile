FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY . .
RUN mvn -q -pl scloud-generator -am package -DskipTests
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/scloud-generator/target/scloud-generator-*.jar app.jar
EXPOSE 9204
ENTRYPOINT ["java","-jar","/app/app.jar"]

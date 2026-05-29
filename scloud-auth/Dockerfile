FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY . .
RUN mvn -q -pl scloud-auth -am package -DskipTests
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/scloud-auth/target/scloud-auth-*.jar app.jar
EXPOSE 9201
ENTRYPOINT ["java","-jar","/app/app.jar"]

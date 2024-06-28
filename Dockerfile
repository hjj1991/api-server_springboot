FROM arm64v8/openjdk:21-slim
COPY build/libs/api-server-plain.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

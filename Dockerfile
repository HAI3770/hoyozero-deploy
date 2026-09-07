FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Build the API from source so a fresh clone can be started with Docker Compose.
COPY pom.xml ./
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q -DskipTests package

FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/library/eclipse-temurin:17-jre
WORKDIR /app
RUN apt-get update && apt-get install -y --no-install-recommends docker.io git ca-certificates && rm -rf /var/lib/apt/lists/*
COPY --from=build /build/target/hoyozero-deploy-1.0.0.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS="-Xms256m -Xmx768m"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]

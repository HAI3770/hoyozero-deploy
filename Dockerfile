FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/maven:3.9-eclipse-temurin-17 AS backend-build
WORKDIR /src
COPY pom.xml ./
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B -Dmaven.test.skip=true package

FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/library/eclipse-temurin:17-jre
WORKDIR /app
COPY --from=backend-build /src/target/hoyozero-deploy-1.0.0.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS="-Xms256m -Xmx768m"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]

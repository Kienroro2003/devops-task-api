FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -DskipTests dependency:go-offline

COPY src src
RUN ./mvnw -B -DskipTests clean package

FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

RUN groupadd --system app \
    && useradd --system --gid app --home-dir /app app \
    && mkdir -p /data \
    && chown -R app:app /app /data

COPY --from=build --chown=app:app /workspace/target/devops-task-api-*.jar /app/app.jar

USER app
EXPOSE 8080
VOLUME ["/data"]

ENV DB_URL="jdbc:h2:file:/data/tasksdb;DB_CLOSE_ON_EXIT=FALSE"

HEALTHCHECK --interval=10s --timeout=3s --start-period=20s --retries=5 \
    CMD bash -c 'exec 3<>/dev/tcp/127.0.0.1/8080 && printf "GET /actuator/health HTTP/1.0\r\n\r\n" >&3 && grep -q "\"status\":\"UP\"" <&3'

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

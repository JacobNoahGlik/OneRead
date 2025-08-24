# Build stage
FROM gradle:8.7-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle :server:shadowJar --no-daemon


# Run stage (distroless JVM)
FROM gcr.io/distroless/java21:nonroot
WORKDIR /srv
COPY --from=build /app/server/build/libs/oneread-server-all.jar ./app.jar
# Optional: mount /srv for keyset and sqlite db
EXPOSE 8080
USER nonroot
ENTRYPOINT ["/usr/lib/jvm/java-21-openjdk/bin/java", "-jar", "app.jar"]

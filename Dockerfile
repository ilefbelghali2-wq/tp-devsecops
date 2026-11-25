FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY target/democyber-0.0.1-SNAPSHOT.jar app.jar

# Utilisateur non-root (principe du moindre privilège)
RUN useradd -m appuser
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
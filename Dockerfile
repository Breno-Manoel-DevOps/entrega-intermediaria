# ── Etapa 1: Build com Maven ──────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copia o pom.xml primeiro (aproveita cache do Docker nas dependências)
COPY pom.xml .
RUN mvn dependency:go-offline --no-transfer-progress

# Copia o código e compila (pula testes — CI já rodou)
COPY src ./src
RUN mvn package -DskipTests --no-transfer-progress

# ── Etapa 2: Imagem final leve (só JRE) ──────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Porta exposta (Render injeta a variável PORT automaticamente)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=${PORT:8080}"]

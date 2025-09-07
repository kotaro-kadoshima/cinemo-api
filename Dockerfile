#---- build stage ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

#依存を先に落としてキャッシュ最適化
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

#ソースをコピーしてビルド
COPY src ./src
RUN mvn -q -DskipTests package

#---- run stage ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

#fat jar を配置（*jar でOK）
COPY --from=build /app/target/*jar /app/app.jar

#Fargateで安定稼働するためのJVM設定（最小）
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"
ENV SERVER_PORT=8080
EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
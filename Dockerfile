FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

# ✅ ADD THIS LINE (important fix)
RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests

CMD ["java", "-jar", "target/*.jar"]

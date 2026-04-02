FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

# Give permission to mvnw
RUN chmod +x mvnw

# Build the project
RUN ./mvnw clean package -DskipTests

# Run the generated jar
CMD ["java", "-jar", "target/Asset_Management-0.0.1-SNAPSHOT.jar"]
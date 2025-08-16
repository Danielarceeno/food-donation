# Estágio 1: Build da aplicação com Maven (usa um JDK completo)
FROM eclipse-temurin:17-jdk-jammy as builder
WORKDIR /app

# Copia os arquivos de definição do Maven e baixa as dependências
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

# Copia o código-fonte e executa o build para criar o .jar
COPY src ./src
RUN ./mvnw package -DskipTests

# Estágio 2: Imagem final (usa apenas o Java Runtime, que é menor)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copia o .jar que foi criado no estágio 'builder'
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta e define o comando para iniciar a aplicação
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

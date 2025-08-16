# Usa JDK 17 leve
FROM openjdk:17-alpine

# Diretório de trabalho
WORKDIR /app

# Copia o jar empacotado para dentro da imagem
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expõe a porta que o Spring Boot utiliza
EXPOSE 8080

# Comando de inicialização
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

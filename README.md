# task-manager-back

Este projeto é desenvolvido utilizando **Java 17** e inclui uma configuração para um ambiente de banco de dados MySQL via Docker Compose.

## Configuração do Banco de Dados

Na pasta `config`, você encontrará:

- **`docker-compose.yml`**: Um arquivo de configuração que define um serviço MySQL.
  - Caso você execute o comando:
    ```sh
    docker-compose up -d
    ```
    O banco de dados será iniciado automaticamente.
- **Script SQL**: Também disponível na pasta `config`, caso prefira criar o banco manualmente.
  - Para executar o script, utilize um cliente MySQL ou a linha de comando:
    ```sh
    mysql -u root -p < config/script_create_database.sql
    ```

## Como Executar o Projeto

1. Certifique-se de ter **Java 17** instalado.
2. Execute o Docker Compose ou rode o script SQL manualmente.
3. Compile e execute o projeto:
    ```sh
    mvn clean install
    mvn spring-boot:run
    ```

## Documentação dos Endpoints

Para acessar a documentação dos endpoints via Swagger, utilize os seguintes links após iniciar o projeto:

- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)



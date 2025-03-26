package com.function;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public class CrearUsuarioFunction {

        @FunctionName("CrearUsuario")
        public HttpResponseMessage run(
                        @HttpTrigger(name = "req", methods = {
                                        HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
                        final ExecutionContext context) {

                context.getLogger().info("Procesando solicitud para crear usuario.");

                // Se espera que el cuerpo contenga "nombre,email"
                String requestBody = request.getBody().orElse("").trim();

                if (requestBody.isEmpty()) {
                        return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                                        .body("{\"error\":\"Por favor, proporciona los datos del usuario en el cuerpo de la solicitud en formato 'nombre,email'.\"}")
                                        .header("Content-Type", "application/json")
                                        .build();
                }

                // Parsear los datos del usuario
                String[] parts = requestBody.split(",");
                if (parts.length < 2) {
                        return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                                        .body("{\"error\":\"Formato incorrecto. Se espera 'nombre,email'.\"}")
                                        .header("Content-Type", "application/json")
                                        .build();
                }
                String nombre = parts[0].trim();
                String email = parts[1].trim();

                // Conexión a la base de datos usando JDBC
                String dbUrl = System.getenv("DB_URL");
                String dbUser = System.getenv("DB_USER");
                String dbPassword = System.getenv("DB_PASSWORD");

                String responseMessage;
                try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                        String sql = "INSERT INTO usuarios (nombre, email) VALUES (?, ?)";
                        try (PreparedStatement ps = connection.prepareStatement(sql)) {
                                ps.setString(1, nombre);
                                ps.setString(2, email);
                                int rowsAffected = ps.executeUpdate();
                                if (rowsAffected > 0) {
                                        responseMessage = "{\"mensaje\":\"Usuario creado exitosamente\", \"nombre\":\""
                                                        + nombre + "\", \"email\":\"" + email + "\"}";
                                } else {
                                        responseMessage = "{\"error\":\"No se pudo crear el usuario.\"}";
                                }
                        }
                } catch (SQLException e) {
                        context.getLogger().severe("Error SQL: " + e.getMessage());
                        responseMessage = "{\"error\":\"Error al crear el usuario: " + e.getMessage() + "\"}";
                        return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(responseMessage)
                                        .header("Content-Type", "application/json")
                                        .build();
                }

                return request.createResponseBuilder(HttpStatus.OK)
                                .body(responseMessage)
                                .header("Content-Type", "application/json")
                                .build();
        }
}

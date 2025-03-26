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

public class AsignarRolFunction {

    @FunctionName("AsignarRol")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Procesando solicitud para asignar rol.");

        // Obtener parámetros de consulta
        String userId = request.getQueryParameters().get("userId");
        String rolId = request.getQueryParameters().get("rolId");

        // Intentar obtener los valores desde el cuerpo si no están en la query
        Optional<String> bodyOptional = request.getBody();
        if ((userId == null || rolId == null) && bodyOptional.isPresent()) {
            String body = bodyOptional.get().trim();
            // Se asume formato: "userId,rolId"
            String[] parts = body.split(",");
            if (parts.length >= 2) {
                if (userId == null || userId.isEmpty()) {
                    userId = parts[0].trim();
                }
                if (rolId == null || rolId.isEmpty()) {
                    rolId = parts[1].trim();
                }
            }
        }

        if (userId == null || rolId == null || userId.isEmpty() || rolId.isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"Por favor, proporciona 'userId' y 'rolId' en la consulta o en el cuerpo.\"}")
                    .header("Content-Type", "application/json")
                    .build();
        }

        // Conexión a la base de datos usando JDBC
        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");

        String responseMessage;
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            String sql = "INSERT INTO usuario_roles (usuario_id, rol_id) VALUES (?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, Integer.parseInt(userId));
                ps.setInt(2, Integer.parseInt(rolId));
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    responseMessage = "{\"mensaje\":\"Rol asignado exitosamente\", \"userId\":\"" + userId
                            + "\", \"rolId\":\"" + rolId + "\"}";
                } else {
                    responseMessage = "{\"error\":\"No se pudo asignar el rol.\"}";
                }
            }
        } catch (SQLException e) {
            context.getLogger().severe("Error SQL: " + e.getMessage());
            responseMessage = "{\"error\":\"Error al asignar el rol: " + e.getMessage() + "\"}";
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseMessage)
                    .header("Content-Type", "application/json")
                    .build();
        } catch (NumberFormatException nfe) {
            responseMessage = "{\"error\":\"Los parámetros 'userId' y 'rolId' deben ser numéricos.\"}";
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
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

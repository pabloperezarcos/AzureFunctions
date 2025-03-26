package com.function;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;

public class AsignarRolFunction {

    @FunctionName("AsignarRol")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = { HttpMethod.POST },
                    authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Procesando solicitud para asignar rol.");

        String userId = request.getQueryParameters().get("userId");
        String rolId = request.getQueryParameters().get("rolId");

        Optional<String> bodyOptional = request.getBody();
        if ((userId == null || rolId == null) && bodyOptional.isPresent()) {
            String body = bodyOptional.get().trim();

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

        String responseMessage = "{\"mensaje\":\"Rol asignado exitosamente\", \"userId\":\"" + userId
                + "\", \"rolId\":\"" + rolId + "\"}";

        return request.createResponseBuilder(HttpStatus.OK)
                .body(responseMessage)
                .header("Content-Type", "application/json")
                .build();
    }
}

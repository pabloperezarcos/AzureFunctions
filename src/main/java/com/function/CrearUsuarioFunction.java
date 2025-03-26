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

public class CrearUsuarioFunction {

    @FunctionName("CrearUsuario")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Procesando solicitud para crear usuario.");

        String requestBody = request.getBody().orElse("").trim();

        if (requestBody.isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"Por favor, proporciona los datos del usuario en el cuerpo de la solicitud.\"}")
                    .header("Content-Type", "application/json")
                    .build();
        }

        String responseMessage = "{\"mensaje\":\"Usuario creado exitosamente\", \"usuario\":\"" + requestBody + "\"}";

        return request.createResponseBuilder(HttpStatus.OK)
                .body(responseMessage)
                .header("Content-Type", "application/json")
                .build();
    }
}

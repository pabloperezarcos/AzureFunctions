# AzureFunctions - Funciones Serverless para Sistema Veterinario

## Descripción del Proyecto
Este proyecto contiene las funciones serverless implementadas en Java, diseñadas para gestionar las operaciones de "Crear Usuario" y "Asignar Rol" en el sistema veterinario. Las funciones están alojadas en **Microsoft Azure**, donde se despliegan como parte de una Azure Function App, y se conectan a una base de datos Oracle mediante JDBC para realizar las operaciones CRUD.

## Funcionalidades Clave
- **Crear Usuario (CrearUsuarioFunction):**  
  Recibe datos del usuario en el cuerpo de la solicitud (formato "nombre,email") y realiza una inserción en la tabla `usuarios` de Oracle.
  
- **Asignar Rol (AsignarRolFunction):**  
  Recibe `userId` y `rolId` (vía query o en el cuerpo de la solicitud) para asignar un rol a un usuario, insertando la relación en la tabla `usuario_roles`.

- Las funciones devuelven respuestas en formato JSON y gestionan errores y validaciones.

## Requisitos
- Java 17
- Maven
- Azure Functions Core Tools
- Oracle JDBC Driver (incluido en el `pom.xml`)
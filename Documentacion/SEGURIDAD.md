# Arquitectura de seguridad

Proyecto académico de gestión de chefs, clientes, administradores, noticias y servicios, centrado en JWT, autorización y protección de credenciales.

## Autenticación

El registro público permite crear únicamente clientes o chefs. El servidor asigna el rol e ignora valores de rol o estado de bloqueo recibidos en la petición. Las contraseñas se guardan con BCrypt.

Tras iniciar sesión mediante POST /login, Spring Security emite un JWT HS256 con caducidad. Cada petición privada exige Authorization: Bearer TOKEN. El filtro valida la firma, la fecha de expiración y consulta en la base de datos si la cuenta sigue activa. Los usuarios baneados o anonimizados pierden acceso a las rutas privadas incluso si conservan un token antiguo.

La clave JWT se suministra como JWT_SECRET, codificada en Base64 y con 32 bytes aleatorios como mínimo. No existe ninguna clave real almacenada en el repositorio.

## Permisos por rol

| Ruta | Visitante | Cliente | Chef | Admin |
| --- | --- | --- | --- | --- |
| POST /login, POST /cliente, POST /chef | Sí | Sí | Sí | Sí |
| GET /chef, GET /chef/{id}, GET /chef/listadoActivos | Sí | Sí | Sí | Sí |
| GET /noticia, GET /noticia/{id} | Sí | Sí | Sí | Sí |
| GET /chef/miPerfil, PUT /chef, DELETE /chef | No | No | Sí | No |
| PUT /chef/activar, PUT /chef/desactivar | No | No | Sí | No |
| GET /cliente/miPerfil, PUT /cliente, DELETE /cliente | No | Sí | No | No |
| GET /cliente, GET /cliente/{id} | No | No | No | Sí |
| POST /servicio/contratar/{chefId} | No | Sí | No | No |
| PUT /servicio/puntuar/{servicioId}/{puntuacion} | No | Sí | No | No |
| GET /servicio/misServiciosCliente | No | Sí | No | No |
| GET /servicio/misServiciosChef | No | No | Sí | No |
| POST /noticia, PUT /noticia/{id}, DELETE /noticia/{id} | No | No | No | Sí |
| PUT /banear/{actorId}, PUT /desbanear/{actorId} | No | No | No | Sí |
| /admin y subrutas | No | No | No | Sí |

La API responde con 401 cuando falta autenticación válida y 403 cuando el usuario está autenticado pero no tiene el rol correcto.

## Contrataciones y valoraciones

Un chef debe estar activo y no baneado; la fecha solicitada debe ser posterior a la actual. Una transacción bloquea su registro mientras se comprueba que no tenga otro servicio ese día. El servidor fija el momento de contratación y no acepta valoraciones incluidas en la petición de creación.

El cliente solo puede puntuar un servicio que haya contratado, cuando su fecha ya haya pasado y si no está valorado previamente. La puntuación válida es de 1 a 5.

Limitación académica: no existe una confirmación explícita del servicio realizado ni integración de pagos. La condición de fecha pasada no garantiza por sí misma que el servicio se haya prestado.

## Configuración y privacidad

JWT_SECRET es obligatorio. Variables opcionales: JWT_EXPIRATION_MS, DB_URL, DB_USER, DB_PASSWORD, CORS_ALLOWED_ORIGINS, APP_ADMIN_USER y APP_ADMIN_PASSWORD. El administrador local se crea solamente cuando se facilitan las dos últimas variables; no se distribuye una cuenta administrativa predeterminada.

Al desactivar un perfil se sustituyen sus datos identificativos, se invalida el acceso y se conserva el historial de relaciones. Esto es una implementación educativa de anonimización, **no una garantía de cumplimiento integral del RGPD**.

La estructura de herencia JPA y la persistencia del enum de roles cambian respecto al simulacro inicial. Si ya existe una base de datos, se requiere una migración de datos planificada y una copia de seguridad previa. La configuración JPA automática es adecuada para desarrollo local, no para migraciones en producción.

## La demo de GitHub Pages

La carpeta docs/ contiene un **simulador independiente**, no un cliente del servidor real. Puede mostrar decisiones de acceso por rol, pero no verifica JWT ni ejecuta operaciones. Para comprobar las reglas reales es necesario ejecutar la API Java y sus pruebas.

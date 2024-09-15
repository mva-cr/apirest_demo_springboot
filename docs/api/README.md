# API (Interfaz de Programación de Aplicaciones)

[Retornar a la principal](../../README.md)

## API - API REST o API RESTful

Una **`API`** (Interfaz de Programación de Aplicaciones) es un conjunto de reglas y definiciones que permite que diferentes piezas de software interactúen entre sí. Funciona como un contrato entre un proveedor de información y un usuario, especificando la entrada y la salida que el usuario espera.

**`REST`** (Representational State Transfer) es un estilo de arquitectura de software para sistemas distribuidos como la World Wide Web. Las APIs que siguen los principios `REST`, conocidas como **`APIs REST`**, permiten la interacción con servicios web de manera sencilla y estándar sin tener que procesar grandes cantidades de datos o recursos del servidor. Los principios clave de las **`APIs REST`** incluyen:

1. **Uso de HTTP como protocolo de comunicación**, aprovechando sus métodos como GET, POST, PUT, DELETE, etc.
2. **Sin estado**: cada llamada HTTP contiene toda la información necesaria para ejecutarla. El servidor no almacena ningún estado sobre el cliente.
3. **Capaz de ser cacheable**: las respuestas deben definir explícitamente si son cacheables o no.
4. **Interfaz uniforme**: la uniformidad en la interfaz de comunicación entre componentes permite que el sistema sea más escalable y modificado independientemente.

Una **`API RESTful`** es una API que cumple estrictamente con los principios `REST`. No es una tecnología diferente de una `API REST`; más bien, "`RESTful`" describe el grado de cumplimiento de esos principios. Cuando una API adhiere completamente a todos los principios de `REST`, se describe como "`RESTful`". En realidad, todas las `APIs REST` deberían ser `RESTful`, pero el término se usa para enfatizar un cumplimiento estricto con REST.

## Principios de RESTful API

1. Stateless (Sin Estado):

Cada solicitud del cliente al servidor debe contener toda la información necesaria para entender y procesar la solicitud. El servidor no debe guardar ningún estado de la sesión del cliente entre solicitudes.

2. Uniform Interface (Interfaz Uniforme):

La API debe tener una interfaz uniforme que simplifique la interacción. Esto incluye el uso de URLs para recursos, y los métodos HTTP estándar como GET, POST, PUT y DELETE.

3. Client-Server Architecture (Arquitectura Cliente-Servidor):

La arquitectura se divide en clientes y servidores. Los clientes envían solicitudes y los servidores responden. Esta separación permite a los clientes y servidores evolucionar independientemente.

4. Stateless Communication (Comunicación Sin Estado):

La comunicación entre cliente y servidor es sin estado. Cada solicitud del cliente al servidor debe ser independiente y contener toda la información necesaria.

5. Cacheable (Cacheable):

Las respuestas deben ser explícitamente marcadas como cacheables o no cacheables. Esto permite mejorar la eficiencia al permitir que las respuestas se almacenen en caché.

6. Layered System (Sistema en Capas):

La arquitectura puede estar compuesta por múltiples capas, con cada capa solo interactuando con la capa adyacente. Esto ayuda a mejorar la escalabilidad y modularidad.

## Métodos HTTP Comunes en RESTful APIs

- GET: Recupera información de un recurso.
- POST: Crea un nuevo recurso.
- PUT: Actualiza un recurso existente.
- DELETE: Elimina un recurso.
- PATCH: Realiza una actualización parcial de un recurso.

## Representaciones

Los recursos pueden ser representados en diferentes formatos como JSON, XML, o HTML. La representación más común en las APIs RESTful es JSON debido a su simplicidad y facilidad de uso con JavaScript.

## Categorización de una API REST según su control de acceso

### API REST Pública (Open API)

Una **API REST pública** es accesible para cualquier persona que tenga la URL, sin necesidad de autenticación o autorización específica. Aunque es "pública", esto no implica que no tenga ningún control; puede tener, por ejemplo, limitaciones en el número de solicitudes que un usuario puede hacer (rate limiting) para prevenir el abuso. Sin embargo, la estructura de la API — tales como los endpoints, los métodos HTTP que utiliza (GET, POST, PUT, DELETE, etc.), y la manera en que los recursos son representados y transferidos — sigue los principios REST.

Ejemplos

- APIs de datos abiertos proporcionados por gobiernos o instituciones educativas.
- APIs de consulta de información meteorológica o de dominio público.

### API REST Privada o Segura

Una **API privada** o **API segura** requiere algún tipo de autenticación y autorización para acceder a ella. Esto generalmente se maneja mediante claves de API, tokens de autenticación (como JWT - JSON Web Tokens), OAuth, y otros mecanismos que controlan quién puede acceder a la API y qué operaciones están permitidas para cada usuario o aplicación. Estas APIs pueden estar más segmentadas con diferentes niveles de acceso dependiendo del rol o permisos del usuario autenticado.

Ejemplos

- APIs internas de una empresa que son utilizadas por diferentes departamentos pero no están expuestas al público.
- APIs que manejan datos sensibles o personales donde se requiere cumplir con regulaciones de privacidad como GDPR o HIPAA.
- APIs de servicios de pago, redes sociales o plataformas de comercio electrónico donde se requiere proteger la información del usuario y de la transacción.

## Categorización Basada en Seguridad y Acceso

- **APIs sin restricciones de acceso**: Cualquiera con la URL puede acceder. Estas se considerarían APIs públicas.
- **APIs con restricciones de acceso**: Requieren claves de acceso, autenticación de usuario, y posiblemente la gestión de roles y permisos para acceder o interactuar con ellas. Estas se clasifican como APIs seguras o privadas.

## Características REST en Ambos Tipos

Independientemente de si una API es pública o privada, para ser clasificada como RESTful, debe adherirse a los siguientes principios clave de REST:

1. **Comunicación sin estado**: Cada solicitud de un cliente a un servidor debe contener toda la información necesaria para entender y completar la solicitud. El servidor no debe almacenar ningún estado de la sesión del cliente entre peticiones.

2. **Separación entre cliente y servidor**: La interfaz REST separa las preocupaciones del cliente de las del almacenamiento de datos, lo que mejora la portabilidad del código del cliente a través de múltiples plataformas y la escalabilidad del servidor al simplificar los componentes del servidor.

3. **Cacheable**: Las respuestas deben, implícita o explícitamente, definir a sí mismas como cacheables o no, para prevenir que los clientes reutilicen datos inapropiadamente stale o incorrectos.

4. **Interfaz uniforme**: Para mantener una interfaz uniforme, todas las peticiones deben seguir los principios básicos de manejo de recursos, incluyendo manipulación de recursos a través de representaciones, mensajes autodescriptivos, y uso de hipermedios.

5. **Sistema de capas**: La arquitectura REST permite el uso de un sistema de capas que ayuda a organizar servidores, gateways, y otros intermediarios entre el cliente y los recursos del servidor.

## Comunicación Sin Estado

En el contexto de una **API REST**, "sin estado" significa que cada solicitud de un cliente al servidor debe contener toda la información necesaria para que el servidor comprenda y procese esa solicitud. Esto incluye autenticación y autorización si es necesario.

## Uso de Tokens

En APIs que requieren autenticación y autorización (como aquellas que utilizan tokens de acceso), el token actúa como una "llave de entrada". Este token es enviado por el cliente en cada petición, usualmente en el encabezado HTTP. el proceso es:

1. **Generación del Token**: Cuando un usuario se autentica correctamente (por ejemplo, mediante un nombre de usuario y contraseña), el servidor genera un token. Este token suele contener información codificada y segura sobre el usuario, como su identificador y cualquier permiso o roles.

2. **Envío del Token**: El cliente debe adjuntar este token a todas las solicitudes subsiguientes para acceder a recursos protegidos. Comúnmente, los tokens se envían en el encabezado de autorización de las solicitudes HTTP.

3. **Validación del Token**: Cada vez que el servidor recibe una solicitud con un token, valida este token para verificar que es válido y no ha expirado. La validación puede implicar verificar una firma digital, consultar una lista de tokens revocados, o comprobar la expiración del token, entre otras cosas.

4. **Procesamiento de la Solicitud**: Si el token es válido, el servidor procede a procesar la solicitud como de costumbre. Si el token no es válido (por ejemplo, si ha expirado o ha sido manipulado), el servidor rechaza la solicitud, típicamente con un código de estado HTTP como 401 Unauthorized o 403 Forbidden.

## Beneficios del Manejo Sin Estado

El manejo sin estado de las sesiones tiene varias ventajas, incluyendo:

- **Escalabilidad**: Como el servidor no necesita almacenar información de estado entre solicitudes, es más fácil escalar la aplicación añadiendo más servidores sin preocuparse por la sincronización del estado de la sesión.
- **Simplicidad**: La gestión de las solicitudes se simplifica, ya que cada una es independiente y contiene toda la información necesaria para su procesamiento.
- **Fiabilidad**: Reduce las dependencias entre solicitudes, lo que puede ayudar a mejorar la fiabilidad y la predictibilidad del comportamiento del servidor.
  Este enfoque es fundamental en el diseño de sistemas distribuidos modernos, especialmente aquellos que operan a gran escala en entornos como la nube, donde la capacidad de manejar grandes volúmenes de solicitudes de manera eficiente y segura es crítica.

## Tipos Comunes de Tokens

1. **Tokens de Sesión**: Guardados en algún lugar (como una base de datos o cache en memoria), pero esto iría en contra del principio sin estado de REST.
2. **JSON Web Tokens (JWT)**: Los más comunes para APIs REST sin estado. Contienen toda la información necesaria dentro del token mismo.

## Verificación de un JWT

Los JWT son especialmente populares porque son autónomos y contienen toda la información necesaria para verificar su validez. el proceso de verificación es:

1. Estructura del JWT: Un JWT está compuesto por tres partes: el encabezado, el payload y la firma.

- **Encabezado**: Generalmente contiene el tipo de token (JWT) y el algoritmo de hashing usado (como HS256).
  8 **Payload**: Contiene las afirmaciones (claims), que incluyen información sobre el usuario y metadatos adicionales, como la expiración (exp), emitido en (iat), etc.
- **Firma**: Un hash de las dos primeras partes del token, creado usando una clave secreta que solo el servidor conoce.

2. Proceso de Verificación:

- **Decodificación**: El servidor decodifica el JWT para obtener el encabezado y el payload.
- V**erificación de la Firma**: El servidor toma el encabezado y el payload, usa la misma clave secreta y el mismo algoritmo especificado en el encabezado para generar un nuevo hash. Si este hash coincide con la firma del token, el token es considerado auténtico.
- **Verificación de Claims**: El servidor verifica las afirmaciones dentro del payload. Esto incluye verificar la validez del token en términos de tiempo (por ejemplo, que no haya expirado) y que cualquier otra información de contexto (como los roles del usuario) sea correcta para la solicitud actual.

3. Contraste con Almacenamiento de Estado:

A diferencia de los sistemas que dependen de almacenar tokens o sesiones en el servidor, con JWT el servidor solo necesita la clave secreta para verificar cualquier token que reciba. Esto mantiene la API sin estado y elimina la necesidad de buscar y validar sesiones almacenadas.

## Seguridad

- **Almacenamiento de la Clave Secreta**: Es crucial que la clave secreta usada para firmar los tokens esté protegida y sea accesible solo por el servidor. Una violación de esta clave compromete todos los tokens.

- **Consideraciones de Seguridad Adicionales**: Aunque los JWT son eficaces, son susceptibles a ciertos ataques si no se manejan correctamente, como la no validación de la firma, reutilización de tokens expirados (si no se verifica la expiración), y otros ataques de interceptación si no se usan en conjunto con HTTPS.

## Manejo del Token del lado del Frontend

### Formas de manejar la seguridad del token:

1. **`Web Storage (LocalStorage o SessionStorage)`**

- **LocalStorage**: Permite almacenar datos que no tienen fecha de expiración. El token permanece almacenado incluso después de cerrar el navegador, lo cual puede ser útil pero potencialmente inseguro si el usuario comparte su dispositivo o no cierra sesión adecuadamente.
- **SessionStorage**: Similar a LocalStorage pero con alcance limitado a la ventana o pestaña del navegador. Los datos se eliminan cuando se cierra la pestaña o ventana, proporcionando un nivel más alto de seguridad en comparación con LocalStorage, pero aún susceptible a ataques de tipo Cross-Site Scripting (XSS).

2. **`Cookies`**

- **Cookies Seguras y HttpOnly**: Guardar el token en una cookie puede ser una opción segura si configuras adecuadamente las cookies. Usa la bandera HttpOnly para evitar que los scripts del lado del cliente accedan a la cookie, reduciendo el riesgo de ataques XSS. Además, la bandera Secure asegura que la cookie solo se envíe a través de conexiones HTTPS, protegiendo contra la interceptación del token a través de ataques man-in-the-middle.
- **SameSite Attribute**: Configura este atributo en tus cookies para restringir a terceros sitios enviar la cookie junto con las solicitudes, lo cual es útil para mitigar los ataques de Cross-Site Request Forgery (CSRF).

3. **`Token Management Libraries`**

- Utiliza bibliotecas de manejo de sesiones y tokens que ofrezcan buenas prácticas de seguridad integradas. Por ejemplo, bibliotecas que automaticen el almacenamiento seguro, renovación y rotación de tokens.

4. **`IndexedDB`**

- **IndexedDB**: Una base de datos en el navegador que permite almacenar grandes cantidades de datos estructurados, incluyendo archivos/blobs. Esta es más segura contra los ataques XSS en comparación con LocalStorage y SessionStorage, dado que los datos en IndexedDB no son accesibles mediante scripts de otros dominios.

### Mejores Prácticas y Consideraciones de Seguridad

- **Limitar la Vida del Token**: Utiliza tokens con una vida útil corta y mecanismos de renovación/actualización para limitar el daño potencial en caso de que el token sea comprometido.
- **Validación de Token**: Asegúrate de que cada solicitud que incluya el token sea validada en el servidor para verificar su integridad y autenticidad.
- **Uso de HTTPS**: Asegúrate de que todas las comunicaciones entre el cliente y el servidor se realicen sobre HTTPS para proteger los datos transmitidos, incluidos los tokens de autenticación, contra la interceptación.
- **Educación del Usuario**: Informa a los usuarios sobre la importancia de cerrar sesión, especialmente en dispositivos compartidos.

[Retornar a la principal](../../README.md)

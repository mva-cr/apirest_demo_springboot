# Mail

[Retornar a la principal](../../README.md)

Se implemanta el envío de correo utilizando la dependencia:

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

Para esto se agregan las variables en el ambiente de desarrollo así.

```
# Mail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.username={MAIL}
spring.mail.password={PasswordMail}
```

Para mayor detalle, visite [geeksforgeeks.org](https://www.geeksforgeeks.org/spring-boot-sending-email-via-smtp/).

## MimeMessage

Se implementó el uso de `MimeMessage` que permite el uso de formato enriquecido como `HTML`, imágenes embebidas, adjuntos, etc.

Para esto es necesario en este proyecto de plantillas `HTML` las que contienen

### Requerimiento

**Dependencia**

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>

```

**Internacionalizacion (opcional)**

Se puede agregar los mensajes de internacionalización en archivos de propiedades (messages.properties, messages_en.properties, etc.) dentro de la carpeta resources, ejemplo:

**`messages.properties`**: (idioma predefinido)

```
email.activation.title=Activación de Cuenta
email.activation.greeting=Hola, {0}!
email.activation.text1=Por favor, haz clic en el enlace a continuación para activar tu cuenta:
email.activation.text2=Saludos cordiales,
email.signature=El equipo de soporte
button.activation.text=Activar Cuenta
```

**`messages_en.properties`**

```
email.activation.title=Account Activation
email.activation.greeting=Hello, {0}!
email.activation.text1=Please click the link below to activate your account:
email.activation.text2=Best regards,
email.signature=The support team
button.activation.text=Activate Account
```

En **application.properties**

```
<!-- habilita el archivo -->
spring.thymeleaf.enabled=true
<!-- asegura que los cambios en la plantilla se reflejen sin tener que reiniciar la app -->
spring.thymeleaf.cache=false
 <!-- Establecer el archivo de propiedades para los mensajes (por defecto en español) -->
spring.messages.basename=i18n/messages
<!-- Establecer el español como el idioma por defecto -->
spring.mvc.locale=es
spring.mvc.locale-resolver=fixed
```

En `application-dev.properties` es necesario agregar;

```
<!-- tiempo en segundos durante el cual los archivos de mensajes estarán en caché -->
spring.messages.cache-duration=0
```

En `application-prod.properties` es necesario agregar;

```
<!-- tiempo en segundos durante el cual los archivos de mensajes estarán en caché -->
spring.messages.cache-duration=3600
```

### Expresiones Thymeleaf

1. **`#{}`** se utiliza para mensajes de internacionalización y
2. **`${}`** para variables del contexto, las que se generan en el MailService, ej.

```
context.setVariable("baseUrl", appUtility.getBaseUrl());
```

en el html se llama como ${baseUrl}

### Consideraciones importantes al usar CSS y Bootstrap en correos electrónicos:

**Clientes de correo y soporte CSS**:

- Clientes de correo electrónico como Gmail, Outlook, Yahoo Mail y otros tienen un soporte limitado para ciertas propiedades CSS.
- Estilos en línea son la opción más segura para garantizar compatibilidad entre clientes.

**CSS que funciona bien**:

- Propiedades de **tipografía** como font-family, font-size, y color funcionan bien.
- Propiedades de **espaciado** como padding, margin, y text-align son compatibles.
- Propiedades de **color** y **fondo** (color, background-color) son bien soportadas.

**Evitar**:

- **Hojas de estilo externas o CDNs**: No se cargarán correctamente en muchos clientes.
- Propiedades avanzadas de CSS como float, position, flex, o grid pueden no funcionar como esperas en correos electrónicos.

### Ventajas de utilizar plantillas HTML en resources:

1. Reutilización:

Puedes reutilizar la misma plantilla para diferentes correos electrónicos, simplemente pasando diferentes variables dinámicas (como el nombre del usuario, el enlace de activación, etc.).

2. Separación de lógica y presentación:

Mantener el contenido del correo separado de la lógica de negocio es una buena práctica, ya que facilita el mantenimiento y la actualización del contenido sin tocar el código Java.

3. Facilidad de personalización:

Puedes agregar diferentes idiomas o modificar el diseño visual de los correos sin necesidad de cambiar el código Java.
Los archivos CSS se pueden agregar y modificar en las plantillas.

4. Internacionalización (i18n):

La internacionalización es el proceso de diseñar una aplicación para que soporte múltiples idiomas sin necesidad de modificar el código principal. Utiliza archivos de propiedades (propiedades de idioma) para almacenar las traducciones de los textos, y dependiendo del idioma seleccionado, tu aplicación mostrará el texto correcto.

Utilizando un motor de plantillas como Thymeleaf, puedes integrar fácilmente soporte para múltiples idiomas con propiedades de internacionalización, como se muestra en tu ejemplo: th:text="#{email.activation.title}".

5. Mantenimiento:

Al mantener los correos en archivos de plantillas, puedes cambiar el contenido del correo sin tener que recompilar el proyecto.

### Funciones principales de `Thymeleaf` cuando se incluye en pom.xml:

1. **Procesamiento de plantillas dinámicas**: `Thymeleaf` permite procesar plantillas HTML en el lado del servidor, reemplazando marcadores de posición con contenido dinámico. Esto es útil para generar vistas HTML basadas en datos provenientes de la aplicación, como páginas web o correos electrónicos.

Ejemplo:

```{html}
Copiar código
<p th:text="'Hola, ' + ${user.login}">Hola, Usuario</p>
```

Aquí, la expresión `${user.login}` será reemplazada con el nombre del usuario en el momento en que se renderiza la plantilla.

2. **Soporte para correos electrónicos HTML**: Thymeleaf también permite generar correos electrónicos en formato HTML de forma dinámica. Puedes definir plantillas HTML dentro de la carpeta resources/templates y luego procesarlas en tiempo de ejecución, insertando valores dinámicos (como nombres de usuarios, enlaces, etc.).

3. **Internacionalización (i18n)**: Thymeleaf tiene soporte nativo para la internacionalización. Puedes definir archivos de propiedades (messages.properties, messages_es.properties, etc.) que contengan los textos traducidos en varios idiomas. Luego, usando Thymeleaf, puedes cambiar el contenido del correo o la página según el idioma del usuario.

Ejemplo de internacionalización en Thymeleaf:

```{html}
Copiar código
<p th:text="#{email.activation.greeting}">Saludos,</p>
```

4. **Soporte de fragmentos**: `Thymeleaf` permite la reutilización de componentes o fragmentos de HTML, lo que facilita la creación de plantillas más modulares y mantenibles. Esto es útil para correos electrónicos o vistas web que comparten partes comunes.

Ejemplo:

```{html}
Copiar código
<div th:replace="fragments/footer :: footer"></div>
```

5. **Expresiones naturales en HTML**: `Thymeleaf` está diseñado para que las plantillas HTML sean válidas en sí mismas, incluso cuando se ven en un navegador sin procesar. Usa atributos de etiquetas como th:text, th:href, th:src, etc., que se reconocen en los editores HTML, lo que permite ver las plantillas sin errores, incluso cuando no se han renderizado aún.

Ejemplo:

```{html}
Copiar código
<a th:href="@{http://example.com/activate?key=${activationKey}}">Activar cuenta</a>
```

6. **Soporte para `Spring Boot` y fácil integración**: La dependencia `spring-boot-starter-thymeleaf` configura automáticamente `Thymeleaf` en proyectos Spring Boot, lo que permite un uso inmediato sin la necesidad de configuraciones manuales adicionales. `Thymeleaf` se integra perfectamente con el modelo de vista-controlador (MVC) de Spring Boot, facilitando la generación dinámica de vistas.

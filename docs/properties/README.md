# Application.properties

[Retornar a la principal](../../README.md)

## Configuración de Perfiles en Spring Boot

1. **Definir Archivos de Propiedades o YML**

Se debe tener un conjunto de archivos de configuración para cada entorno:

- `application-dev.properties` (o `application-dev.yml`) para desarrollo.
- `application-prod.properties` (o `application-prod.yml`) para producción.
- `application.properties` (o `application.yml`) para configuraciones comunes.

2. Configurar los Archivos Específicos del Entorno

Cada archivo debe contener configuraciones específicas para ese entorno. Por ejemplo, conexiones a bases de datos, configuraciones de logging, parámetros de API externas, etc., que son diferentes entre desarrollo y producción.

3. Especificar el Perfil Activo

No especifiques el perfil activo en application.properties. En lugar de esto, usa métodos externos para definir el perfil activo para evitar errores de configuración que puedan exponer datos sensibles o afectar la disponibilidad.

## Activación de Perfiles de Forma Segura

En ambiente de desarrollo que se trabaja localmente si se utiliza Visual Studio Code, se puede gestionar de dos formas:

1. **Configuración de Variables de Entorno Localmente**:

Si estás en un entorno Unix-like (Linux, macOS), puedes configurar la variable temporalmente en tu terminal antes de iniciar tu aplicación:

```
export SPRING_PROFILES_ACTIVE=dev
```

Luego, simplemente ejecuta tu aplicación desde el mismo terminal.

2. **Configuración en Visual Studio Code**:

Una forma más permanente o específica para tu proyecto que no dependa de la configuración de tu terminal, puedes configurar las variables de entorno directamente en Visual Studio Code.

Crea un archivo .vscode/launch.json en tu proyecto si aún no existe.

Añade una configuración para Spring Boot con el perfil deseado:

```
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Launch App",
      "request": "launch",
      "mainClass": "com.example.DemoApplication",
      "projectName": "demoapplication",
      "args": "",
      "vmArgs": "-Dspring.profiles.active=dev"
    }
  ]
}
```

**Recomendación**

Para ambos entornos, es recomendable no hardcodear configuraciones sensibles y utilizar variables de entorno o servicios de gestión de configuración tanto como sea posible. Esto ayuda a mantener la seguridad de tu aplicación y facilita la gestión de diferentes entornos sin necesidad de cambiar el código.

Esta configuración permite que inicies tu aplicación directamente desde VS Code con el perfil dev activo.

## Consideraciones de Seguridad y Mejores Prácticas

- **Nunca hardcodees** las credenciales u otros datos sensibles en tus archivos de propiedades. Utiliza siempre secretos gestionados externamente o servicios de gestión de configuración.
- **Valida las configuraciones** en todos los entornos para asegurarte de que no haya fugas de datos ni configuraciones incorrectas.
- **Utiliza HTTPS** y otras técnicas de seguridad para proteger los datos en tránsito.
- **Audita y revisa regularmente** tus configuraciones y perfiles para adaptarte a cualquier cambio en los requisitos de seguridad o negocio.

**Hardcodear** es el término utilizado para describir la práctica de escribir datos fijos directamente en el código fuente de un programa. Este enfoque hace que los datos estén estáticamente incrustados en el código, lo que significa que cualquier cambio requerido en estos datos necesitará una modificación del código fuente y una recompilación y redistribución del software. ej.

```
String apiKey = "ABCD1234APIKEY";
```

## Problemas Asociados con Hardcodear

1. **Seguridad**: Hardcodear datos sensibles, como credenciales o claves API, puede exponer información crítica si el código es accesible por terceros, ya sea por repositorios de código no seguros o por distribución del software.

2. **Mantenibilidad**: Cambiar datos que están hardcodeados requiere modificar el código fuente, lo cual no es práctico. Esto reduce la flexibilidad y aumenta el tiempo necesario para mantener y actualizar el software.

3. **Reusabilidad**: El código hardcodeado es menos reutilizable, ya que está diseñado con datos específicos que quizás no sean aplicables en otros contextos o entornos.

4. **Pruebas**: Hardcodear puede dificultar las pruebas automatizadas, especialmente cuando se trata de probar el código en diferentes entornos o con diferentes configuraciones.

## Alternativas a Hardcodear

Para evitar los problemas asociados con hardcodear, puedes utilizar las siguientes prácticas:

1. **Variables de Entorno**: Almacena datos configurables y sensibles en variables de entorno. Esto mantiene estos datos fuera del código y facilita la configuración del software en diferentes entornos sin necesidad de cambiar el código.

```
String apiKey = System.getenv("API_KEY");
```

2. **Archivos de Configuración Externos**: Utiliza archivos de configuración externos (como properties, json, o yaml) para almacenar información configurable. Estos archivos pueden ser leídos por el programa en tiempo de ejecución.

```
{
    "apiUrl": "http://example.com/api/data"
}
```

3. **Servicios de Gestión de Configuración y Secretos**: Para aplicaciones empresariales o aplicaciones en la nube, utiliza servicios dedicados de gestión de configuraciones y secretos, como AWS Secrets Manager, Azure Key Vault o HashiCorp Vault.

## Uso de dotenv-java

Una de las bibliotecas más simples para integrar es dotenv-java. Proceso de configurarla y cómo usarla:

1. Agregar Dependencia

- Añade la dependencia de dotenv-java en tu archivo pom.xml si estás usando Maven:

```{xml}
<dependency>
  <groupId>io.github.cdimascio</groupId>
  <artifactId>dotenv-java</artifactId>
  <version>5.2.2</version>
</dependency>
```

O en tu `build.gradle` si usas Gradle:

```{gradle}
implementation 'io.github.cdimascio:dotenv-java:5.2.2'
```

2. Crear el Archivo `.env`

- Crea un archivo `.env` en el directorio raíz de tu proyecto (al mismo nivel que src):

```{makefile}
DB_USERNAME=miUsuario
DB_PASSWORD=miContraseña
```

3. Cargar Variables de Entorno en el Código

- Antes de que se cargue el contexto de Spring Boot, necesitas cargar las variables de entorno. Una forma de hacerlo es en el método `main` de tu aplicación:

```{java}
import io.github.cdimascio.dotenv.Dotenv;

public class MiAplicacion {
  public static void main(String[] args) {
    Dotenv dotenv = Dotenv.load();
    System.setProperty("spring.datasource.username", dotenv.get("DB_USERNAME"));
    System.setProperty("spring.datasource.password", dotenv.get("DB_PASSWORD"));

    SpringApplication.run(MiAplicacion.class, args);
  }
}
```

## Uso Manual

Si prefieres no usar una biblioteca externa, puedes cargar manualmente las variables desde .env usando Java puro, aunque esto requiere más código, con este método no se requiere :

1. Leer el Archivo .env y Establecer Variables

- Puedes crear un método que lea el archivo .env y establezca las variables de entorno:

En una Clase, por ejemplo Util se incluye estos métodos o en una exclusiva:

```{java}
  /**
    * Carga las variables de entorno desde un archivo .env situado en la raíz del proyecto.
    * Establece cada variable como una propiedad del sistema para que puedan ser utilizadas
    * por toda la aplicación.
    *
    * @throws IOException Si ocurre un error al leer el archivo .env.
    */
  public static void loadDotenv() throws IOException {
      Path envPath = Path.of(".env");
      try (Stream<String> lines = Files.lines(envPath)) {
        lines.filter(line -> line.contains("="))
          .forEach(line -> {
              int index = line.indexOf('=');
              String key = line.substring(0, index).trim();
              String value = line.substring(index + 1).trim();
              // Asegura que los valores con caracteres especiales sean manejados correctamente
              value = handleSpecialCharacters(value);
              System.setProperty(key, value);
          });
      }
  }

  /**
    * Carga las variables de entorno desde un archivo .env situado en la raíz del proyecto.
    * Establece cada variable como una propiedad del sistema para que puedan ser utilizadas
    * por toda la aplicación.
    *
    * @throws IOException Si ocurre un error al leer el archivo .env.
    */
  public static void loadDotenv() throws IOException {
    Path envPath = Path.of(".env");
    try (Stream<String> lines = Files.lines(envPath)) {
      lines.filter(line -> line.contains("="))
            .forEach(line -> {
                int index = line.indexOf('=');
                String key = line.substring(0, index).trim();
                String value = line.substring(index + 1).trim();
                // Asegura que los valores con caracteres especiales sean manejados correctamente
                value = handleSpecialCharacters(value);
                System.setProperty(key, value);
            });
    }
  }

  /**
    * Maneja caracteres especiales en los valores de las propiedades para asegurar que se procesen correctamente.
    *
    * @param value El valor original de la propiedad.
    * @return El valor modificado si es necesario.
    */
  private static String handleSpecialCharacters(String value) {
    // Esta función puede ser expandida para manejar otros casos especiales según sea necesario
    if (value.startsWith("\"") && value.endsWith("\"")) {
        value = value.substring(1, value.length() - 1);
    }
    return value;
  }
```

2. Llamar a `cargarEnv` o `loadDotenv` en el Método `main`

```{java}
public class MiAplicacion {
  public static void main(String[] args) throws IOException {
    try {
      // cargarEnv():
      loadDotenv();
      // Imagina que aquí inicia Spring Boot o cualquier otra aplicación que dependa de estas propiedades
    } catch (IOException e) {
      System.err.println("Failed to load .env file");
      e.printStackTrace();
    }
    SpringApplication.run(MiAplicacion.class, args);
  }
}
```

**Comparación de los Métodos**

1. **Método `cargarEnv()`**:

1. Lectura Completa: Este método utiliza Files.readAllLines, que carga todas las líneas del archivo en la memoria simultáneamente. Esto puede ser menos eficiente si el archivo .env es muy grande.
1. Manejo Simple de Strings: Divide cada línea basándose en el primer "=" encontrado y configura las propiedades del sistema si la línea contiene exactamente dos partes. No filtra las líneas antes de procesarlas, lo que podría incluir líneas sin "=".
1. Excepciones: Lanza una excepción IOException que debe ser manejada por el método llamador.

1. **Método `loadDotenv()`**:

1. Lectura Eficiente: Usa Files.lines, que retorna un Stream de líneas, permitiendo el manejo de cada línea conforme se lee del archivo, lo que es más eficiente en uso de memoria para archivos grandes.
1. Filtrado y Procesamiento Stream: Filtra líneas que contengan "=" antes de procesarlas y maneja la configuración de propiedades dentro de un bloque try con recursos, garantizando que el flujo del archivo se cierre adecuadamente al final del procesamiento.
1. Excepciones: Al igual que el primer método, lanza IOException.

## Explicación

1. **Filtrado y Procesamiento**: El método filtra las líneas para asegurarse de que contienen un signo igual (=), lo cual indica una declaración de variable. Divide la cadena en clave y valor en el primer =, trima ambos para eliminar espacios innecesarios.

2. **Manejo de Caracteres Especiales**: La función handleSpecialCharacters es un lugar donde puedes manejar cualquier caso especial relacionado con los valores de las propiedades. Por ejemplo, si tus valores están entrecomillados y deseas quitar las comillas antes de configurarlos.

3. **Uso de Propiedades**: Una vez que las variables son cargadas como propiedades del sistema, Spring Boot automáticamente las utilizará para reemplazar los placeholders como ${DB_USERNAME} en application-dev.properties.

## Cómo Funciona la Integración con Spring Boot

Cuando Spring Boot se inicia, sigue estos pasos para integrar las configuraciones:

1. **Carga de Archivos de Propiedades**: Spring Boot carga primero application.properties y luego carga cualquier archivo específico de perfil (por ejemplo, application-dev.properties) si se ha especificado un perfil activo.

2. **Sustitución de Placeholders**: Durante la carga de estos archivos, Spring Boot busca placeholders en el formato ${...} y los reemplaza con los valores correspondientes de las propiedades del sistema. Este proceso se realiza automáticamente gracias a la integración de Spring Boot con el entorno de la JVM, que incluye las propiedades del sistema.

3. **Uso de las Propiedades**: Una vez que las variables de entorno son sustituidas por los valores reales en las configuraciones, estas propiedades están listas para ser usadas en la aplicación, ya sea para configurar una conexión a la base de datos, autenticación, configuraciones de correo, etc.

## Ventajas de Este Método

1. **Seguridad Mejorada:** Al no incluir datos sensibles directamente en el control de versiones y al manejarlos fuera del código fuente, reduces el riesgo de exposición accidental de información sensible.

2. **Flexibilidad**: Puedes cambiar fácilmente la configuración de tu aplicación sin necesidad de recompilar o redeployar la aplicación, simplemente modificando las variables de entorno en el entorno de ejecución o actualizando el archivo `.env`.

3. **Consistencia**: Este método garantiza que todas las instancias de tu aplicación, independientemente del entorno en el que se ejecuten, manejen la configuración de la misma manera, lo que ayuda a evitar errores comunes causados por configuraciones inconsistentes entre diferentes entornos.

## Consideraciones de Seguridad

- Asegúrate de agregar `.env` a tu `.gitignore` para evitar que se suba al control de versiones.
- Verifica regularmente que no se esté exponiendo accidentalmente información sensible, especialmente al mover o copiar archivos en tu proyecto.

Usar archivos `.env` en Spring Boot puede requerir un poco de configuración extra, pero es una forma efectiva de manejar configuraciones sensibles y específicas del entorno fuera del código y del control de versiones.

## Generar clave secreta

Utilizando una terminal para que se genere en una txt de la carpeta donde está ubicado:

```{teminal}
openssl rand -base64 64 > jwt_secret.txt
```

Este comando realiza lo siguiente:

1. `openssl rand -base64 64`: Genera una clave aleatoria y la codifica en base64. El número `64` aquí significa que `openssl` generará 48 bytes de datos aleatorios, que luego codifica en base64 para que sea más fácil de manejar en textos y configuraciones. El resultado será aproximadamente 64 caracteres de longitud en formato base64, lo que es adecuado para una clave secreta segura.
2. `> jwt_secret.txt`: Redirecciona la salida del comando `openssl` a un archivo llamado `jwt_secret.txt` en tu directorio actual.

## Metodo alternativo

Se generan tres archivos `.properties` uno para desarrollo y otro para producción, en el de desarrollo se agregan las claves pero se debe asegurar que este se agrega a `.gitignore` para que no quede expuesto en el repositorio.

**`application.properties`**

El que se ejecuta es `application.properties` en este se agrega la línea que indica cuál debe ejecutar, cuando está en desarrollo debe apuntar al de desarrollo y al hacer el deploy se debe apuntar al de produccion. En este se agregan las condiciones comunes y no sensibles, ej.

```
spring.application.name=sugef_test_springboot_b
# Configuraciones comunes
spring.jpa.show-sql=true

# Establece el perfil activo por defecto a 'dev'
spring.profiles.active=dev

# Zona horaria
spring.jpa.properties.hibernate.jdbc.time_zone=UTC
```

**`application-dev.properties`**

Este aplica en desarrollo con las claves necesaria.

**`application-prod.properties`**

Este aplica en desarrollo con las claves necesaria. ej.

```
# Configuración de la base de datos de producción
spring.datasource.url=jdbc:mysql://direccion_servidor_produccion:3306/mi_base_de_datos_prod?useSSL=true&requireSSL=true
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=validate

# Seguridad y JWT
app.jwtSecret=${JWT_SECRET}

# Configuración específica de producción
server.port=80
```

Al utilizar esta estratégia no se requiere las dependencias de `dotenv` ni el archivo `.env`

Se agrega a `.gitignore`

```
application-dev.properties
```

## Valores de spring.jpa.hibernate.ddl-auto

Esta propiedad puede tomar varios valores, cada uno con un comportamiento diferente:

1. `none`: No se realiza ninguna acción sobre el esquema de la base de datos.
2. `validate`: Hibernate valida que el esquema de la base de datos coincida con los mapeos de las entidades. Si hay diferencias, se lanzará una excepción.
3. `update`: Hibernate actualiza el esquema de la base de datos según los mapeos de las entidades, haciendo cambios incrementales para ajustar el esquema existente según sea necesario sin eliminar los datos existentes.
4. `create`: Hibernate crea el esquema de la base de datos al iniciar la aplicación, destruyéndolo al final de la sesión.
5. `create-drop`: Hibernate crea el esquema al iniciar la sesión y lo elimina al finalizar la sesión.

## Uso de validate

El valor validate es particularmente útil en entornos de producción o en cualquier entorno donde no desees que Hibernate realice cambios automáticamente en el esquema de la base de datos. Aquí están los detalles de cómo funciona y cuándo deberías usarlo:

- **Validación de Esquema**: Al usar validate, Hibernate simplemente verifica que el esquema actual de la base de datos coincida con el mapeo de las entidades definidas en tu aplicación. No realiza cambios en la base de datos, solo lee la estructura del esquema y la compara con tus mapeos.
- **Detección de Inconsistencias**: Si se detecta alguna discrepancia entre el esquema de la base de datos y los mapeos de las entidades (por ejemplo, una columna faltante, un tipo de dato incorrecto, etc.), Hibernate lanzará una excepción al arrancar la aplicación, lo cual es útil para capturar problemas de configuración antes de que la aplicación entre en operación.

## Recomendaciones para el Uso de validate

1. **Entornos de Producción**: Es recomendable usar validate en producción para evitar cambios no deseados o accidentales en el esquema de la base de datos que podrían afectar la integridad de los datos existentes.
2. **Desarrollo y Pruebas**: En desarrollo, puedes preferir usar update o incluso create-drop para facilitar el desarrollo y las pruebas, permitiendo que Hibernate maneje dinámicamente el esquema de la base de datos conforme cambian tus entidades.

[Retornar a la principal](../../README.md)

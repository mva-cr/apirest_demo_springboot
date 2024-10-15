# Aspectos técnicos de la base de datos

[Retornar a la principal](../../README.md)

## Implementación de dos bases de datos

Este proyecto implementa dos bases de datos para mantener separada la lógica de los usuarios que se conectan a la aplicación y la otra para la lógica de negocio que se está proporcionando, estas bases en este ejemplo se llaman:

1. customer que contiene a los usuarios clientes de la aplicación
2. trade, contiene lo lógica del negocio que se ofrece a los clientes

Ambas bases se generaron en `SQL Server` usando puertos diferente, simulando que se pueden ubicar en diferentes fuentes, una usa el `port: 1433` y la otra el `port 1435`

para esto es necesario agregar, en el caso de este proyecto que utiliza archivos `.properties` lo siguiente:

```
# Configuración de la base de datos de producción
spring.datasource.customer.jdbc-url=jdbc:sqlserver://localhost:1435;databaseName=customer;encrypt=true;trustServerCertificate=true
spring.datasource.customer.username=${DB_USERNAME}
spring.datasource.customer.password=${DB_PASSWORD}
spring.datasource.customer.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.jpa.customer.hibernate.dialect=org.hibernate.dialect.SQLServer2012Dialect
# Configuración JPA para la base de datos customer
# y asegurar que Hibernate respete la configuración específica
spring.jpa.customer.hibernate.ddl-auto=validate


# Base de datos 2 (business)
spring.datasource.business.jdbc-url=jdbc:sqlserver://localhost:1433;databaseName=trade;encrypt=true;trustServerCertificate=true
spring.datasource.business.username=${DB_USERNAME2}
spring.datasource.business.password=${DB_PASSWORD2}
spring.datasource.business.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.jpa.business.hibernate.dialect=org.hibernate.dialect.SQLServer2012Dialect

# Configuración JPA para la base de datos trade
# y asegurar que Hibernate respete la configuración específica
spring.jpa.business.hibernate.ddl-auto=validate
```

En la ruta `com.mvanalytic.apirest_demo_springboot.security.config.datasource` se crearon los archivos de configuración, en el caso de la base de negocio:

```

package com.mvanalytic.apirest_demo_springboot.security.config.datasource;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.mvanalytic.apirest_demo_springboot.repositories.business", entityManagerFactoryRef = "businessEntityManagerFactory", transactionManagerRef = "businessTransactionManager")
public class BusinessDataSourceConfig {

  @Bean(name = "businessDataSource")
  @ConfigurationProperties(prefix = "spring.datasource.business")
  public DataSource businessDataSource() {
    return DataSourceBuilder.create()
        .type(com.zaxxer.hikari.HikariDataSource.class)
        .build();
  }

  @Bean(name = "businessEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean businessEntityManagerFactory(
      EntityManagerFactoryBuilder builder,
      @Qualifier("businessDataSource") DataSource dataSource) {
    return builder
        .dataSource(dataSource)
        .packages("com.mvanalytic.apirest_demo_springboot.domain.business")
        .persistenceUnit("business")
        .build(); // No es necesario agregar el dialecto aquí
  }

  @Bean(name = "businessTransactionManager")
  public PlatformTransactionManager businessTransactionManager(
      @Qualifier("businessEntityManagerFactory") LocalContainerEntityManagerFactoryBean businessEntityManagerFactory) {

    EntityManagerFactory entityManagerFactory = businessEntityManagerFactory.getObject();
    if (entityManagerFactory == null) {
      throw new IllegalStateException("Business EntityManagerFactory is null");
    }

    return new JpaTransactionManager(entityManagerFactory);
  }
}
```

Y para la base de usuarios de la applicación:

```

package com.mvanalytic.apirest_demo_springboot.security.config.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = "com.mvanalytic.apirest_demo_springboot.repositories.user", entityManagerFactoryRef = "customerEntityManagerFactory", transactionManagerRef = "customerTransactionManager")
public class CustomerDataSourceConfig {

  @Primary
  @Bean(name = "customerDataSource")
  @ConfigurationProperties(prefix = "spring.datasource.customer")
  public DataSource customerDataSource() {
    return DataSourceBuilder.create()
        .type(com.zaxxer.hikari.HikariDataSource.class) // Asegura que HikariCP es el pool de conexiones
        .build();
  }

  @Primary
  @Bean(name = "customerEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean customerEntityManagerFactory(
      EntityManagerFactoryBuilder builder,
      @Qualifier("customerDataSource") DataSource dataSource) {
    return builder
        .dataSource(dataSource)
        .packages("com.mvanalytic.apirest_demo_springboot.domain.user") // Cambia a tu paquete correcto
        .persistenceUnit("customer")
        .build(); // No es necesario agregar el dialecto aquí
  }

  @Primary
  @Bean(name = "customerTransactionManager")
  public PlatformTransactionManager customerTransactionManager(
      @Qualifier("customerEntityManagerFactory") LocalContainerEntityManagerFactoryBean customerEntityManagerFactory) {
    EntityManagerFactory entityManagerFactory = customerEntityManagerFactory.getObject();
    if (entityManagerFactory == null) {
      throw new IllegalStateException("Customer EntityManagerFactory is null");
    }

    return new JpaTransactionManager(entityManagerFactory);
  }

}
```

**Nota**: Los Entities deben estar creados en `filterChain` de la clase `SecurityConfig`

Adicionalmente se agrega las rutas permitidas en el, ej.

```
.requestMatchers("/api/users/**", "/api/user-bussiness/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
```

y la ruta del controller es:

```
@RequestMapping("/api/user-bussiness/client-type")
```

Para efectos de prueba si bien se implmentaron todas las `entities`, los `repositories` solo se implmentó un `service` para hacer la prueba y en este falta de implmentar los DTO's los mensajes de error y lo demás.

Finalmente, en el componente de arranque se agregó

```
// asegura que Spring escanee todos los componentes dentro del paquete
// com.mvanalytic.apirest_demo_springboot y sus subpaquetes, incluidos los
// repositorios.
@ComponentScan(basePackages = { "com.mvanalytic.apirest_demo_springboot" })
```

## Lógica de negocio implementada en la base de datos de los usuarios

### Asignación del role

#### Rol default

Mediente el trigger `trg_add_user_role` que es de tipo `AFTER INSERT` en la tabla `user_mva` agregar el rol `ROLE_USER`, rol que debe estar en la tabla `authority`.

Por lo tanto, en caso de querer asignar un rol diferente, este debe cambiado directamente por el usuario con `ROLE_ADMIN`.

#### Cambio de Roles

El cambio de roles se realiza por medio del cambio en el atributo `status` de la tabla `user_mva`.

Cuando un user se le cambia el status de `true` a `false` o la inversa mediante un trigger (`trg_update_user_authority`) de tipo `AFTER UPDATE` en la tabla `user_mva` se modifica el rol asignado a este user, si el nuevo `status` es `true` o `1` hace un update al rol pasando a `ROLE_USER`.

En cambio si se cambia a `false` o `0` el `status` se cambia a `ROLE_UNAUTHORIZE`, el cual no tiene privilegios, lo que equivale a deshabilitar y quitar los roles.

### Auditorias

Se han creado las tablas de auditoria

| Tabla origen   | Tabla para auditoría |    Trigger que se aplica |
| :------------- | :------------------- | -----------------------: |
| user_mva       | user_audit           |       trg_user_mva_audit |
| user_authority | user_authority_audit | trg_user_authority_audit |

**Otras tablas de auditoría**

Adicionalmente se han creado tablas para otro tipo de seguimiento, estas son:

| Nombre de tabla | Propósito                                                |
| :-------------- | :------------------------------------------------------- |
| login_attempt   | rastrea intentos de inicio de sesión fallidos y exitosos |
| user_session    | controla y audita sesiones activas y pasadas             |

### Actualizaciones parciales

Se crearon dos procedimientos almacenados (`Store Procedure`) que contiene parámetros opcionales con valores predeterminados de `NULL`. Esto permite que la función `COALESCE` actualice el campo solo si se proporciona un valor nuevo: de lo contrario, mantiene el actual. En realidad reescribe el que ya está en la base de datos, de forma que esto provoca que se active un trigger que esté vinculado con `UPDADE` de esta tabla, por lo que se han incorporados en estos, un condición de ejecutar el contenido del `trigger` solo si el valor ha cambiado, evitando así sobre carga de la base de datos.

Estos `Procedimiento` son:

| Nombre del procedimiento     | Qué modifica                                                               | Rol que lo puede hacer |
| :--------------------------- | :------------------------------------------------------------------------- | :--------------------- |
| sp_update_user_by_role_admin | Los parámetros: `activated` y `status`                                     | Administrador          |
| sp_update_user_by_role_user  | Parámetros: `first_name`, `last_name`, `second_last_name` y `language_key` | Usuario                |

**Ventajas**

1. **Flexibilidad**: Permite actualizaciones muy flexibles sin necesidad de escribir múltiples declaraciones UPDATE para diferentes combinaciones de campos.
2. **Mantenimiento**: Facilita la mantenimiento del código al tener un único punto de actualización.
   Este enfoque simplifica la gestión de actualizaciones en aplicaciones complejas donde los usuarios pueden necesitar actualizar diferentes conjuntos de campos en diferentes momentos.

ejemplo de la forma en que se debe enviar del frontend.

```
EXEC sp_update_user_by_role_admin 1, null, true
```

## Otros

Para el idioma se utiliza el código de idiomas según ISO-639-1 (2 letras). Por ejemplo:

1. Español: es
2. Inglés: en

## Tamaño Común de activationKey

UUID (32 caracteres hexadecimales + 4 guiones):

Un UUID estándar tiene 36 caracteres en total (32 hexadecimales y 4 guiones).

Ejemplo: 123e4567-e89b-12d3-a456-426614174000

Si usas UUID para activationKey, un tamaño de NVARCHAR(36) es adecuado.

## Manejo de errores en los procedimientos para una Base `SQL Server`

Se uitiliza `RAISERROR` esta es una función de `SQL Server` que se utiliza para generar un error y enviar un mensaje al cliente. Los parámetros 16 y 1 tienen significados específicos:

- `16`: Es el nivel de severidad del error. En `SQL Server`, los niveles de severidad del 11 al 16 son errores generados por el usuario y pueden ser corregidos por el usuario. El nivel 16 indica un error general de usuario o un error de solicitud incorrecta.
- `1`: Es el estado del error, que puede ser utilizado para categorizar los errores dentro de cada nivel de severidad. El estado puede ayudar a identificar exactamente en qué punto del procedimiento se encontraba cuando ocurrió el error, especialmente útil si se lanzan varios errores diferentes dentro del mismo procedimiento almacenado.

## Manejo de procedimiento antes de crearlo

Se utiliza un código como el siguiente ejemplo:

```
IF EXISTS (SELECT *
FROM sys.objects
WHERE object_id = OBJECT_ID(N'[dbo].[sp_change_password]') AND type in (N'P', N'PC'))
BEGIN
  DROP PROCEDURE [dbo].[sp_change_password]
END
GO
```

El fragmento de SQL que has anterior verifica la existencia de un objeto en la base de datos antes de intentar eliminarlo, y especifica ciertos tipos de objeto mediante los códigos `(N'P', N'PC')`. Aquí está el significado de estos valores:

- `P`: Representa un procedimiento almacenado regular. Es el tipo más común de procedimiento que contiene código T-SQL compilado y optimizado que se puede ejecutar repetidamente.

- `PC`: Representa un procedimiento almacenado compilado nativamente. Estos procedimientos son específicos de SQL Server y son compilados a código máquina, lo que significa que se ejecutan mucho más rápido que los procedimientos almacenados regulares. Se utilizan principalmente en aplicaciones de alto rendimiento y son parte de la funcionalidad de In-Memory OLTP de SQL Server.

El uso de `N` antes de las comillas indica que la cadena está siendo tratada como NVARCHAR, una representación de cadena que puede almacenar caracteres Unicode, lo cual es útil para la compatibilidad con múltiples idiomas y configuraciones regionales.

## Manejo de Trigger antes de crealo

Similar el caso anterior, en este ejemplo:

```
IF EXISTS (SELECT 1 FROM sys.triggers WHERE object_id = OBJECT_ID(N'[dbo].[trg_user_authority_audit]'))
BEGIN
    DROP TRIGGER [dbo].[trg_user_authority_audit];
END
GO
```

**Explicación**:

- `IF EXISTS`: Esta condición verifica la existencia del trigger especificado en la base de datos antes de intentar eliminarlo.
- `sys.triggers`: Es el catálogo de vistas en SQL Server que contiene información sobre los triggers. Se usa para buscar el trigger por su nombre.
- `OBJECT_ID`: Función que obtiene el ID del objeto para el nombre del trigger dado, facilitando la búsqueda exacta sin ambigüedad.
- `DROP TRIGGER`: Comando SQL que elimina el trigger especificado.

Este método es útil para evitar errores durante scripts de despliegue o mantenimiento que pueden ejecutarse en entornos donde el estado actual de la base de datos puede ser desconocido. Asegura que los scripts no fallarán si el trigger ya ha sido eliminado o si nunca fue creado.

[Retornar a la principal](../../README.md)

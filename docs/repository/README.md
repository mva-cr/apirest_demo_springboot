# Consultas Usando Spring Data JPA

[Retornar a la principal](../../README.md)

Spring Data JPA genera las consultas automáticamente basadas en los nombres de los métodos en los repositorio.

Para ejemplicar, tenenos esta `Entity`:

```
@Entity
@Table(name = "user_login_activity")
public class UserLoginActivity {

 @Id
 @Column(name = "id_session", length = 128)
 private String idSession;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "id_user", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_id_user_user_login_activity"))
 private User user;

 @Column(name = "session_time", columnDefinition = "DATETIME2", nullable = false)
 private Instant sessionTime;

 @Column(name = "ip_address", nullable = false, length = 50)
 private String ipAddress;

 @Column(name = "user_agent", length = 512)
 private String userAgent;

 // ('SUCCESS', 'FAILURE')
 @Column(name = "session_status", nullable = false, length = 50)
 private String sessionStatus;

 ... constructor y getters and seters
 }
```

Note que se utiliza la variable `sessionTime`, si tengo este `Repository` con la siguiente firma lanzará un error como este: `Caused by: org.springframework.data.mapping.PropertyReferenceException: No property 'startTime' found for type 'UserLoginActivity'` por el uso en el nombre de `StartTime`:

```
@Repository
public interface UserLoginActivityRepository extends JpaRepository<UserLoginActivity, String> {

 List<UserLoginActivity> findByUserIdAndStartTimeBetween(Long userId, Instant startDate, Instant endDate);
}
```

Spring Data JPA intenta descomponer el nombre del método para generar la consulta correspondiente. Busca propiedades en tu entidad que coincidan con los nombres indicados en el método. En este caso, está buscando una propiedad llamada startTime en tu entidad UserLoginActivity, pero esa propiedad no existe, ya que en tu entidad la propiedad es sessionTime.

**¿Por qué sucede?**

Spring Data JPA utiliza la estrategia de derivación de consultas por nombres de métodos. Esto significa que analiza el nombre del método y lo traduce a una consulta SQL. Cada parte del nombre del método corresponde a un campo o condición de la entidad:

- **findByUserId**: busca por el campo userId o una relación que contenga un User.
- **AndStartTimeBetween**: está buscando un campo llamado startTime en tu entidad para aplicar la condición BETWEEN con las fechas.

Dado que tu entidad tiene una propiedad sessionTime en lugar de startTime, Spring no puede generar la consulta correctamente y lanza el error: `No property 'startTime' found for type 'UserLoginActivity'`.

**Solución**

```
List<UserLoginActivity> findByUserIdAndSessionTimeBetween(Long userId, Instant startDate, Instant endDate);
```

Esto le indica a Spring Data JPA que utilice la propiedad sessionTime en lugar de startTime. Al hacerlo, Spring podrá generar la consulta correcta para este método.

## Cómo interpreta el nombre y cómo construye el query

Cuando utilizas un método con una convención de nombres específica en un repositorio de Spring Data JPA, como findByIpAddressAndSessionTimeBetween, Spring Data genera automáticamente una consulta basada en los atributos de la entidad y la operación solicitada. Aquí te explico cómo se traduce este nombre en una consulta SQL para SQL Server.

**Desglose del método findByIpAddressAndSessionTimeBetween**

1. `findBy`: indica que estás realizando una operación de búsqueda.
2. `IpAddress`: hace referencia a la propiedad ipAddress en la entidad UserLoginActivity.
3. `And`: une dos condiciones en la consulta.
4. `SessionTime`: hace referencia a la propiedad sessionTime en la entidad.
5. `Between`: especifica un rango de valores para la propiedad sessionTime.

**Traducción a SQL**

Dado que el método está buscando registros por dos condiciones (ipAddress y un rango de tiempo entre dos valores de sessionTime), la consulta SQL que se genera es equivalente a:

```sql
SELECT *
FROM user_login_activity
WHERE ip_address = ?
AND session_time BETWEEN ? AND ?
```

Componentes de la consulta:

1. `SELECT *`: selecciona todas las columnas de la tabla user_login_activity.
2. `FROM user_login_activity`: la tabla sobre la que se ejecuta la consulta es user_login_activity, que corresponde a la entidad UserLoginActivity.
3. `WHERE ip_address = ?`: filtra los registros donde la columna ip_address coincide con el valor que le pasaste como parámetro al método.
4. `AND session_time BETWEEN ? AND ?`: además, filtra los registros donde la columna session_time esté entre los valores startDate y endDate, que se pasaron al método como parámetros.

**Parámetros de la consulta:**
El primer `?` corresponde al valor de `ipAddress`.
Los otros dos `?` corresponden al valor de inicio (`startDate`) y al valor de fin (`endDate`) del rango de tiempo para sessionTime.

[Retornar a la principal](../../README.md)

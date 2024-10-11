# Paginación

[Retornar a la principal](../../README.md)

En SQL Server, la paginación de consultas se puede lograr usando las cláusulas `OFFSET`y `FETCH`. Estas cláusulas permiten que solo se devuelvan un subconjunto de filas, lo que es especialmente útil para la paginación. En tu caso, si necesitas la página 10 de una consulta donde cada página contiene 40 registros, SQL Server solo devolverá los registros comprendidos entre el número 401 y 440, en lugar de devolver todos los 1000 registros.

**Explicación de las cláusulas**:

1. `OFFSET`: Indica cuántas filas (registros) se deben omitir desde el inicio de la consulta. Por ejemplo, si deseas empezar desde el registro 401, el valor de `OFFSET`será 400.

2. `FETCH NEXT`: Indica cuántas filas se deben devolver después de aplicar el `OFFSET`. Si necesitas 40 registros por página, usarás `FETCH NEXT` 40 ROWS ONLY.

**Sintaxis SQL para paginación**:

```sql
SELECT columnas
FROM tabla
ORDER BY columna_orden
`OFFSET` 400 ROWS    -- Número de filas a omitir (página 10 de 40 registros por página)
`FETCH NEXT` 40 ROWS ONLY;  -- Número de filas a retornar
```

**Ejemplo completo**:
Imagina que tienes una tabla de `LoginAttempty` quieres obtener la página 10, con 40 registros por página, ordenados por attemptTimede forma descendente.

```sql
SELECT *
FROM LoginAttempt
ORDER BY attemptTime DESC
`OFFSET` 400 ROWS   -- Omitir las primeras 400 filas (10 páginas * 40 registros por página)
`FETCH NEXT` 40 ROWS ONLY;  -- Obtener los siguientes 40 registros (es decir, la página 10)
```

**¿Cómo funciona esto?**

- `OFFSET 400 ROWS` omite las primeras 400 filas, lo que corresponde a 10 páginas anteriores, dado que cada página contiene 40 registros (10 \* 40 = 400).
- `FETCH NEXT 40 ROWS ONLY` selecciona exactamente 40 registros después de omitir los primeros 400, es decir, los registros comprendidos entre el número 401 y 440.

**Traducción de este enfoque a la paginación en Java (Spring Data)**:
En tu servicio de Spring, al usar Pageable, el framework genera automáticamente una consulta similar. Por ejemplo, si haces la consulta para la página 10 con 40 registros por página, el código en Java sería:

```Java
Copiar código
Pageable pageable = PageRequest.of(9, 40, Sort.by("attemptTime").descending());
Page<LoginAttempt> page = loginAttemptRepository.findAll(pageable);
```

Este código hará que el framework genere una consulta con `OFFSET`y `FETCH NEXT`basándose en los valores que hayas configurado para pageNumbery pageSize. El marco

**Beneficios**:

1. **Rendimiento**: Solo se cargan en memoria los registros necesarios para la página solicitada, lo que hace la consulta mucho más liviana.
2. **Escalabilidad**: La paginación es fundamental cuando se trabaja con grandes volúmenes de datos. No necesitas cargar millas de registro
3. **Optimización**: Al usar `OFFSET`y `FETCH NEXT`, la consulta es manejada directamente por la base de datos, y no por el servidor de aplicaciones, lo que reduce la sobrecarga en el backend.

## Teoría de `Pageable` en Spring Data

`Pageable` es una interfaz en Spring Data que facilita la implementación de la paginación y el ordenamiento de consultas en bases de datos de manera eficiente. En lugar de cargar todos los registros y luego paginarlos en la aplicación, `Pageable` permite que la consulta a la base de datos se ejecute con parámetros que controlan cuántos registros deben devolverse y cómo deben estar ordenados.

### Conceptos clave: 1.

1. **`PageRequest`**: - Es una implementación de la interfaz `Pageable` que se usa para definir la página que se desea obtener, el tamaño de la página (cantidad de registros por página ) y la dirección de ordenamiento.

```java
Paginable paginable = PageRequest.of(pageNumber, pageSize, Sort.by("property").ascending());
```

2. **`Sort`**: - Permite especificar el criterio de ordenamiento, ya sea ascendente o descendente, basado en una o varias columnas de la tabla.

- Ejemplo:

```java
Sort sort = Sort.by("attemptTime").descending(); Pageable pageable = PageRequest.of(0, 20, sort);
```

3. `Page` y `Slice` :

- `Page`: Devuelve información adicional como el número total de páginas, el número total de elementos, la página actual, etc.
- `Slice`: Simi `Page`, pero no incluye información sobre el total de elementos, ideal cuando solo deseas saber si hay más datos disponibles.
  Ejemplo:

```Java
Page<LoginAttempt> attemptsPage = loginAttemptRepository.findByUserId(userId, pageable);
```

4. `JpaRepository` y `PagingAndSortingRepository`: Al extender estas interfaces en el repositorio, se obtienen métodos como `findAll(Pageable pageable)` para facilitar la paginación.

**Beneficios de usoPageable**

1. **Eficiencia** : Carga solo los datos necesarios para la página solicitada.
2. **Escalabilidad** : Mejora el rendimiento en grandes volúmenes de datos.
3. **Ordenamiento** : Puedes aplicar orden dinámica a las consultas.
4. **Simplicidad** : Spring Data simplifica la lógica de paginación generando consulta

**Ejemplo de implementación**

Supongamos que tienes una entidad `LoginAttempty` quieres implementar la paginación para listar los intentos de inicio de sesión:

**Repositorio**
Defina un repositorio que extienda `JpaRepositorypara` habilitar la funcionalidad de paginación.

```Java
@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {
    Page<LoginAttempt> findByUserId(Long userId, Pageable pageable);
}
```

**Servicio**

En el servicio, utilice Pageablepara implementar la lógica de paginación.

```Java
@Service
public class LoginAttemptService {

    @Autowired
    private LoginAttemptRepository loginAttemptRepository;

    public Page<LoginAttemptResponseDTO> findLoginAttemptsByUserId(Long userId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("attemptTime").descending());

        // Obtener una página de resultados
        Page<LoginAttempt> loginAttempts = loginAttemptRepository.findByUserId(userId, pageable);

        // Mapear los resultados a DTO
        return loginAttempts.map(LoginAttemptMapper::convertToLoginAttemptResponseDTO);
    }
}
```

**Controlador**

En el controlador, recibe los parámetros `pageNumber` y `pageSizemediante` `@RequestPa@RequestParam`.

```Java
@RestController
@RequestMapping("/api/login-attempts")
public class LoginAttemptController {

    @Autowired
    private LoginAttemptService loginAttemptService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<LoginAttemptResponseDTO>> getLoginAttemptsByUserId(
        @PathVariable Long userId,
        @RequestParam(defaultValue = "0") int pageNumber,  // Página por defecto 0
        @RequestParam(defaultValue = "10") int pageSize    // Tamaño por defecto 10 registros
    ) {
        Page<LoginAttemptResponseDTO> loginAttempts = loginAttemptService.findLoginAttemptsByUserId(userId, pageNumber, pageSize);
        return ResponseEntity.ok(loginAttempts);
    }
}
```

**Ejemplo de consulta SQL optimizada con paginación**

En SQL Server, al usar paginación, se traducirá en una consulta que utiliza `OFFSET` y `FETCH NEXT`. Por ejemplo, si quieres obtener la página 10 con 40 registros por página:

```sql
SELECT * FROM LoginAttempt
WHERE userId = ?
ORDER BY attemptTime DESC
OFFSET 400 ROWS
FETCH NEXT 40 ROWS ONLY;
```

Esto significa que, en lugar de cargar todos los resultados, la base de datos solo devolverá los registros 401-440.

### Paginación en el Frontend

**HTML con Angular Material**

```html
<mat-table [dataSource]="loginAttempts">
  <!-- Definir columnas -->
</mat-table>

<mat-paginator
  [pageSize]="10"
  [pageSizeOptions]="[5, 10, 25]"
  (page)="onPageChange($event)"
>
  <
</mat-paginator>
```

**Método para manejar la paginación**

```Typescript
onPageChange(event: PageEvent) {
  const pageNumber = event.pageIndex;
  const pageSize = event.pageSize;

  this.loginAttemptService.getLoginAttempts(this.userId, pageNumber, pageSize)
    .subscribe(response => {
      this.loginAttempts = response.content; // Contenido de la página actual
      this.totalElements = response.totalElements; // Total de elementos
    });
}
```

**Servicio en Angular**

```Typescript
getLoginAttempts(userId: number, pageNumber: number, pageSize: number): Observable<Page<LoginAttempt>> {
  const params = new HttpParams()
    .set('pageNumber', pageNumber.toString())
    .set('pageSize', pageSize.toString());

  return this.httpClient.get<Page<LoginAttempt>>(`/api/login-attempts/user/${userId}`, { params });
}
```

## Cómo Saber si Hay Más Páginas Pendientes

En el frontend, cuando se utiliza la paginación, puedes saber que no hay más páginas pendientes o datos adicionales para mostrar analizando la respuesta que envía el backend. Cuando usas paginación con Spring Data, el backend devuelve un objeto Page<T>, el cual contiene no solo los datos de la página solicitada, sino también información útil

En el objeto `Page<T>` que devuelve Spring Data, hay varios métodos que te ayudan a gestionar la paginación en el frontend. Algunos de los más útiles son:

1. `getTotalPages()`: Te dice el número total de páginas.
2. `getNumber()`: Te dice el número de la página actual.
3. `hasNext()`: Desaparecetruesi hay más páginas disponibles después de la página actual.
4. `hasPrevious()`: Devuelve truesi hay una página anterior disponible

**Ejemplo de respuesta paginada en JSON**

Cuando haces una solicitud de una página, Spring Data JPA puede devolver una re

```json
{
  "content": [
    /* Aquí van los datos de la página actual */
  ],
  "pageable": {
    "pageNumber": 4,
    "pageSize": 10
  },
  "totalPages": 5,
  "totalElements": 50,
  "last": false,
  "first": false,
  "numberOfElements": 10,
  "size": 10,
  "number": 4,
  "sort": {
    "sorted": true,
    "unsorted": false,

    "empty": false
  }
}
```

**En el Frontend**

En el frontend, cuando recibas esta respuesta, puedes utilizar la información de los campos de paginación para saber si hay más páginas por cargar.

Por ejemplo, si estás usando Angular , puedes implementar algo como esto

```Typescript
loadMore() {
  this.myService.getPage(this.currentPage).subscribe(data => {
    this.items = [...this.items, ...data.content]; // Añadir los nuevos elementos
    this.currentPage = data.number;  // Actualizar el número de página actual
    this.hasNextPage = !data.last;   // Si 'last' es true, ya no hay más páginas
  });
}
```

Aquí, el valor data.lastte indica si ya ha llegado a la última página. Si data.lastes true, significa que no

**Desactivando el botón**

Usando `data.last`, puedes condicionar la visibilidad o el estado del botón:

html

Copiar código
<button (click)="loadMore()" \*ngIf="hasNextPage">Cargar más</button>
O bien, si estás utilizando paginación en tabla con \*\*Angular Mat Angular Material :

```html
<mat-paginator
  [length]="totalElements"
  [pageSize]="pageSize"
  [pageIndex]="currentPage"
  [pageSizeOptions]="[5, 10, 20]"
>
</mat-paginator>
```

Y luego en el **componente** puedes manejar los eventos de cambio.

```Typescript
onPageChange(event: PageEvent) {
  this.currentPage = event.pageIndex;
  this.pageSize = event.pageSize;
  this.loadMore(); // Aquí vuelves a hacer la solicitud al backend para obtener la página
}
```

**Backend: Parámetros de Paginación**

En cada solicitud desde el frontend, puedes enviar los parámetros pageNumbery pageSizeal backend para obtener los datos correspondientes a esa página específica:

```Typescript
this.http.get('api/my-endpoint', { params: { pageNumber: '0', pageSize: '10' } })
    .subscribe(response => {
        // Manejar la respuesta
    });
```

Se ha implementado la paginación en las peticiones de UserSession en los `get`

## Cuando usar `List` o `Paginación`

**Cuándo usar List (sin paginación)**:

1. Pequeños volúmenes de datos:

Si el conjunto de datos que esperas recibir es pequeño (por ejemplo, unas decenas o cientos de registros), puedes optar por utilizar List. Esto es más sencillo de implementar y no añade la complejidad de la paginación.
Ejemplo: cuando estás obteniendo una lista de roles, configuraciones o pequeñas tablas maestras que no crecerán de manera considerable.

2. Consultas internas:

Si estás realizando consultas para propósitos internos o para algún procesamiento que no depende de la interacción del usuario, y sabes que el tamaño de los datos es pequeño, entonces un List es suficiente.

3. Consultas sobre datos estáticos:

Si los datos no cambian con frecuencia y no tienden a crecer mucho (ej. configuraciones del sistema), podrías no necesitar paginación.

4. Mejor rendimiento cuando los resultados son pequeños:

Si la consulta va a devolver solo unas pocas filas de manera constante, paginar añade una capa extra de procesamiento que podría no ser necesaria.

**Cuándo usar Paginación**:

2. Grandes volúmenes de datos:

Si esperas que la consulta devuelva muchos registros (miles o millones de filas), siempre es recomendable implementar paginación. Esto evita cargar demasiados datos en memoria de una sola vez, mejorando el rendimiento y reduciendo el consumo de recursos (tanto en el servidor como en el cliente).
Ejemplo: si tienes una tabla de auditoría de login con millones de registros y estás mostrando estos resultados en una interfaz de usuario.

2. Mejora de la experiencia del usuario (frontend):

Si los resultados se muestran en una interfaz gráfica donde el usuario puede ir navegando entre páginas de datos, la paginación es clave para mejorar la experiencia y reducir la latencia de carga. Los usuarios solo verán una página de datos a la vez en lugar de tener que esperar que se cargue toda la información.

3. Consultas que pueden crecer con el tiempo:

Si trabajas con tablas que tienden a crecer de manera considerable con el tiempo (ej. logs, transacciones), es mejor diseñar el sistema desde el principio con paginación, para evitar problemas de rendimiento en el futuro.

4. Sistemas distribuidos o API públicas:

Si estás trabajando con una API pública o distribuida donde los usuarios pueden hacer muchas consultas a tu base de datos, es recomendable limitar la cantidad de resultados con paginación. Esto previene que un cliente consuma demasiados recursos de manera innecesaria y ayuda a proteger la estabilidad del sistema.

5. Consultas dinámicas e iterativas:

Cuando los usuarios pueden interactuar con los resultados, como por ejemplo, aplicar filtros o buscar dentro de los resultados, la paginación permite limitar las respuestas de manera eficiente, ofreciendo los datos de manera incremental.

**Consideraciones adicionales**:

Consistencia de la interfaz de usuario: En interfaces donde los usuarios esperan ver los datos distribuidos en varias páginas, usar paginación garantiza que siempre tengan una experiencia consistente, sin importar cuántos registros se devuelvan.
Coste de memoria: Sin paginación, es más probable que el servidor tenga que cargar en memoria grandes cantidades de datos, lo que puede llevar a problemas de rendimiento.

[Retornar a la principal](../../README.md)

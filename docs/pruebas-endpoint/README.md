# Pruebas de las funcionalidades

[Retornar a la principal](../../README.md)

Puede utilizar [Postman](https://www.postman.com/) para hace pruebas, seguidamente se muestran las pruebas que se puede hacer y le permiten tener claro el objeto que debe enviarse a estos endpoits:

Se presentan según el rol de acceso que son tres:

1. Public, rutas que no requieren el envío de token
2. ROLE_USER, rutas que puede utilizar tanto el `ROLE_ADMIN` como el `ROLE_USER`
3. ROLE_ADMIN, ruta que sólo puede aceder el `ROLE_ADMIN`

## Public

### Account

1. Activar cuenta

Método: **`GET`**
URL:

```
http://localhost:8080/api/public/activate-account/23/63a0cacf-b754-4a2a-a3f2-222d24ed815b
```

2. Descarga de archivo PDF

Método: **`GET`**
URL:

```
http://localhost:8080/api/public/files/download/Resume_es.pdf
```

3. Activación de una cuenta con un password temporal (cuenta creada por el ROLE_USER)

Método: **`POST`**
URL:

```
http://localhost:8080/api/public/activate-account-with-temporaty-password/77/0fa73827-825d-4c46-9662-d1ea4e2749a9
```

JSON:

```
{
    "tempPassword": "AMESvk",
    "newPassword": "Juan2024.$14"
}
```

4. Solicitud de restablecimiento de contraseña

Método: **`POST`**
URL:

```
http://localhost:8080/api/public/password-reset-request
```

JSON:

```
{
    "email": "testcodecr78b@gmail.com",
    "nickname": null
}
```

5. cambio de contraseña que ha sido restablecida

Método: **`POST`**
URL:

```
http://localhost:8080/api/public/change-password-by-reset/78/1de90916-8815-4902-bae0-cabfba2a0b78
```

JSON:

```
{
    "newPassword": "Juan2024.$13"
}
```

### Auth (no requiere token)

1. Registro de usuario por el propio usuario

Método: **`POST`**
URL:

```
http://localhost:8080/api/auth/register
```

JSON:

```
{
    "firstName": "Juana78",
    "lastName": "Arcoiris",
    "secondLastName": "Lan",
    "email": "testcodecr@gmail.com",
    "nickname": "JuanaC78",
    "languageKey": "en",
    "password": "Juan2024.$12"
}
```

2. Login con el nickname

Método: **`POST`**
URL:

```
http://localhost:8080/api/auth/login/nickname
```

JSON:

```
{
    "nickname": "JuanaC17",
    "password": "Juan2024.$12"
}
```

3. Login con el email

Método: **`POST`**
URL:

```
http://localhost:8080/api/auth/login/email
```

JSON:

```
{
    "email": "tema@gmail.com",
    "password": "Juan2024.$13"
}
```

## ROLE_ADMIN (SI requiere token)

### User

1. Consulta de un usuario por `Correo`

Método: **`GET`**
URL:

```
http://localhost:8080/api/admin/users/get-by-email/nombre@gmail.com
```

2. Consulta de un usuario por `Nickname`

Método: **`GET`**
URL:

```
http://localhost:8080/api/admin/users/get-by-nickname/nicnameusuario
```

3. Consulta de un usuario por `Id`

Método: **`GET`**
URL:

```
http://localhost:8080/api/admin/users/get/by-id/3
```

4. Consulta de todos los usuarios

Método: **`GET`**
URL:

```
http://localhost:8080/api/admin/users
```

5. Cambio de parámetros `status` y `activated` de cualquier usuario

Método: **`PATCH`** (Se utiliza procedimiento almacenado)
URL:

```
http://localhost:8080/api/admin/users/update
```

JSON:

```
{
    "id": numero,
    "status": null,
    "activated": true o null
}
```

6. Establecimiento de Rol a cualquier usario

Método: **`PATCH`** (Se utiliza procedimiento almacenado)
URL:

```
http://localhost:8080/api/admin/users/update/role
```

JSON:

```
{
    "userId": 10022,
    "authorities": [
        {
            "name": "ROLE_USER"
        }
    ]
}
```

7. Crear una cuenta por parte del Administrador

Método: **`POST`**
URL:

```
http://localhost:8080/api/admin/users/register
```

JSON:

```
{
    "firstName": "nombre",
    "lastName": "apellido",
    "secondLastName": "segundo_apellido" o null,
    "email": "correo",
    "nickname": "apodo",
    "languageKey": "es"
}
```

8. Reenvío de activación de cuenta

Método: **`POST`**
URL:

```
ttp://localhost:8080/api/admin/users/resend-activation
```

JSON:

```
{
    "email": "correo"
}
```

### FailedLoginAttempt

1. Obtener los intentos fallidos por `nickname`

Método: **`GET`**
URL:

```
http://localhost:8080/api/admin/failed-login/get-by-nickname/ljuan?pageNumber=0&pageSice=10
```

2. Obtener los intentos fallidos por `email` paginado

Método: **`GET`**
URL:

```
http://localhost:8080/api/admin/failed-login/get-by-email/mm@gmail.com?pageNumber=0&pageSize=10
```

3. Obtener el número de intentos fallidos por `nickname`

Método: **`GET`**
URL:

```
http://localhost:8080/api/admin/failed-login/count-by-nickname/ljuan
```

4. Obtener el número de intentos fallidos por `ipAddress`

Método: **`GET`**
URL:

```
http://localhost:8080/api/admin/failed-login/count-by-ipAddress/0:0:0:0:0:0:0:1
```

5. Obtener el número de intentos fallidos por `email entre fechas`

Método: **`GET`**
URL:

```
http://localhost:8080/api/admin/failed-login/count-by-email/mm@gmail.com/startTime/2024-10-03T15:00/endTime/2024-10-10T20:00
```

6. Obtener el número de intentos fallidos por `nickname entre fechas`

Método: **`GET`**
URL:

```
http://localhost:8080/api/admin/failed-login/count-by-nickname/ljuan/startTime/2024-10-10T15:00/endTime/2024-10-10T20:00
```

7. Obtener el número de intentos fallidos por `ipAddress entre fechas`

Método: **`GET`**
URL:

```
http://localhost:8080/api/admin/failed-login/count-by-ipAddress/0:0:0:0:0:0:0:1/startTime/2024-10-10T15:00/endTime/2024-10-10T18:00
```

8. Eliminar los intentos fallidos por `email antes de una fecha`

Método: **`DELETE`**
URL:

```
http://localhost:8080/api/admin/failed-login/delete-by-email/mm@gmail.com/attemptTime/2024-10-10T16:28
```

9. Eliminar los intentos fallidos por `nickname antes de una fecha`

Método: **`DELETE`**
URL:

```
http://localhost:8080/api/admin/failed-login/delete-by-nickname/ljuan/attemptTime/2024-10-10T16:26
```

10. Eliminar los intentos fallidos `antes de una fecha`

Método: **`DELETE`**
URL:

```
http://localhost:8080/api/admin/failed-login/delete-by-time-before/2024-10-10T16:31
```

### UserLoginActivity

1. Obtener todos los intentos

Método: **`GET`**
URL:

```
http://localhost:8080/api/admin/user-login/all
```

2. Obtener todos los intentos paginados

Método: **`GET`**
URL:

```
http://localhost:8080/api/admin/user-login/get-by-page?pageNumber=0&pageSize=10
```

3. Obtener los intentos pur idUser y Status

Método: **`GET`**
URL:

```
http://localhost:8080/api/admin/user-login/get-by-userId/1/status/SUCCESS
```

4. Obtener los intentos por idUser y Status

Método: **`GET`**
URL:

```
http://localhost:8080/api/admin/user-login/get-by-userId/1/status/SUCCESS
```

5. Obtener los intentos por ipAddress y Status

Método: **`GET`**
URL:

```
http://localhost:8080/api/admin/user-login/get-by-ipAddress/0:0:0:0:0:0:0:1/status/SUCCESS
```

6. Obtener los intentos por UserId paginado

Método: **`GET`**
URL:

```
http://localhost:8080/api/admin/user-login/get-by-userId/1?pageNumber=0&pageSize=10
```

7. Obtener los intentos por IpAddress entre fechas paginado

Método: **`GET`**
URL:

```
http://localhost:8080/api/admin/user-login/get-top-by-ipAddress/0:0:0:0:0:0:0:1/startTime/2024-10-10T15:00/endTime/2024-10-10T17:00?pageNumber=0&pageSize=10
```

8. Obtener los intentos por UserAgent entre fechas paginado

Método: **`GET`**
URL:

```
http://localhost:8080/api/admin/user-login/get-by-userAgent?userAgent=PostmanRuntime/2F7.42.0&startTimeString=2024-10-10T15:00&endTimeString=2024-10-10T16:00&pageNumber=0&pageSize=10
```

9. Contar los intentos por userId por status

Método: **`GET`**
URL:

```
http://localhost:8080/api/admin/user-login/count-by-userId/1/status/SUCCESS
```

10. Eliminar los intentos por userId y status

Método: **`DEL`**
URL:

```
http://localhost:8080/api/admin/user-login/delete-by-userId/1/status/FAILURE
```

### Refresh Token

1. Obtener los Refresh Token

Método: **`GET`**
URL:

```
http://localhost:8080/api/admin/refresh-tokens/all
```

2. Obtener los Refresh Token que expiran entre fechas

Método: **`GET`**
URL:

```
http://localhost:8080/api/admin/refresh-tokens/expiry-from/2024-10-01T14:52/to/2024-10-03T15:00
```

3. Obtener los Refresh Token por idToken

Método: **`GET`**
URL:

```
http://localhost:8080/api/admin/refresh-tokens/by-idToken/1
```

4. Obtener los Refresh Token por Token

Método: **`GET`**
URL:

```
http://localhost:8080/api/admin/refresh-tokens/by-token/eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJNYXJpb01MIiwicm9sZXMiOiJST0xFX0FETUlOIiwiaWF0IjoxNzI5MDIyMDg0LCJleHAiOjE3MjkwMjU2ODR9.HRuNjhIXMg15aTVts_liZlIUE-rwap4QXD1AURb6DjU1UE75uvvfeiWN5QDlZlPSXwWeIIBLOKZNPXPUiik6hg
```

5. Obtener los Refresh Token por idUser

Método: **`GET`**
URL:

```
http://localhost:8080/api/admin/refresh-tokens/by-idUser/1
```

6. Eliminar los Refresh Token previo a una fecha

Método: **`DEL`**
URL:

```
http://localhost:8080/api/admin/refresh-tokens/delete-expired-before/2024-10-03T15:00
```

7. Eliminar todos los Refresh Token

Método: **`DEL`**
URL:

```
http://localhost:8080/api/admin/refresh-tokens/delete-all
```

8. Eliminar el Refresh Token por idToken

Método: **`DEL`**
URL:

```
http://localhost:8080/api/admin/refresh-tokens/delete-all
```

9. Eliminar el Refresh Token por idUser

Método: **`DEL`**
URL:

```
http://localhost:8080/api/admin/refresh-tokens/delete-by-idUser/3
```

## ROLE_USER (SI requiere token)

### Account

1. Cambio de `password` del usario

Método: **`PATCH`** (Se utiliza procedimiento almacenado)
URL:

```
http://localhost:8080/api/users/update/password
```

JSON:

```
{
    "id": 10004,
    "newPassword": "nuevo_pass",
    "oldPassword": "pass_actual"
}
```

2. Cambio de `correo` del usario

Método: **`PATCH`** (Se utiliza procedimiento almacenado)
URL:

```
http://localhost:8080/api/users/update/email
```

JSON:

```
{
    "id": 10022,
    "email": "nombre2.com"
}
```

3. Cambio de `nickname` del usario

Método: **`PATCH`** (Se utiliza procedimiento almacenado)
URL:

```
http://localhost:8080/api/users/update/nickname
```

JSON:

```
{
    "id": 10004,
    "nickname": "nuevoNickname"
}
```

4. Administración de datos básicos del usario

Método: **`PATCH`** (Se utiliza procedimiento almacenado)
URL:

```
http://localhost:8080/api/users/update/profile
```

JSON:

```
{
    "id": 10022,
    "firstName": null o nombre,
    "lastName": null o apellido,
    "secondLastName": null o segundo_apellido,
    "languageKey": "es"
}
```

5. Refresh Token

Método: **`POST`** (Se utiliza procedimiento almacenado)
URL:

```
http://localhost:8080/api/users/refresh-token/1
```

### ClientType

1. Obtener los tipos de clientes

Método: **`GET`**
URL:

```
http://localhost:8080/api/user-bussiness/client-type/all
```

[Retornar a la principal](../../README.md)

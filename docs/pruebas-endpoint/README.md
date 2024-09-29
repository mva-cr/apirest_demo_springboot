# Pruebas de las funcionalidades

[Retornar a la principal](../../README.md)

Puede utilizar [Postman](https://www.postman.com/) para hace pruebas, seguidamente se muestran las pruebas que se puede hacer y le permiten tener claro el objeto que debe enviarse a estos endpoits:

Se presentan según la ruta de acceso

## Auth (no requiere token)

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

1. Consulta de un usuario por `Correo`

Método: **`GET`**
URL:

```
http://localhost:8080/api/admin/users/get/by-email/nombre@gmail.com
```

2. Consulta de un usuario por `Nickname`

Método: **`GET`**
URL:

```
http://localhost:8080/api/admin/users/get/by-nickname/nicnameusuario
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

## ROLE_USER (SI requiere token)

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

## Public (no requiere token)

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

[Retornar a la principal](../../README.md)

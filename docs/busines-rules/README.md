# Reglas de negocio definidas

[Retornar a la principal](../../README.md)

A nivel de la APIRest se han definido las siguientes reglas de negocio

## Accesos

### Libre

Todos los usuario tienen acceso al login

### `ROLE_ADMIN`

Solo este rol tiene los siguientes permisos

1. crear un usuario
2. consultar cualquier usuario por:

- id
- email
- nickname

3. modificar el `status` y la condición de `activado` de cualquier usuario
4. modificar el `rol` denominado `Authority` de cualquier usuario

## `ROLE_USER`

Solo el propio usuario (dueño de la cuenta) puede:

1. modificar el perfil, específicamente en:

- firstName (nombre)
- lastName (apellido)
- secondLastName (segundo apellido, este valor es opcional)
- nickname
- languageKey (idioma)

## Reglas que deben cumplir los atributos

1. `id` no puede ser nulo
2. `firstName`, `secondName`, `languageKey`, `name` (se refiere al nombre del Autoridad asignada o el `ROLE`) y `secondLastName` cuando el valor enviado no se nulo, son:

- no pude ser nulo,
- no puede contener espacio es blanco,
- al menos debe contener un caracter,
- no puede superar los 50 caracteres.

3. `nickname` este debe cumplir las sigiuentes reglas:

- no puede ser nulo,
- no puede contener espacio es blanco,
- al menos debe contener un caracter,
- no puede superar los 50 caracteres.
- puede (no obligatorio) incluir las siguientes condiciones:
  - contener letras mayúsculas y minúsculas
  - dígitos (del 0 al 9)
  - guion bajo (\_)
  - punto (.)
  - arroba (@)
  - guion (-)

4. `email`

- no puede ser nulo,
- no puede contener espacio es blanco,
- al menos debe contener 5 caracter,
- no puede superar los 50 caracteres.
- puede (no obligatorio) incluir las siguientes condiciones:
  - contener letras mayúsculas y minúsculas
  - dígitos (del 0 al 9)
  - guion bajo (\_)
  - punto (.)
  - signo de porcentaje (%)
  - guion (-)
  - símbolo de suma (+)

5. `password`

- no puede ser nulo,
- no puede contener espacio es blanco,
- al menos debe contener 12 caracter,
- no puede superar los 50 caracteres.
- debe (es obligatorio) incluir las siguientes condiciones:
  - al menos 1 letra minúscula,
  - al menos 1 letra mayúscula,
  - al menos 1 dígito (del 0 al 9)
  - al menos uno de los siguientes caracteres especiales:
    - arroba (@)
    - Símbolo de dólar ($)
    - Signo de exclamación (!)
    - Signo de porcentaje (%)
    - Asterisco (\*)
    - Signo de interrogación (?)
    - Ampersand (&)
    - punto (.)
    - Coma (,)
    - Slash (/)

[Retornar a la principal](../../README.md)

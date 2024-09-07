# Aspectos técnicos de la base de datos

Para el idioma se utiliza el código de idiomas según ISO-639-1 (2 letras). Por ejemplo:
1. Español: es
2. Inglés: en

## Tamaño Común de activationKey
UUID (32 caracteres hexadecimales + 4 guiones):

Un UUID estándar tiene 36 caracteres en total (32 hexadecimales y 4 guiones).

Ejemplo: 123e4567-e89b-12d3-a456-426614174000

Si usas UUID para activationKey, un tamaño de NVARCHAR(36) es adecuado.
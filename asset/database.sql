
use master
GO
DROP database IF EXISTS customer
GO
CREATE DATABASE customer
GO
USE customer;
GO
DROP TABLE IF EXISTS country
GO
CREATE TABLE country
(
  id TINYINT NOT NULL IDENTITY(1,1),
  name NVARCHAR(255) NOT NULL,
  phone_code SMALLINT NOT NULL,
  CONSTRAINT PK_id_country PRIMARY KEY CLUSTERED (id),
  CONSTRAINT UK_name_country UNIQUE(name)
)

GO
DROP TABLE IF EXISTS message
GO
CREATE TABLE message
(
  id INT NOT NULL IDENTITY(1,1),
  content NVARCHAR(700) NOT NULL,
  topic NVARCHAR(100) NOT NULL,
  summary NVARCHAR(100) NOT NULL,
  CONSTRAINT PK_id_message PRIMARY KEY CLUSTERED (id)
)

GO
DROP TABLE IF EXISTS message_type
GO
CREATE TABLE message_type
(
  id TINYINT NOT NULL IDENTITY(1,1),
  type NVARCHAR(12) NOT NULL,
  CONSTRAINT PK_id_message_type PRIMARY KEY CLUSTERED (id)
)
GO
DROP TABLE IF EXISTS subscription
GO
CREATE TABLE subscription
(
  id TINYINT NOT NULL IDENTITY(1,1),
  type NVARCHAR(12) NOT NULL,
  CONSTRAINT PK_id_subscription PRIMARY KEY CLUSTERED (id)
)

GO
DROP TABLE IF EXISTS client_type
GO
CREATE TABLE client_type
(
  id TINYINT NOT NULL IDENTITY(1,1),
  type NVARCHAR(12) NOT NULL,
  CONSTRAINT PK_id_client_type PRIMARY KEY CLUSTERED (id)
)

GO
DROP TABLE IF EXISTS client
GO
CREATE TABLE client
(
  id INT NOT NULL IDENTITY(1,1),
  name NVARCHAR(50) NOT NULL,
  last_name NVARCHAR(50) NOT NULL,
  second_last_name NVARCHAR(50) DEFAULT null,
  email NVARCHAR(100) NOT NULL,
  address NVARCHAR(250) DEFAULT NULL,
  create_at DATE NOT NULL,
  id_country TINYINT NOT NULL,
  id_message_type TINYINT NOT NULL,
  id_subscription TINYINT NOT NULL,
  CONSTRAINT PK_id_client PRIMARY KEY CLUSTERED (id),
  CONSTRAINT FK_id_country_client FOREIGN KEY (id_country) REFERENCES country (id),
  CONSTRAINT FK_message_type_client FOREIGN KEY (id_message_type) REFERENCES message_type (id),
  CONSTRAINT FK_id_subscription_client FOREIGN KEY (id_subscription) REFERENCES subscription (id)
)

GO
DROP TABLE IF EXISTS authority
GO

-- ===============================================================
-- Author: Mario Martínez Lanuza
-- Create date: 2024-09-08
-- Description: Tabla de perfiles de usuario
-- ===============================================================
CREATE TABLE authority
(
  name NVARCHAR(50) NOT NULL,
  CONSTRAINT PK_name_authority  PRIMARY KEY CLUSTERED (name)
)

GO

-- ===============================================================
-- Author: Mario Martínez Lanuza
-- Create date: 2024-09-08
-- Description: Tabla de user
-- ===============================================================
DROP TABLE IF EXISTS user_mva
GO

CREATE TABLE user_mva
(
  id BIGINT NOT NULL IDENTITY(1,1),
  first_name NVARCHAR(50) NOT NULL,
  last_name NVARCHAR(50) NOT NULL,
  second_last_name NVARCHAR (50) NULL,
  email NVARCHAR(254) NOT NULL,
  nickname NVARCHAR(50) NOT NULL,
  password_hash NVARCHAR(60) NOT NULL,
  language_key NVARCHAR(2) CONSTRAINT DF_language_key_user_mva DEFAULT ('es') NOT NULL,
  activated BIT CONSTRAINT DF_activated_user_mva DEFAULT ((0)) NOT NULL,
  status BIT CONSTRAINT DF_status_user_mva DEFAULT ((1)) NOT NULL,
  activation_key NVARCHAR(36),
  reset_key NVARCHAR(36),
  reset_date DATETIME2,
  CONSTRAINT PK_id_user_mva  PRIMARY KEY CLUSTERED (id),
  CONSTRAINT UK_email_user_mva UNIQUE(email),
  CONSTRAINT UK_nickname_user_mva UNIQUE(nickname)
)

GO
DROP TABLE IF EXISTS user_authority
GO

-- ===============================================================
-- Author: Mario Martínez Lanuza
-- Create date: 2024-09-08
-- Description: Tabla de Roles por user
-- ===============================================================
CREATE TABLE user_authority
(
  user_id SMALLINT NOT NULL,
  authority_name NVARCHAR(50) NOT NULL,
  CONSTRAINT PK_user_id_authority_name_user_auth PRIMARY KEY CLUSTERED (user_id, authority_name),
  CONSTRAINT FK_user_id_user_authority FOREIGN KEY (user_id) REFERENCES user_mva(id) ON DELETE CASCADE,
  CONSTRAINT FK_name_user_authority FOREIGN KEY (authority_name) REFERENCES authority(name) ON DELETE CASCADE
)

GO

-- ===============================================================
-- Author: Mario Martínez Lanuza
-- Create date: 2024-09-08
-- Description: Tabla de auditoría que registra los cambios realizados en la tabla user
-- ===============================================================
CREATE TABLE user_audit
(
  id_audit BIGINT NOT NULL IDENTITY(1,1),
  id_user BIGINT NOT NULL,
  -- id del user que hizo el cambio
  first_name NVARCHAR(50) NULL,
  -- Valor anterior del first_name
  last_name NVARCHAR(50) NULL,
  -- Valor anterior del last_name
  second_last_name NVARCHAR (50) NULL,
  -- Valor anterior del second_last_name
  email NVARCHAR(254) NULL,
  -- Valor anterior del email
  nickname NVARCHAR(50) NULL,
  -- Valor anterior del nickname
  password_changed BIT CONSTRAINT DF_password_changed_user_audit DEFAULT ((0)) NOT NULL,
  -- Valor anteror del atributo de idioma
  language_key NVARCHAR(2) NULL,
  -- Indica si la contraseña fue cambiada
  activated BIT NULL,
  -- Valor anterior del activated
  status BIT NULL,
  -- Valor anterior del status
  change_date DATETIME2 CONSTRAINT DF_change_date_user_audit DEFAULT (GETDATE()) NOT NULL,
  -- Fecha de la modificacion
  change_type NVARCHAR(10) NOT NULL,
  -- Tipo de cambio: 'INSERT', 'UPDATE', 'DELETE'
  CONSTRAINT PK_id_audit_user_audit PRIMARY KEY CLUSTERED (id_audit),
  CONSTRAINT FK_id_user_user_audit FOREIGN KEY (id_user) REFERENCES user_mva(id)
);

GO

-- ===============================================================
-- Author: Mario Martínez Lanuza
-- Create date: 2024-09-08
-- Description: Tabla rastrea intentos de inicio de sesión fallidos y exitosos
-- ===============================================================
CREATE TABLE login_attempt
(
  id_attempt BIGINT NOT NULL IDENTITY(1,1),
  id_user BIGINT NULL,
  -- Puede ser NULL para intentos con usuario no identificado
  attempt_time DATETIME2 CONSTRAINT DF_attempt_time_login_attempt DEFAULT(GETDATE()) NOT NULL,
  ip_address NVARCHAR(50) NOT NULL,
  user_agent NVARCHAR(255) NULL,
  attempt_result NVARCHAR(10) NOT NULL,
  -- 'SUCCESS', 'FAILED'
  CONSTRAINT PK_id_attempt_login_attempt PRIMARY KEY CLUSTERED (id_attempt),
  CONSTRAINT FK_id_user_login_attempt FOREIGN KEY (id_user) REFERENCES user_mva(id)
);

GO


-- ===============================================================
-- Author: Mario Martínez Lanuza
-- Create date: 2024-09-08
-- Description: Tabla que controla y audita sesiones activas y pasadas
-- ===============================================================
CREATE TABLE user_session
(
  id_session NVARCHAR(128) NOT NULL,
  id_user BIGINT NOT NULL,
  start_time DATETIME2 CONSTRAINT DF_start_time_user_sesion DEFAULT(GETDATE()) NOT NULL,
  end_time DATETIME2,
  ip_address NVARCHAR(50) NOT NULL,
  user_agent NVARCHAR(255),
  session_status NVARCHAR(50) NOT NULL,
  -- 'ACTIVE', 'EXPIRED', 'LOGGED_OUT'
  CONSTRAINT PK_id_session_user_session PRIMARY KEY CLUSTERED (id_session),
  CONSTRAINT FK_id_user_user_session FOREIGN KEY (id_user) REFERENCES user_mva(id)
);
 GO

-- ===============================================================
-- Author: Mario Martínez Lanuza
-- Create date: 2024-09-08
-- Description: Tabla de auditoría que registra los cambios realizados
-- en la tabla user_authority
-- ===============================================================
CREATE TABLE user_authority_audit
(
  id_authority_audit BIGINT NOT NULL IDENTITY(1,1),
  id_user BIGINT NOT NULL,
  -- ID del usuario cuyas autoridades han cambiado
  authority_name NVARCHAR(50) NOT NULL,
  -- Nombre de la autoridad asignada o revocada
  change_date DATETIME2 CONSTRAINT DF_change_date_user_authority_audit DEFAULT(GETDATE()) NOT NULL,
  -- Fecha y hora en que se realizó el cambio
  change_type NVARCHAR(10) NOT NULL,
  -- Opciones de cambio: 'ASSIGNED', 'REVOKED'
  CONSTRAINT PK_id_authority_audit_user_authority_audit PRIMARY KEY CLUSTERED (id_authority_audit),
  CONSTRAINT FK_userid_user_authority_audit FOREIGN KEY (id_user) REFERENCES user_mva(id)
);


GO

-- ===============================================================
-- Trigger
-- ===============================================================


-- ===============================================================
-- Author: Mario Martínez Lanuza
-- Create date: 2024-09-08
-- Description: Trigger para actualizar el rol a ROLE_UNAUTHORIZE
-- cuando se modifica el status a 0, previamente el status es 1 y
-- el rol actual no es ROLE_UNAUTHORIZE
-- ===============================================================
IF EXISTS (SELECT 1
FROM sys.triggers
WHERE object_id = OBJECT_ID(N'[dbo].[trg_update_user_authority_on_status_change]'))
BEGIN
  DROP TRIGGER [dbo].[trg_update_user_authority_on_status_change];
END
GO

CREATE TRIGGER trg_update_user_authority_on_status_change
ON user_mva
AFTER UPDATE
AS
BEGIN
  -- Declarar variables para el nuevo y el estado anterior, el ID del usuario, y el rol actual
  DECLARE @new_status BIT, @current_status BIT, @id_user BIGINT, @current_role NVARCHAR(50);

  -- Obtener los estados anteriores y nuevos de la tabla insertada y eliminada
  SELECT @new_status = i.status, @current_status = d.status, @id_user = i.id
  FROM inserted i
    JOIN deleted d ON i.id = d.id;

  -- Obtener el rol actual del usuario
  SELECT TOP 1
    @current_role = authority_name
  FROM user_authority
  WHERE user_id = @id_user;

  -- Verificar si se cumplen las condiciones para actualizar el rol
  IF @new_status = 0 AND @new_status <> @current_status AND @current_role <> 'ROLE_UNAUTHORIZE'
  BEGIN
    -- Actualizar la tabla user_authority con el nuevo rol 'ROLE_UNAUTHORIZE'
    UPDATE ua
      SET ua.authority_name = 'ROLE_UNAUTHORIZE'
      FROM user_authority ua
      WHERE ua.user_id = @id_user;
  END
END;

GO

-- ===============================================================
-- Author: Mario Martínez Lanuza
-- Create date: 2024-09-08
-- Description: Trigger para auditar cambios en la tabla user_mva
-- ===============================================================
IF EXISTS (SELECT 1
FROM sys.triggers
WHERE object_id = OBJECT_ID(N'[dbo].[trg_user_mva_audit]'))
BEGIN
  DROP TRIGGER [dbo].[trg_user_mva_audit];
END
GO
CREATE TRIGGER trg_user_mva_audit
ON user_mva
AFTER INSERT, UPDATE
AS
BEGIN
  SET NOCOUNT ON;

  -- Insertar un registro en user_audit después de un insert o update
  INSERT INTO user_audit
    (
    id_user,
    first_name,
    last_name,
    second_last_name,
    email,
    nickname,
    password_changed,
    activated,
    status,
    change_date,
    change_type
    )
  SELECT
    i.id,
    d.first_name,
    d.last_name,
    d.second_last_name,
    d.email,
    d.nickname,
    CASE WHEN i.password_hash <> d.password_hash THEN 1 ELSE 0 END,
    d.activated,
    d.status,
    GETDATE(),
    CASE WHEN d.id IS NULL THEN 'INSERT' ELSE 'UPDATE' END
  FROM
    inserted i
    LEFT JOIN
    deleted d ON i.id = d.id;
END;
GO

-- ===============================================================
-- Author: Mario Martínez Lanuza
-- Create date: 2024-09-08
-- Description: Trigger para auditar cambios en la tabla user_authority
-- ===============================================================
IF EXISTS (SELECT 1
FROM sys.triggers
WHERE object_id = OBJECT_ID(N'[dbo].[trg_user_authority_audit]'))
BEGIN
  DROP TRIGGER [dbo].[trg_user_authority_audit];
END
GO

CREATE TRIGGER trg_user_authority_audit
ON user_authority
AFTER INSERT, UPDATE
AS
BEGIN
  SET NOCOUNT ON;

  -- Insertar un registro en user_authority_audit después de un insert o update
  INSERT INTO user_authority_audit
    (
    id_user,
    authority_name,
    change_date,
    change_type
    )
  SELECT
    i.user_id,
    i.authority_name,
    GETDATE(),
    CASE 
      WHEN i.authority_name <> 'ROLE_UNAUTHORIZE' THEN 'ASSIGNED' 
      ELSE 'REVOKED' 
    END
  FROM
    inserted i;
-- Eliminamos el LEFT JOIN ya que no es necesario en esta lógica
END;


GO

-- ===============================================================
-- Procedimientos almacenados
-- ===============================================================

-- ===============================================================
-- Author: Mario Martínez Lanuza
-- Create date: 2024-09-08
-- Description: Procedimiento almacenado para actualizar selectivamente los atributos 'activated' y 'status'.
-- Previo a realizar el update en la tabla user_mva realiza tres validaciones:
-- Primera: Al menos uno de los parametros nullables no debe ser NULL
-- Segunda: Verifica que el id del usuario ingresado existe.
-- Tercera: Si los dos parámetros no son NULL no deben se igual al valor previo o si uno es NULL
-- el otro no debe ser a su valor previo.
-- Importate: Este procedimiento activará cualquier Trigger de 'AFTER UPDATE' definido en user_mva,
-- se recomiende incluir lógica en los triggers para verificar cambios efectivos en los datos
-- y evitandor ejecuciones innecesarias que podrían afectar el rendimiento.
-- ===============================================================
IF EXISTS (SELECT *
FROM sys.objects
WHERE object_id = OBJECT_ID(N'[dbo].[sp_update_user_by_role_admin]') AND type in (N'P', N'PC'))
BEGIN
  DROP PROCEDURE [dbo].[sp_update_user_by_role_admin]
END
GO

CREATE PROCEDURE sp_update_user_by_role_admin
  @id_user BIGINT,
  @activated BIT = NULL,
  @status BIT = NULL
AS
BEGIN
  SET NOCOUNT ON;

  -- Verificar si al menos uno de los parámetros tiene un valor no nulo
  IF @activated IS NULL AND @status IS NULL
  BEGIN
    RAISERROR('100, Al menos una variable no debe ser nula', 16, 1);
    RETURN;
  END

  -- Verificar si el id_user existe en la tabla user_mva
  IF NOT EXISTS (SELECT 1
  FROM user_mva
  WHERE id = @id_user)
  BEGIN
    RAISERROR('102, El usuario no existe', 16, 1);
    RETURN;
  END

  -- Carga de los valores previos
  DECLARE @current_activated BIT, @current_status BIT;
  SELECT @current_activated = activated, @current_status = status
  FROM user_mva
  WHERE id = @id_user;

  -- Verificar si los parámetros proporcionados son iguales a los valores existentes
  IF ((@activated = @current_activated AND @status = @current_status) OR
    (@activated = @current_activated) AND (@status IS NULL) OR
    (@activated IS NULL AND @status = @current_status))
  BEGIN
    RAISERROR('103, El nuevo valor no puede ser igual al valor actual', 16, 1);
    RETURN;
  END

  BEGIN TRY
    BEGIN TRANSACTION

    -- Actualizar configuraciones críticas de usuario controladas por administradores
    UPDATE user_mva
    SET activated = COALESCE(@activated, activated),
        status = COALESCE(@status, status)
    WHERE id = @id_user;

    COMMIT TRANSACTION
  END TRY
  BEGIN CATCH
    ROLLBACK TRANSACTION;
    DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
    RAISERROR(@ErrorMessage, 16, 1);
  END CATCH
END;


GO

-- ===============================================================
-- Author: Mario Martínez Lanuza
-- Create date: 2024-09-08
-- Description: Actualiza el rol de un usuario
-- Validaciones:
--  El id del user debe existir
--  El rol no debe ser null
--  El rol debe existir
--  El rol asignado debe ser diferente al existente. Si el rol actual es 
-- 'ROLE_ADMIN' y se va a pasar a 'ROLE_USER' o viceversa solo se actualiza
-- la tabla user_authority. Pero si se va a cambiar a 'ROLE_UNAUTHORIZE'
-- se verifica que el 'status' de la tabla user_mva sea diferente de 0, y se
-- cambia a status = 0 en esta tabla, otro trigger actualiza la tabla user_authority
-- Importante: Esto procedimiento activa Trigger.
-- ===============================================================
IF EXISTS (SELECT *
FROM sys.objects
WHERE object_id = OBJECT_ID(N'[dbo].[sp_update_user_role_with_status_check]') AND type in (N'P', N'PC'))
BEGIN
  DROP PROCEDURE [dbo].[sp_update_user_role_with_status_check]
END
GO

CREATE PROCEDURE sp_update_user_role_with_status_check
  @id_user BIGINT,
  @role NVARCHAR(50)
-- Los roles posibles son 'ROLE_ADMIN', 'ROLE_USER', 'ROLE_UNAUTHORIZE'
AS
BEGIN
  SET NOCOUNT ON;

  -- Verificar que el role no sea nulo
  IF @role IS NULL
  BEGIN
    RAISERROR('101, El valor no puede ser nulo', 16, 1);
    RETURN;
  END

  -- Verificar si el id_user existe en la tabla user_mva
  IF NOT EXISTS (SELECT 1
  FROM user_mva
  WHERE id = @id_user)
  BEGIN
    RAISERROR('102, El usuario no existe', 16, 1);
    RETURN;
  END

  -- Declaración de variables
  DECLARE @current_status BIT;
  DECLARE @current_role NVARCHAR(50);

  -- Obtener el status actual del usuario
  SELECT @current_status = status
  FROM user_mva
  WHERE id = @id_user;

  -- Obtener el rol actual del usuario
  SELECT TOP 1
    @current_role = authority_name
  FROM user_authority
  WHERE user_id = @id_user;

  -- Verificar si el rol proporcionado es igual al rol existente
  IF @current_role = @role
  BEGIN
    RAISERROR('103, El nuevo valor no puede ser igual al valor actual', 16, 1);
    RETURN;
  END

  -- Verificar que el rol exista
  IF NOT EXISTS (SELECT 1
  FROM authority
  WHERE name = @role)
  BEGIN
    RAISERROR('104, El rol asignado no existe', 16, 1);
    RETURN;
  END

  BEGIN TRY
    BEGIN TRANSACTION
    -- Verificar si el usuario no tiene ningún rol asignado
    IF @current_role IS NULL
    BEGIN
      -- Asignar el rol proporcionado
      INSERT INTO user_authority (user_id, authority_name)
      VALUES (@id_user, @role);
    END
    
    -- Condición para 'ROLE_USER' o 'ROLE_ADMIN': Actualizar status a 1 si el status es 0
    IF @role IN ('ROLE_USER', 'ROLE_ADMIN') AND @current_status = 0
    BEGIN
    UPDATE user_mva
      SET status = 1
      WHERE id = @id_user;

    UPDATE user_authority
      SET authority_name = @role
      WHERE user_id = @id_user;
  END

    -- Condición para cambio entre 'ROLE_ADMIN' y 'ROLE_USER' si el status es 1
    IF @role IN ('ROLE_ADMIN', 'ROLE_USER') AND @current_status = 1
    BEGIN
    UPDATE user_authority
      SET authority_name = @role
      WHERE user_id = @id_user;
  END

    -- Condición para 'ROLE_UNAUTHORIZE': Si el usuario está activo, cambiarlo a inactivo
    IF @role = 'ROLE_UNAUTHORIZE' AND @current_status <> 0
    BEGIN
    UPDATE user_mva
        SET status = 0
        WHERE id = @id_user;

    UPDATE user_authority
        SET authority_name = @role
        WHERE user_id = @id_user;
  END

  -- Condición para 'ROLE_UNAUTHORIZE': Si el usuario NO está activo, cambiarlo a inactivo
    IF @role = 'ROLE_UNAUTHORIZE' AND @current_status = 0
    BEGIN
    UPDATE user_authority
        SET authority_name = @role
        WHERE user_id = @id_user;
  END

    COMMIT TRANSACTION
  END
  TRY
  BEGIN CATCH
  ROLLBACK TRANSACTION;
  -- Capturar y re-lanzar cualquier error que ocurra durante la transacción
  DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
  DECLARE @ErrorSeverity INT = ERROR_SEVERITY();
  DECLARE @ErrorState INT = ERROR_STATE();
  RAISERROR(@ErrorMessage, @ErrorSeverity, @ErrorState);
  END CATCH
END;

GO

-- ===============================================================
-- Author: Mario Martínez Lanuza
-- Create date: 2024-09-08
-- Description: Procedimiento almacenado para actualizar selectivamente los atributos:
--  'first_name', 'last_name y 'email'. Entos no deben ser igual a los
-- Importate: Este procedimiento activará cualquier Trigger de 'AFTER UPDATE' definido en user_mva,
-- se recomiende incluir lógica en los triggers para verificar cambios efectivos en los datos
-- y evitandor ejecuciones innecesarias que podrían afectar el rendimiento.
-- ===============================================================
IF EXISTS (SELECT *
FROM sys.objects
WHERE object_id = OBJECT_ID(N'[dbo].[sp_update_user_by_role_user]') AND type in (N'P', N'PC'))
BEGIN
  DROP PROCEDURE [dbo].[sp_update_user_by_role_user]
END
GO

CREATE PROCEDURE sp_update_user_by_role_user
  @id_user BIGINT,
  @first_name NVARCHAR(50) = NULL,
  @last_name NVARCHAR(50) = NULL,
  @second_last_name NVARCHAR (50) NULL,
  @language_key NVARCHAR(2) NULL
AS
BEGIN
  SET NOCOUNT ON;

  -- Verificar si al menos uno de los parámetros tiene un valor no nulo
  IF @first_name IS NULL AND @last_name IS NULL AND @second_last_name IS NULL AND @language_key IS NULL
  BEGIN
    RAISERROR('100, Al menos una variable no debe ser nula', 16, 1);
    RETURN;
  END

  -- Verificar si el id_user existe en la tabla user_mva
  IF NOT EXISTS (SELECT 1
  FROM user_mva
  WHERE id = @id_user)
  BEGIN
    RAISERROR('102, El usuario no existe', 16, 1);
    RETURN;
  END

  -- Carga de los valores previos
  DECLARE @current_first_name NVARCHAR(50), @current_last_name NVARCHAR(50),
   @current_second_last_name NVARCHAR (50), @current_language_key NVARCHAR(2);

  -- Obtener los valores actuales del usuario
  SELECT @current_first_name = first_name,
    @current_last_name = last_name,
    @current_second_last_name = second_last_name,
    @current_language_key = language_key
  FROM user_mva
  WHERE id = @id_user;

  -- Verificar si los parámetros proporcionados son iguales a los valores existentes
  IF (
      (@current_first_name = @first_name) OR (@current_last_name = @last_name) OR
    (@current_second_last_name = @second_last_name) OR (@current_language_key = @language_key)
    )
    BEGIN
    RAISERROR('103, El nuevo valor no puede ser igual al valor actual', 16, 1);
    RETURN;
  END

  BEGIN TRY
        BEGIN TRANSACTION

        -- Actualizar información personal que los usuarios pueden modificar por sí mismos
        UPDATE user_mva
        SET first_name = COALESCE(@first_name, first_name),
            last_name = COALESCE(@last_name, last_name),
            second_last_name = COALESCE(@second_last_name, second_last_name),
            language_key = COALESCE(@language_key, language_key)
        WHERE id = @id_user;

        COMMIT TRANSACTION
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
        RAISERROR (@ErrorMessage, 16, 1);
    END CATCH
END;

GO

-- ===============================================================
-- Author: Mario Martínez Lanuza
-- Create date: 2024-09-08
-- Description: Procedimiento para cambiar el nickname de un usuario
-- No debe ser nulo, debe ser diferente a los activos en la base y
-- diferente al del propio usuario
-- ===============================================================
IF EXISTS (SELECT *
FROM sys.objects
WHERE object_id = OBJECT_ID(N'[dbo].[sp_change_nickname]') AND type in (N'P', N'PC'))
BEGIN
  DROP PROCEDURE [dbo].[sp_change_nickname]
END
GO

CREATE PROCEDURE sp_change_nickname
  @id_user BIGINT,
  @NewNickname NVARCHAR(50)
AS
BEGIN
  SET NOCOUNT ON;
  BEGIN TRY
    BEGIN TRANSACTION

    -- Validar que el nuevo nickname no sea NULL
    IF @NewNickname IS NULL
    BEGIN
    RAISERROR('101, El valor no puede ser nulo', 16, 1);
    RETURN;
  END

   -- Verificar si el id_user existe en la tabla user_mva
  IF NOT EXISTS (SELECT 1
  FROM user_mva
  WHERE id = @id_user)
  BEGIN
    RAISERROR('102, El usuario no existe', 16, 1);
    RETURN;
  END

    -- Verificar si el nuevo nickname ya está en uso por otro usuario
    IF EXISTS(SELECT 1
  FROM user_mva
  WHERE nickname = @NewNickname AND id <> @id_user)
    BEGIN
    RAISERROR('105, El nickname ingresado ya existe', 16, 1);
    RETURN;
  END

  -- Verificar si el nuevo nickname no sea el mismo
    IF EXISTS(SELECT 1
  FROM user_mva
  WHERE nickname = @NewNickname AND id = @id_user)
    BEGIN
    RAISERROR('103, El nuevo valor no puede ser igual al valor actual', 16, 1);
    RETURN;
  END

    -- Actualizar el nickname si no está en uso
    UPDATE user_mva
    SET nickname = @NewNickname
    WHERE id = @id_user;

    COMMIT TRANSACTION
  END TRY
  BEGIN CATCH
    ROLLBACK TRANSACTION;
    DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
    RAISERROR(@ErrorMessage, 16, 1);
  END CATCH
END;
GO

-- ===============================================================
-- Author: Mario Martínez Lanuza
-- Create date: 2024-09-08
-- Description: Procedimiento para cambiar el email de un usuario
-- El usuario debe existir, y el email debe ser diferente utilizado
-- por otros usuario y el mismo usuario
-- ===============================================================
IF EXISTS (SELECT *
FROM sys.objects
WHERE object_id = OBJECT_ID(N'[dbo].[sp_change_email]') AND type in (N'P', N'PC'))
BEGIN
  DROP PROCEDURE [dbo].[sp_change_email]
END
GO

CREATE PROCEDURE sp_change_email
  @id_user BIGINT,
  @NewEmail NVARCHAR(254)
AS
BEGIN
  SET NOCOUNT ON;
  BEGIN TRY
    BEGIN TRANSACTION

    -- Verificar que el nuevo email no sea NULL
    IF @NewEmail IS NULL
    BEGIN
    RAISERROR('101, El valor no puede ser nulo', 16, 1);
    RETURN;
  END

    -- Verificar si el id_user existe en la tabla user_mva
  IF NOT EXISTS (SELECT 1
  FROM user_mva
  WHERE id = @id_user)
  BEGIN
    RAISERROR('102, El usuario no existe', 16, 1);
    RETURN;
  END

    -- Verificar si el nuevo email ya está en uso por otro usuario
    IF EXISTS(SELECT 1
  FROM user_mva
  WHERE email = @NewEmail AND id <> @id_user)
    BEGIN
    RAISERROR('106, El correo ingresado ya existe', 16, 1);
    RETURN;
  END

      -- Verificar si el nuevo email ya está en uso por otro usuario
    IF EXISTS(SELECT 1
  FROM user_mva
  WHERE email = @NewEmail AND id = @id_user)
    BEGIN
    RAISERROR('103, El nuevo valor no puede ser igual al valor actual', 16, 1);
    RETURN;
  END

    -- Actualizar el email si no está en uso
    UPDATE user_mva
    SET email = @NewEmail
    WHERE id = @id_user;

    COMMIT TRANSACTION
  END TRY
  BEGIN CATCH
    ROLLBACK TRANSACTION;
    DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
    RAISERROR(@ErrorMessage, 16, 1);
  END CATCH
END;
GO

-- ===============================================================
-- Author: Mario Martínez Lanuza
-- Create date: 2024-09-08
-- Description: Procedimiento para cambiar la contraseña de un usuario
-- Nota: La nueva contraseña debe ser enviada ya hasheada y no puede NULL
-- Debe ser diferente a la actual y el usuario debe existir
-- ===============================================================
IF EXISTS (SELECT *
FROM sys.objects
WHERE object_id = OBJECT_ID(N'[dbo].[sp_change_password]') AND type in (N'P', N'PC'))
BEGIN
  DROP PROCEDURE [dbo].[sp_change_password]
END
GO

CREATE PROCEDURE sp_change_password
  @id_user BIGINT,
  @NewPassword NVARCHAR(255),
  -- La nueva contraseña ya hasheada
  @CurrentPassword NVARCHAR(255)
-- La contraseña actual para verificación
AS
BEGIN
  SET NOCOUNT ON;

  BEGIN TRY
    BEGIN TRANSACTION

    -- Verifica que la nueva contraseña no sea NULL
    IF @NewPassword IS NULL
    BEGIN
    RAISERROR('101, El valor no puede ser nulo', 16, 1);
    RETURN
  END

      -- Verifica que la contraseña actual no sea NULL
    IF @CurrentPassword IS NULL
    BEGIN
    RAISERROR('107, La contraseña es nula', 16, 1);
    RETURN
  END

    -- Verificar si el id_user existe en la tabla user_mva
  IF NOT EXISTS (SELECT 1
  FROM user_mva
  WHERE id = @id_user)
  BEGIN
    RAISERROR('102, El usuario no existe', 16, 1);
    RETURN;
  END

  -- Verificar que la contraseña actual sea la misma que la de la base de datos
  IF NOT EXISTS (SELECT 1
  FROM user_mva
  WHERE id = @id_user AND password_hash = @CurrentPassword)
  BEGIN
    RAISERROR('108, La contraseña no coincide con la registrada', 16, 1);
    RETURN;
  END

  DECLARE @old_password NVARCHAR(255);

  -- Cargar la contraseña actual

  SELECT @old_password = password_hash
  FROM user_mva
  WHERE id = @id_user

  -- verificicar que la nueva contraseña sea diferente a la nueva
  IF @old_password = @NewPassword
  BEGIN
    RAISERROR('109, La nueva contraseña es igual a la contraseña vigente', 16, 1);
    RETURN;
  END

    -- Actualizar la contraseña hasheada
    UPDATE user_mva
    SET password_hash = @NewPassword
    WHERE id = @id_user;

    COMMIT TRANSACTION
  END TRY
  BEGIN CATCH
    ROLLBACK TRANSACTION;
    DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
    RAISERROR(@ErrorMessage, 16, 1);
  END CATCH
END;




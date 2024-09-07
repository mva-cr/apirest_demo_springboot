
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
CREATE TABLE authority
(
  name NVARCHAR(50) NOT NULL,
  CONSTRAINT PK_name_authority  PRIMARY KEY CLUSTERED (name)
)

GO
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

CREATE TABLE user_authority
(
  user_id SMALLINT NOT NULL,
  authority_name NVARCHAR(50) NOT NULL,
  CONSTRAINT PK_user_id_authority_name_user_auth PRIMARY KEY CLUSTERED (user_id, authority_name),
  CONSTRAINT FK_user_id_user_authority FOREIGN KEY (user_id) REFERENCES user_mva(id) ON DELETE CASCADE,
  CONSTRAINT FK_name_user_authority FOREIGN KEY (authority_name) REFERENCES authority(name) ON DELETE CASCADE
)




IF OBJECT_ID('SP_AddUserRoleIfExists', 'P') IS NOT NULL
    DROP PROCEDURE SP_AddUserRoleIfExists;
GO

CREATE PROCEDURE SP_AddUserRoleIfExists
  @UserId BIGINT
AS
BEGIN
  -- Verifica si el rol 'ROLE_USER' existe en la tabla 'authority'
  IF EXISTS (SELECT 1
  FROM authority
  WHERE name = 'ROLE_USER')
    BEGIN
    -- Si el rol existe, inserta el registro en 'user_authority'
    INSERT INTO user_authority
      (user_id, authority_name)
    VALUES
      (@UserId, 'ROLE_USER');
  END
-- Si el rol no existe, no hace nada
END;
GO

CREATE TRIGGER trg_AddUserRole
ON user_mva
AFTER INSERT
AS
BEGIN
  DECLARE @UserId BIGINT;

  -- Asumimos que el ID del usuario es generado automáticamente (IDENTITY)
  SELECT @UserId = id
  FROM inserted;

  -- Llama al procedimiento almacenado para agregar el rol si existe
  EXEC SP_AddUserRoleIfExists @UserId;
END;
GO

-- Trigger para que al cambiar el status a 0 en user_authority haga un update el roll dejandolo en ROLE_UNAUTHORIZE
-- y si cambia el status a 1 lo cambie a ROLE_USER
CREATE TRIGGER UpdateUserAuthority
ON user_mva
AFTER UPDATE
AS
BEGIN
  -- Actualizar a 'ROLE_USER' cuando el status es 1
  UPDATE ua
    SET ua.authority_name = 'ROLE_USER'
    FROM user_authority ua
    JOIN INSERTED i ON ua.user_id = i.id
    WHERE i.status = 1;

  -- Actualizar a 'ROLE_UNAUTHORIZE' cuando el status es 0
  UPDATE ua
    SET ua.authority_name = 'ROLE_UNAUTHORIZE'
    FROM user_authority ua
    JOIN INSERTED i ON ua.user_id = i.id
    WHERE i.status = 0;
END;



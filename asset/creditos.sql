use master
GO
DROP database IF EXISTS trade
GO
CREATE DATABASE trade
GO
USE trade;
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
  content NVARCHAR(MAX) NOT NULL,
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

-- Data

INSERT INTO country VALUES
('Costa Rica', 506),
('Nicaragua', 505);

INSERT INTO client_type VALUES
('Vip'),
('Regular');

INSERT INTO message_type VALUES
('email'),
('sms'),
('whatsapp');

INSERT INTO subscription VALUES
('enable'),
('desable'),
('standby');

INSERT INTO message VALUES
('Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse maximus blandit commodo. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Maecenas turpis turpis, pharetra sit amet commodo ut, vestibulum et dolor. Suspendisse potenti. Cras a lacinia mi, ac placerat nibh. Ut dignissim ex in eleifend convallis. Proin sed ipsum sagittis, efficitur ex eu, feugiat felis. 
Morbi euismod risus mattis eleifend pretium. Proin id pretium erat, in lacinia quam. Etiam et dignissim ligula. Vestibulum iaculis ultrices sodales. Suspendisse potenti. Sed eu elit neque. Nulla feugiat turpis elit, eu tempor lacus auctor molestie. Nunc id tellus nibh. Praesent vel vehicula urna. Ut sit amet neque blandit, viverra orci nec, dignissim massa. Donec vel varius enim. Maecenas consectetur sagittis pharetra. Nulla dapibus dolor id diam tempus vulputate','Invitación a feria gratuita de castración de perros en la zona de Oreamuno de Cartago','Feria de castración de perros en Oreamuno de Cartago el 2023-11-30'),
('El domingo 26 de noviembre a las 5:00 p.m. los invitamos al evento Melodiosa Navidad, un espectacular concierto con el coro Laus Deo, quienes interpretarán los tradicionales villancicos de la época.
Esta será una actividad bimodal: Presencial, exclusiva para colegiados, en el Auditorio de la Sede Central del Colegio (contiguo a la Rotonda de la Bandera). El cupo es limitado y la inscripción se habilitará este martes 21 de noviembre a partir de las 4:00 p.m. en la plataforma: Mi Colegio
También la pueden seguir por la cuenta de Facebook del Colegio.
Se solicita a quienes asisten presencialmente, colaborar con la entrega de 1 juguete nuevo, el cual donaremos a niños de recursos limitados por medio de la campaña Navidad y Sonrisas.','Melodiosa Navidad','Colegio de Ciencias Económicas informa'),
('En este evento híbrido se darán a conocer los resultados del Programa para la Evaluación Internacional de Alumnos (PISA) de la OCDE del año 2022. En la última medición participaron 14 países de la América Latina y el Caribe: Argentina, Brasil, Chile, Colombia, Costa Rica, República Dominicana, El Salvador, Guatemala, Jamaica, México, Panamá, Paraguay, Perú y Uruguay.

El objetivo es abordar los desafíos que los países de la región enfrentan en relación con los resultados de aprendizaje y promover acciones para una transformación educativa que permita avanzar hacia una educación inclusiva y de calidad.','INVITACIÓN | Evento de lanzamiento de PISA 2022 para América Latina y el Caribe (12/dic/2023)','Lanzamiento de PISA 2022 para América Latina y el Caribe');

INSERT INTO client VALUES
('Juan','Pérez', 'Prado','jperez@prado.com','Del la móvil 300 s, 50 n', GETDATE(), 1,1,1);
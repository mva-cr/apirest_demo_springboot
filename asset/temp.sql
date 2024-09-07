CREATE TABLE `countries` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `phone_code` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_1pyiwrqimi3hnl3vtgsypj5r` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `message` text NOT NULL,
  `subject` varchar(100) NOT NULL,
  `summary` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `message_type` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `subscription` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(15) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `customer_type` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(15) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `customers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(250) DEFAULT NULL,
  `create_at` date NOT NULL,
  `email` varchar(100) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `name` varchar(255) NOT NULL,
  `phone` varchar(9) NOT NULL,
  `second_last_name` varchar(50) DEFAULT NULL,
  `id_country` bigint NOT NULL,
  `id_customer_type` bigint NOT NULL,
  `id_message_type` bigint NOT NULL,
  `id_subscription` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_rfbvkrffamfql7cjmen8v976v` (`email`),
  KEY `FK8lwbe7ialhwlup257hyyn95fj` (`id_country`),
  KEY `FKhm2fh6svkcsgwe3qn8rdc2xgi` (`id_customer_type`),
  KEY `FK5swlu5xxveagadil80puaacx1` (`id_message_type`),
  KEY `FK56nkp640qyostob2gnidmxgmx` (`id_subscription`),
  CONSTRAINT `FK56nkp640qyostob2gnidmxgmx` FOREIGN KEY (`id_subscription`) REFERENCES `subscription` (`id`),
  CONSTRAINT `FK5swlu5xxveagadil80puaacx1` FOREIGN KEY (`id_message_type`) REFERENCES `message_type` (`id`),
  CONSTRAINT `FK8lwbe7ialhwlup257hyyn95fj` FOREIGN KEY (`id_country`) REFERENCES `countries` (`id`),
  CONSTRAINT `FKhm2fh6svkcsgwe3qn8rdc2xgi` FOREIGN KEY (`id_customer_type`) REFERENCES `customer_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
SELECT * FROM creditos.cliente
    WHERE numero_documento = pNumeroDocumento;
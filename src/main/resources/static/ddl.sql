-- db & tables created on fly
-- spring.jpa.hibernate.ddl-auto=update
-- spring.datasource.url=jdbc:mysql://localhost:3306/nimesa?createDatabaseIfNotExist=true


CREATE TABLE `job` (
  `id` varchar(255) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
)


CREATE TABLE `s3object` (
  `id` int(11) NOT NULL,
  `bucket_name` varchar(255) DEFAULT NULL,
  `object_name` varchar(255) DEFAULT NULL,
  `job_id` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK88` (`job_id`),
  CONSTRAINT `FK88` FOREIGN KEY (`job_id`) REFERENCES `job` (`id`)
)


CREATE TABLE `service_discovery_result` (
  `id` int(11) NOT NULL,
  `service_name` varchar(255) DEFAULT NULL,
  `result` varchar(255) DEFAULT NULL,
  `job_id` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK99` (`job_id`),
  CONSTRAINT `FK99` FOREIGN KEY (`job_id`) REFERENCES `job` (`id`)
)
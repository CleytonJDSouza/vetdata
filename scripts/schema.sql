CREATE DATABASE IF NOT EXISTS vetdata;

USE vetdata;

DROP USER IF EXISTS 'vetuser'@'%';
CREATE USER  'vetuser'@'%' IDENTIFIED BY 'vetpass';
GRANT ALL PRIVILEGES ON vetdata.* TO 'vetuser'@'%';
FLUSH PRIVILEGES;

CREATE TABLE IF NOT EXISTS post_operative (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    description TEXT NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1421 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS diagnostic (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    description TEXT NOT NULL,
    observation TEXT
) ENGINE=InnoDB AUTO_INCREMENT=1421 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `dog_breeds` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `description` text,
  `female_weight_max` double NOT NULL,
  `female_weight_min` double NOT NULL,
  `hypoallergenic` bit(1) NOT NULL,
  `id_external_api` varchar(255) DEFAULT NULL,
  `life_expectancy_max` int NOT NULL,
  `life_expectancy_min` int NOT NULL,
  `male_weight_max` double NOT NULL,
  `male_weight_min` double NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1421 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS health_record (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    age DOUBLE UNSIGNED,
    cod_patient VARCHAR(255),
    color VARCHAR(255),
    death BIT(1) DEFAULT 0,
    euthanasia ENUM('INDICATED', 'NO', 'YES') NOT NULL,
    gender ENUM('FEMALE', 'MALE') NOT NULL,
    patient VARCHAR(255) NOT NULL,
    size ENUM('LARGE', 'MEDIUM', 'SMALL') NOT NULL,
    tutor VARCHAR(255) NOT NULL,
    weight DOUBLE UNSIGNED,
    breed_id BIGINT UNSIGNED NOT NULL,
    CONSTRAINT fk_health_record_breed FOREIGN KEY (breed_id)
        REFERENCES dog_breeds(id) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1421 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS hospital_admission (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    reason_hospitalization ENUM('POS_OPERATORIO', 'GASTROENTEROLOGIA', 'DERMATOLOGIA', 'ONCOLOGIA', 'ORTOPEDIA_E_TRAUMATOLOGIA', 'NEFROLOGIA', 'NEUROLOGIA',
    'OFTALMOLOGIA', 'HEMATOLOGIA', 'OUTROS', 'UROLOGIA', 'PNEUMOLOGIA', 'ENDOCRINOLOGIA') NOT NULL,
    date_medical_discharge DATE NOT NULL,
    reason_medical_discharge VARCHAR(20) NOT NULL,
    date_return DATE,
    medical_evolution  ENUM('PROGRESSO_FAVORAVEL', 'PROGRESSO_DESFAVORAVEL', 'QUADRO_GRAVE', 'PCR'),
    treatment_next_steps ENUM('TRATAMENTO_EM_ANDAMENTO', 'ENCAMINHAMENTO_ESPECIALISTA', 'ENCAMINHAMENTO_HOVET_PUBLICO', 'TUTOR_INTERROMPEU_TRATAMENTO',
    'ALTA_MEDICA', 'CONTINUOU_TRATAMENTO', 'EUTANASIA_INDICADA', 'EUTANASIA', 'TUTOR_OPTOU_POR_EUTANASIA', 'PCR'),
    health_record_id BIGINT UNSIGNED NOT NULL,
    CONSTRAINT fk_health_record_id FOREIGN KEY (health_record_id)
		REFERENCES health_record(id) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1421 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS admission_post_operative (
    id_hospital_admission BIGINT UNSIGNED,
    id_post_operative BIGINT UNSIGNED,
    PRIMARY KEY (id_hospital_admission, id_post_operative),
    FOREIGN KEY (id_hospital_admission) REFERENCES hospital_admission(id) ON DELETE CASCADE,
    FOREIGN KEY (id_post_operative) REFERENCES post_operative(id) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1421 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS admission_diagnostic (
    id_hospital_admission BIGINT UNSIGNED NOT NULL,
    id_diagnostic BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (id_hospital_admission, id_diagnostic),
    FOREIGN KEY (id_hospital_admission) REFERENCES hospital_admission(id) ON DELETE CASCADE,
    FOREIGN KEY (id_diagnostic) REFERENCES diagnostic(id) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1421 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_date DATETIME NOT NULL,
    password_last_updated_date DATETIME NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1421 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;






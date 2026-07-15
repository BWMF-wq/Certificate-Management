CREATE TABLE student_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    student_id VARCHAR(30) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    school VARCHAR(100),
    major VARCHAR(100),
    graduation_year INT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL
);

CREATE TABLE certificates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    issuer VARCHAR(120) NOT NULL,
    category VARCHAR(30) NOT NULL,
    level VARCHAR(30) NOT NULL,
    issue_date DATE NOT NULL,
    expiry_date DATE,
    credential_no VARCHAR(100),
    credential_url VARCHAR(500),
    description VARCHAR(1000),
    file_name VARCHAR(255),
    stored_file_name VARCHAR(255),
    file_content_type VARCHAR(100),
    file_size BIGINT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_certificate_user FOREIGN KEY (user_id) REFERENCES student_users(id) ON DELETE CASCADE
);

CREATE INDEX idx_certificate_user ON certificates(user_id);
CREATE INDEX idx_certificate_category ON certificates(user_id, category);
CREATE INDEX idx_certificate_issue_date ON certificates(user_id, issue_date);
CREATE INDEX idx_certificate_expiry_date ON certificates(user_id, expiry_date);

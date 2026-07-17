ALTER TABLE certificates
    ADD COLUMN deleted_at DATETIME(6) NULL;

CREATE INDEX idx_certificate_deleted_at ON certificates(user_id, deleted_at);

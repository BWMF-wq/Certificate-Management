ALTER TABLE certificates
    ADD COLUMN award_type VARCHAR(30) NOT NULL DEFAULT 'INDIVIDUAL';

UPDATE certificates
SET level = 'PROVINCIAL_MUNICIPAL'
WHERE level IN ('PROVINCIAL', 'MUNICIPAL');

UPDATE certificates
SET level = 'OTHER'
WHERE level = 'INTERNATIONAL';

CREATE INDEX idx_certificate_level ON certificates(user_id, level);
CREATE INDEX idx_certificate_award_type ON certificates(user_id, award_type);

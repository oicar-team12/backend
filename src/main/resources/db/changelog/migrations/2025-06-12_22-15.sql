DELETE
FROM refresh_tokens;

ALTER TABLE refresh_tokens
    ALTER COLUMN token TYPE TEXT;

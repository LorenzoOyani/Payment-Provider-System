-- add index for better performance
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_status ON users(active);

-- update table with table relationship
ALTER TABLE payment ADD COLUMN user_id BIGINT not null;
ALTER TABLE payment ADD CONSTRAINT fk_payemnt_user FOREIGN KEY(user_id) REFERENCES users(id);
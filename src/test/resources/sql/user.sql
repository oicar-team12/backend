INSERT INTO users (id, first_name, last_name, email, password)
VALUES (1, 'John', 'Doe', 'john@email.com', '$2a$10$rc0ibs7g1YYQLiV0TqXJtejo7usGv5VT0gDUAI7R3XyxEUzoANwk6');

INSERT INTO groups (id, name)
VALUES (1, 'First Group');

INSERT INTO group_users (user_id, group_id, role)
VALUES (1, 1, 'EMPLOYEE');

INSERT INTO notifications (id, user_id, title, message, created_at, is_read)
VALUES (1, 1, 'Test Notification Title', 'Test Notification Message', now(), true);

INSERT INTO users (id, first_name, last_name, email, password, is_admin)
VALUES (1, 'John', 'Doe', 'john@email.com', '$2a$10$rc0ibs7g1YYQLiV0TqXJtejo7usGv5VT0gDUAI7R3XyxEUzoANwk6', true);
INSERT INTO users (id, first_name, last_name, email, password, is_admin)
VALUES (2, 'James', 'Moe', 'james@email.com', '$2a$10$rc0ibs7g1YYQLiV0TqXJtejo7usGv5VT0gDUAI7R3XyxEUzoANwk6', false);
INSERT INTO users (id, first_name, last_name, email, password, is_admin)
VALUES (3, 'Jane', 'Joe', 'jane@email.com', '$2a$10$rc0ibs7g1YYQLiV0TqXJtejo7usGv5VT0gDUAI7R3XyxEUzoANwk6', false);

INSERT INTO user_delete_requests (id, user_id)
VALUES (nextval('user_delete_requests_id_seq'), 2);
INSERT INTO user_delete_requests (id, user_id, is_approved)
VALUES (nextval('user_delete_requests_id_seq'), 3, false);

INSERT INTO groups (id, name)
VALUES (1, 'First Group');

INSERT INTO group_users (user_id, group_id, role)
VALUES (2, 1, 'EMPLOYEE');
INSERT INTO group_users (user_id, group_id, role)
VALUES (3, 1, 'EMPLOYEE');

INSERT INTO notifications (id, user_id, title, message, created_at, is_read)
VALUES (1, 2, 'Test Notification Title', 'Test Notification Message', now(), true);
INSERT INTO notifications (id, user_id, title, message, created_at, is_read)
VALUES (2, 3, 'Test Notification Title 2', 'Test Notification Message 2', now(), false);

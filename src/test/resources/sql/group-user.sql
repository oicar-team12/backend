INSERT INTO users (id, first_name, last_name, email, password)
VALUES (1, 'John', 'Doe', 'john@email.com', '$2a$10$rc0ibs7g1YYQLiV0TqXJtejo7usGv5VT0gDUAI7R3XyxEUzoANwk6');
INSERT INTO users (id, first_name, last_name, email, password)
VALUES (2, 'James', 'Moe', 'james@email.com', '$2a$10$rc0ibs7g1YYQLiV0TqXJtejo7usGv5VT0gDUAI7R3XyxEUzoANwk6');
INSERT INTO users (id, first_name, last_name, email, password)
VALUES (3, 'Jane', 'Joe', 'jane@email.com', '$2a$10$rc0ibs7g1YYQLiV0TqXJtejo7usGv5VT0gDUAI7R3XyxEUzoANwk6');

INSERT INTO groups (id, name)
VALUES (1, 'First Group');

INSERT INTO group_users (group_id, user_id, role)
VALUES (1, 1, 'MANAGER');
INSERT INTO group_users (group_id, user_id, role)
VALUES (1, 2, 'EMPLOYEE');

INSERT INTO users (id, first_name, last_name, email, password, is_admin)
VALUES (1, 'John', 'Doe', 'john@email.com',
        '$2a$10$rc0ibs7g1YYQLiV0TqXJtejo7usGv5VT0gDUAI7R3XyxEUzoANwk6', false);
INSERT INTO users (id, first_name, last_name, email, password, is_admin)
VALUES (2, 'James', 'Moe', 'james@email.com', '$2a$10$rc0ibs7g1YYQLiV0TqXJtejo7usGv5VT0gDUAI7R3XyxEUzoANwk6', false);

INSERT INTO groups (id, name)
VALUES (nextval('groups_id_seq'), 'First Group');

INSERT INTO group_users (group_id, user_id, role)
VALUES (1, 1, 'MANAGER');
INSERT INTO group_users (group_id, user_id, role)
VALUES (1, 2, 'EMPLOYEE');

INSERT INTO availabilities (user_id, date, is_available, group_id)
VALUES (1, '2020-01-01', true, 1);

INSERT INTO shifts (id, group_id, date, start_time, end_time)
VALUES (1, 1, '2020-01-01', '08:00:00', '12:00:00');

INSERT INTO schedules (shift_id, user_id)
VALUES (1, 1);

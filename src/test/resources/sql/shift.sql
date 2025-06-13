INSERT INTO users (id, first_name, last_name, email, password, is_admin)
VALUES (1, 'John', 'Doe', 'john@email.com', '$2a$10$rc0ibs7g1YYQLiV0TqXJtejo7usGv5VT0gDUAI7R3XyxEUzoANwk6', false);
INSERT INTO users (id, first_name, last_name, email, password, is_admin)
VALUES (2, 'James', 'Moe', 'james@email.com', '$2a$10$rc0ibs7g1YYQLiV0TqXJtejo7usGv5VT0gDUAI7R3XyxEUzoANwk6', false);
INSERT INTO users (id, first_name, last_name, email, password, is_admin)
VALUES (3, 'Jane', 'Joe', 'jane@email.com', '$2a$10$rc0ibs7g1YYQLiV0TqXJtejo7usGv5VT0gDUAI7R3XyxEUzoANwk6', false);

INSERT INTO groups (id, name)
VALUES (1, 'First Group');
INSERT INTO groups (id, name)
VALUES (2, 'Second Group');

INSERT INTO group_users (group_id, user_id, role)
VALUES (1, 1, 'MANAGER');
INSERT INTO group_users (group_id, user_id, role)
VALUES (1, 2, 'EMPLOYEE');
INSERT INTO group_users (group_id, user_id, role)
VALUES (2, 3, 'EMPLOYEE');

INSERT INTO shifts (id, group_id, date, start_time, end_time)
VALUES (nextval('shifts_id_seq'), 1, '2020-01-01', '08:00:00', '12:00:00');
INSERT INTO shifts (id, group_id, date, start_time, end_time)
VALUES (nextval('shifts_id_seq'), 1, '2021-01-01', '09:00:00', '13:00:00');
INSERT INTO shifts (id, group_id, date, start_time, end_time)
VALUES (nextval('shifts_id_seq'), 1, '2022-01-01', '07:00:00', '11:00:00');

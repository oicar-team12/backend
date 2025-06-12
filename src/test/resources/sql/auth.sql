INSERT INTO users (id, first_name, last_name, email, password)
VALUES (nextval('users_id_seq'), 'John', 'Doe', 'john@email.com',
        '$2a$10$rc0ibs7g1YYQLiV0TqXJtejo7usGv5VT0gDUAI7R3XyxEUzoANwk6');

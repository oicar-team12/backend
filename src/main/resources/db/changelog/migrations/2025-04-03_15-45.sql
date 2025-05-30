CREATE TYPE GROUP_USER_ROLE AS ENUM ('MANAGER', 'EMPLOYEE');

CREATE TABLE USERS
(
    ID         BIGSERIAL PRIMARY KEY,
    FIRST_NAME TEXT         NOT NULL,
    LAST_NAME  TEXT         NOT NULL,
    EMAIL VARCHAR(255) NOT NULL UNIQUE,
    PASSWORD   TEXT         NOT NULL
);

CREATE TABLE ACCESS_TOKENS
(
    ID         BIGSERIAL PRIMARY KEY,
    USER_ID    BIGSERIAL                                          NOT NULL REFERENCES USERS (ID),
    TOKEN      TEXT                                               NOT NULL,
    EXPIRES_AT TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE REFRESH_TOKENS
(
    ID         BIGSERIAL PRIMARY KEY,
    USER_ID    BIGSERIAL                                          NOT NULL REFERENCES USERS (ID),
    TOKEN      UUID                                               NOT NULL,
    EXPIRES_AT TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE GROUPS
(
    ID   BIGSERIAL PRIMARY KEY,
    NAME TEXT NOT NULL
);

CREATE TABLE GROUP_USERS
(
    GROUP_ID BIGSERIAL,
    USER_ID  BIGSERIAL,
    ROLE     GROUP_USER_ROLE NOT NULL,

    PRIMARY KEY (GROUP_ID, USER_ID)
);

CREATE TABLE NOTIFICATIONS
(
    ID         BIGSERIAL PRIMARY KEY,
    USER_ID    BIGSERIAL                                          NOT NULL REFERENCES USERS (ID),
    TITLE      TEXT                                               NOT NULL,
    MESSAGE    TEXT                                               NOT NULL,
    CREATED_AT TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    IS_READ    BOOLEAN                  DEFAULT FALSE             NOT NULL
);

CREATE TABLE SHIFTS
(
    ID         BIGSERIAL PRIMARY KEY,
    GROUP_ID   BIGSERIAL NOT NULL REFERENCES GROUPS (ID),
    DATE       DATE      NOT NULL,
    START_TIME TIME      NOT NULL,
    END_TIME   TIME      NOT NULL,

    CHECK (START_TIME < END_TIME)
);

CREATE TABLE SCHEDULES
(
    ID       BIGSERIAL PRIMARY KEY,
    SHIFT_ID BIGSERIAL NOT NULL REFERENCES SHIFTS (ID),
    USER_ID BIGSERIAL NOT NULL REFERENCES USERS (ID),

    UNIQUE (SHIFT_ID, USER_ID)
);

CREATE TABLE AVAILABILITIES
(
    ID           BIGSERIAL PRIMARY KEY,
    USER_ID      BIGSERIAL NOT NULL REFERENCES USERS (ID),
    DATE         DATE      NOT NULL,
    IS_AVAILABLE BOOLEAN NOT NULL,

    UNIQUE (USER_ID, DATE)
);

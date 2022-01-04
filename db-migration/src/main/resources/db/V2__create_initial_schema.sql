DROP TABLE IF EXISTS user_platform CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id serial NOT NULL PRIMARY KEY,
    firstname VARCHAR(30),
    lastname VARCHAR(30),
    email VARCHAR(50) NOT NULL,
    phone VARCHAR(30),
    default_language_id int,
    validated boolean,
    user_key VARCHAR(50),
    history_user_id int,
    active boolean,
    deleted boolean,
    created_date timestamp,
    modified_date timestamp,
    created_by VARCHAR(30),
    modified_by VARCHAR(30),
    log_key VARCHAR(30),
    password VARCHAR(200),
    unique(email)
);

CREATE TABLE IF NOT EXISTS user_platform (
    id serial NOT NULL PRIMARY KEY,
    roles VARCHAR(100) NOT NULL,
    platform VARCHAR(15) NOT NULL,
    user_id int NOT NULL
);

ALTER TABLE user_platform
    ADD FOREIGN KEY (user_id) REFERENCES users(id);
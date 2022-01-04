DROP TABLE IF EXISTS profile CASCADE;
DROP TABLE IF EXISTS skill CASCADE;
DROP TABLE IF EXISTS project CASCADE;
DROP TABLE IF EXISTS suggested_skill CASCADE;
DROP TABLE IF EXISTS skill_category CASCADE;
DROP TABLE IF EXISTS project_skill CASCADE;
DROP TABLE IF EXISTS profile_skill CASCADE;
DROP TABLE IF EXISTS skill CASCADE;
DROP TABLE IF EXISTS category_skill_relation CASCADE;

CREATE TABLE IF NOT EXISTS profile (
    id serial NOT NULL PRIMARY KEY,
    profile_picture VARCHAR,
    firstname VARCHAR(30),
    lastname VARCHAR(30),
    email VARCHAR(50),
    phone VARCHAR(30),
    summary VARCHAR(250),
    role VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS spoken_languages (
    spoken_language VARCHAR(50),
    profile_id INT,
    UNIQUE(spoken_language,profile_id)
);

CREATE TABLE IF NOT EXISTS suggested_skill (
    id serial NOT NULL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    category_id INT NOT NULL,
    user_id INT NOT NULL,
    UNIQUE(name, user_id)
);

CREATE TABLE IF NOT EXISTS skill_category (
    id serial NOT NULL PRIMARY KEY,
    category_key VARCHAR(30) NOT NULL UNIQUE,
    category_name VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS skill (
    id serial NOT NULL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    level VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS project_skill (
    project_id INT NOT NULL,
    skill_id INT NOT NULL,
    UNIQUE(project_id, skill_id)
);

CREATE TABLE IF NOT EXISTS project (
    id serial NOT NULL PRIMARY KEY,
    title VARCHAR(30) NOT NULL,
    short_description VARCHAR(250) NOT NULL,
    period VARCHAR(50) NOT NULL,
    responsibilities VARCHAR(500) NOT NULL,
    profile_id INT NOT NULL
);

CREATE TABLE IF NOT EXISTS profile_skill (
    profile_id INT NOT NULL,
    skill_id INT NOT NULL,
    UNIQUE(profile_id, skill_id)
);

CREATE TABLE IF NOT EXISTS category_skill_relation (
    category_id INT NOT NULL,
    skill_id INT NOT NULL,
    UNIQUE(category_id, skill_id)
);


ALTER TABLE spoken_languages
    ADD FOREIGN KEY (profile_id) REFERENCES profile(id);

ALTER TABLE suggested_skill
    ADD FOREIGN KEY (category_id) REFERENCES skill_category(id),
    ADD FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE project_skill
    ADD FOREIGN KEY (project_id) REFERENCES project(id),
    ADD FOREIGN KEY (skill_id) REFERENCES skill(id);

ALTER TABLE project
    ADD FOREIGN KEY (profile_id) REFERENCES profile(id);

ALTER TABLE profile_skill
    ADD FOREIGN KEY (profile_id) REFERENCES profile(id),
    ADD FOREIGN KEY (skill_id) REFERENCES skill(id);

ALTER TABLE category_skill_relation
    ADD FOREIGN KEY (category_id) REFERENCES skill_category(id),
    ADD FOREIGN KEY (skill_id) REFERENCES skill(id);


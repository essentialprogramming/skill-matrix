--Add profile id to users

DROP PROCEDURE IF EXISTS ADD_PROFILE_ID_TO_USERS;
CREATE PROCEDURE ADD_PROFILE_ID_TO_USERS()
    language plpgsql
as $$
BEGIN
ALTER TABLE users
ADD COLUMN profile_id INT,
ADD FOREIGN KEY(profile_id) REFERENCES profile(id);
END $$;
CALL ADD_PROFILE_ID_TO_USERS();
DROP PROCEDURE ADD_PROFILE_ID_TO_USERS;
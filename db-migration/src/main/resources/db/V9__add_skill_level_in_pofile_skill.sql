DROP PROCEDURE IF EXISTS ADD_SKILL_LEVEL_COLUMN_IN_PROFILE_SKILL;
CREATE PROCEDURE ADD_SKILL_LEVEL_COLUMN_IN_PROFILE_SKILL()
    language plpgsql
as $$
BEGIN
ALTER TABLE profile_skill
    ADD COLUMN skill_level varchar(30);
END $$;
CALL ADD_SKILL_LEVEL_COLUMN_IN_PROFILE_SKILL();
DROP PROCEDURE ADD_SKILL_LEVEL_COLUMN_IN_PROFILE_SKILL;
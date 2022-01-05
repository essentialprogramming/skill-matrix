-- Add skill key in skill table and project key in project table

DROP PROCEDURE IF EXISTS ADD_SKILL_KEY;
CREATE PROCEDURE ADD_SKILL_KEY()
language plpgsql
as $$
BEGIN
  ALTER TABLE skill
  ADD COLUMN skill_key VARCHAR(300);
END $$;
CALL ADD_SKILL_KEY();
DROP PROCEDURE ADD_SKILL_KEY;

DROP PROCEDURE IF EXISTS ADD_PROJECT_KEY;
CREATE PROCEDURE ADD_PROJECT_KEY()
language plpgsql
as $$
BEGIN
  ALTER TABLE project
  ADD COLUMN project_key VARCHAR(300);
END $$;
CALL ADD_PROJECT_KEY();
DROP PROCEDURE ADD_PROJECT_KEY;
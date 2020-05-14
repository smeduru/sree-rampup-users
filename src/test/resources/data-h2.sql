INSERT INTO USER (id, first_name, last_name, email) VALUES ('2c79421-f5c5-45b7-afe3-711b68ddd1b7', 'Damon', 'Alvarez', 'damon.alvarez@test.com')

INSERT INTO ROLE (id, name) VALUES ('97b2b8ba-06e6-464a-a5b1-20e165308cad', 'GOOGLE_ADMIN_ROLE')

INSERT INTO USER_ROLES(USER_ID, ROLE_ID) VALUES('2c79421-f5c5-45b7-afe3-711b68ddd1b7', '97b2b8ba-06e6-464a-a5b1-20e165308cad')

INSERT INTO PERMISSION (id, is_enabled, name, role_id) VALUES ('587c0573-6f70-44ac-a077-a4f2df2c53c5', true, 'READONLY_PERMISSION', '97b2b8ba-06e6-464a-a5b1-20e165308cad')
INSERT INTO PERMISSION (id, is_enabled, name, role_id) VALUES ('f37a5eea-b5a1-4293-bddd-7485d817003e', true, 'REPORTS_PERMISSION', '97b2b8ba-06e6-464a-a5b1-20e165308cad')
INSERT INTO PERMISSION (id, is_enabled, name, role_id) VALUES ('70b20d92-6c04-4022-ad02-40eb446a476f', true, 'POLICY_PERMISSION', '97b2b8ba-06e6-464a-a5b1-20e165308cad')

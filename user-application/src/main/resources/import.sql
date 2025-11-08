INSERT INTO tb_users (username, password) VALUES ('joaozinho@gmail.com', '$2a$10$wkgAMyT3USNlpaFWGWf3PO1X/5E6pRoyYsCEAWQTU95Q5EhRtuTw2');
INSERT INTO tb_users (username, password) VALUES ( 'pedrinho@gmail.com', '$2a$10$wkgAMyT3USNlpaFWGWf3PO1X/5E6pRoyYsCEAWQTU95Q5EhRtuTw2');

INSERT INTO tb_role (role_description, role_name) VALUES ('Administrator role can create, delete, update or get data from other users','ROLE_ADMIN');
INSERT INTO tb_role (role_description, role_name) VALUES ('User role can update, delete or get his own user info','ROLE_USER');

INSERT INTO tb_user_role(role_id, user_id) VALUES (1,1);
INSERT INTO tb_user_role(role_id, user_id) VALUES (1,2);
INSERT INTO tb_user_role(role_id, user_id) VALUES (2,2);








insert into user_table (user_name, hashed_password, display_name, email, user_type, user_state, created)
	values ('admin', '$argon2i$v=19$m=65536,t=4,p=1$8EoC66mBIlqOCO168AuLag$mvVRguZ2RovfPoRuvkZeoVuZ9HA6oSGZym5BHI8wSio', 'Test Admin User', 'admin@docker.devtest', 'ADMIN', 'ACTIVE', current_timestamp);

insert into user_table (user_name, hashed_password, display_name, email, user_type, user_state, created)
	values ('test', '$argon2i$v=19$m=65536,t=4,p=1$8EoC66mBIlqOCO168AuLag$mvVRguZ2RovfPoRuvkZeoVuZ9HA6oSGZym5BHI8wSio', 'Test User', 'test@docker.devtest', 'USER', 'ACTIVE', current_timestamp);

insert into user_table (user_name, hashed_password, display_name, email, user_type, user_state, two_factor_type, two_factor_secret, created)
	values ('totp', '$argon2i$v=19$m=65536,t=4,p=1$8EoC66mBIlqOCO168AuLag$mvVRguZ2RovfPoRuvkZeoVuZ9HA6oSGZym5BHI8wSio', 'Test Admin User', 'admin@docker.devtest', 'ADMIN', 'ACTIVE', 'TOTP', 'sad', current_timestamp);


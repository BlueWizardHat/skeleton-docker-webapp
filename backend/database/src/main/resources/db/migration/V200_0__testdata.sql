
insert into user_table (user_name, hashed_password, display_name, email, created)
	values ('test', '$argon2i$v=19$m=65536,t=4,p=1$8EoC66mBIlqOCO168AuLag$mvVRguZ2RovfPoRuvkZeoVuZ9HA6oSGZym5BHI8wSio', 'Test User', 'test@docker.devtest', current_timestamp);


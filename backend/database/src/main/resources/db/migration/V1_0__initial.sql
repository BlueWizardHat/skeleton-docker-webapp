
create sequence bananafish_id_seq increment 50;

create table bananafish (
	id			bigserial primary key,
	name		varchar(128) not null,
	created		timestamp with time zone not null
);

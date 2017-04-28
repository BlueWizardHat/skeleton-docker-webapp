
create table user_table (
	id						bigserial primary key not null,
	user_name				varchar(64) not null constraint user_name_key unique,
	hashed_password			varchar(128) not null,
	display_name			varchar(128) not null,
	email					varchar(128) not null,
	user_type				varchar(16) not null,
	user_state				varchar(16) not null,
	two_factor_type			varchar(16),
	two_factor_secret		varchar(64),
	created					timestamp with time zone not null,
	last_login				timestamp with time zone,
	last_login_attempt		timestamp with time zone,
	failed_login_attempts	integer,
	version					bigint not null default 1
);

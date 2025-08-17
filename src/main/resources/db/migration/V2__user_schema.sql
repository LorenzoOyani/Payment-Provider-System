create table users(
    id BIGSERIAL PRIMARY KEY ,
    email varchar(255) not null,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    password varchar(255) not null,
    active Boolean not null  DEFAULT true,
    created_at timestamp not null,
    updated_at timestamp
)






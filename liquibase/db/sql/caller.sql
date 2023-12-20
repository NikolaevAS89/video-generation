create sequence caller_sequence;

create table callback(
    id int,
    phone varchar,
    name varchar,
    email varchar,
    creation timestamp,
    primary key (id)
);

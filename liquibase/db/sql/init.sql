create table templates(
    id uuid,
    name varchar,
    status varchar,
    creation timestamp,
    primary key (id)
);

create table transcript(
    id uuid,
    template_id uuid,
    transcript jsonb,
    chosen jsonb,
    creation timestamp,
    primary key (id)
);

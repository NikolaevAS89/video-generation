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
    creation timestamp,
    primary key (id)
);

create table audio_template(
    id uuid,
    template_id uuid,
    mapping jsonb,
    chosen jsonb,
    creation timestamp,
    primary key (id)
);

create table processed(
    id uuid,
    audio_template_id uuid,
    words jsonb,
    status varchar,
    creation timestamp,
    primary key (id)
);

create table demo_request(
    id uuid,
    callback_id uuid,
    template_id uuid,
    words jsonb,
    primary key (id)
);

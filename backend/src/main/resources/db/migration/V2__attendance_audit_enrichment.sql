alter table attendance
    add column if not exists hours_worked numeric(10,2),
    add column if not exists imported_position varchar(255);

alter table audit_logs
    add column if not exists actor_name varchar(255),
    add column if not exists actor_dni varchar(255),
    add column if not exists actor_position varchar(255);

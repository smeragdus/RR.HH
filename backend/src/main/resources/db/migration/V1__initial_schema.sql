create table employees (
    id bigserial primary key,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    dni varchar(255) not null,
    phone varchar(255),
    email varchar(255) not null,
    position varchar(255),
    area varchar(255),
    location varchar(255),
    employment_status varchar(30) not null,
    created_at timestamp not null,
    updated_at timestamp,
    updated_by varchar(255),
    constraint uk_employee_dni unique (dni)
);

create table users (
    id bigserial primary key,
    email varchar(255) not null unique,
    password varchar(255) not null,
    role varchar(30) not null,
    status varchar(30) not null,
    created_at timestamp not null,
    employee_id bigint references employees(id)
);

create table documents (
    id bigserial primary key,
    employee_id bigint references employees(id),
    request_id bigint,
    contract_id bigint,
    original_name varchar(255) not null,
    stored_name varchar(255) not null,
    path varchar(1000) not null,
    content_type varchar(255),
    size bigint,
    uploaded_by varchar(255),
    uploaded_at timestamp not null
);

create table attendance (
    id bigserial primary key,
    employee_id bigint not null references employees(id),
    work_date date not null,
    check_in time,
    check_out time,
    status varchar(30) not null,
    late boolean not null,
    justification varchar(1000),
    justification_document_id bigint references documents(id),
    constraint uk_attendance_employee_date unique (employee_id, work_date)
);

create table absence_requests (
    id bigserial primary key,
    employee_id bigint not null references employees(id),
    type varchar(30) not null,
    start_date date not null,
    end_date date not null,
    reason varchar(1000) not null,
    status varchar(30) not null,
    rejection_reason varchar(1000),
    registered_at timestamp not null,
    reviewed_at timestamp,
    reviewed_by varchar(255)
);

create table contracts (
    id bigserial primary key,
    employee_id bigint not null references employees(id),
    contract_type varchar(255) not null,
    start_date date not null,
    end_date date,
    status varchar(30) not null
);

alter table documents add constraint fk_documents_request foreign key (request_id) references absence_requests(id);
alter table documents add constraint fk_documents_contract foreign key (contract_id) references contracts(id);

create table audit_logs (
    id bigserial primary key,
    user_id bigint,
    actor_email varchar(255) not null,
    action varchar(255) not null,
    module varchar(255) not null,
    occurred_at timestamp not null,
    affected_record varchar(255),
    description varchar(1000)
);

create table notifications (
    id bigserial primary key,
    recipient_id bigint references users(id),
    message varchar(255) not null,
    read boolean not null,
    created_at timestamp not null
);

create table system_config (
    id bigserial primary key,
    config_key varchar(255) not null unique,
    config_value varchar(255) not null
);

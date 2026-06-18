create table employees (
    id bigserial primary key,
    first_name character varying(255) not null,
    last_name character varying(255) not null,
    dni character varying(255) not null,
    phone character varying(255),
    email character varying(255) not null,
    position character varying(255),
    area character varying(255),
    location character varying(255),
    employment_status character varying(30) not null,
    created_at timestamp not null,
    updated_at timestamp,
    updated_by character varying(255),
    constraint uk_employee_dni unique (dni)
);

create table users (
    id bigserial primary key,
    email character varying(255) not null unique,
    password character varying(255) not null,
    role character varying(30) not null,
    status character varying(30) not null,
    created_at timestamp not null,
    employee_id bigint references employees(id)
);

create table documents (
    id bigserial primary key,
    employee_id bigint references employees(id),
    request_id bigint,
    contract_id bigint,
    original_name character varying(255) not null,
    stored_name character varying(255) not null,
    path character varying(1000) not null,
    content_type character varying(255),
    size bigint,
    uploaded_by character varying(255),
    uploaded_at timestamp not null
);

create table attendance (
    id bigserial primary key,
    employee_id bigint not null references employees(id),
    work_date date not null,
    check_in time,
    check_out time,
    status character varying(30) not null,
    late boolean not null,
    justification character varying(1000),
    justification_document_id bigint references documents(id),
    constraint uk_attendance_employee_date unique (employee_id, work_date)
);

create table absence_requests (
    id bigserial primary key,
    employee_id bigint not null references employees(id),
    type character varying(30) not null,
    start_date date not null,
    end_date date not null,
    reason character varying(1000) not null,
    status character varying(30) not null,
    rejection_reason character varying(1000),
    registered_at timestamp not null,
    reviewed_at timestamp,
    reviewed_by character varying(255)
);

create table contracts (
    id bigserial primary key,
    employee_id bigint not null references employees(id),
    contract_type character varying(255) not null,
    start_date date not null,
    end_date date,
    status character varying(30) not null
);

alter table documents add constraint fk_documents_request foreign key (request_id) references absence_requests(id);
alter table documents add constraint fk_documents_contract foreign key (contract_id) references contracts(id);

create table audit_logs (
    id bigserial primary key,
    user_id bigint,
    actor_email character varying(255) not null,
    action character varying(255) not null,
    module character varying(255) not null,
    occurred_at timestamp not null,
    affected_record character varying(255),
    description character varying(1000)
);

create table notifications (
    id bigserial primary key,
    recipient_id bigint references users(id),
    message character varying(255) not null,
    read boolean not null,
    created_at timestamp not null
);

create table system_config (
    id bigserial primary key,
    config_key character varying(255) not null unique,
    config_value character varying(255) not null
);

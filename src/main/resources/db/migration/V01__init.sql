    create table permission (
       id varchar(50) not null,
        is_enabled boolean default true not null,
        name varchar(50) not null,
        role_id varchar(50),
        primary key (id)
    ) engine=MyISAM;

    create table role (
       id varchar(50) not null,
        name varchar(50) not null,
        primary key (id)
    ) engine=MyISAM;

    create table user (
       id varchar(50) not null,
        email varchar(50) not null,
        first_name varchar(50) not null,
        last_name varchar(50) not null,
        primary key (id)
    ) engine=MyISAM;

    create table user_roles (
       user_id varchar(50) not null,
        role_id varchar(50) not null,
        primary key (user_id, role_id)
    ) engine=MyISAM;

    alter table permission
       add constraint FKrvhjnns4bvlh4m1n97vb7vbar
       foreign key (role_id)
       references role (id);

    alter table user_roles
       add constraint FKrhfovtciq1l558cw6udg0h0d3
       foreign key (role_id)
       references role (id);

    alter table user_roles
       add constraint FK55itppkw3i07do3h7qoclqd4k
       foreign key (user_id)
       references user (id);
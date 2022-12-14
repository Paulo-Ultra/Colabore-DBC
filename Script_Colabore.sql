create table usuario
(
    id_usuario numeric not null,
    nome       text    not null,
    email      text    not null unique,
    senha      text    not null,
    foto       text,
    primary key (id_usuario)
);

create sequence seq_usuario
    increment 1
start 1;


create table campanha
(
    id_campanha              numeric       not null,
    id_usuario               numeric       not null,
    meta                     decimal(8, 2) not null,
    arrecadacao              decimal(8, 2) not null,
    titulo                   text          not null,
    descricao                text          not null,
    foto_capa                text          not null,
    encerrar_automaticamente boolean       not null,
    status_meta              boolean       not null,
    data_limite              timestamp     not null,
    ultima_alteracao         timestamp     not null,
    primary key (id_campanha),
    constraint fk_campanha_usuario
        foreign KEY (id_usuario)
            references usuario (id_usuario) on delete cascade
);

create sequence seq_campanha
    increment 1
start 1;

create table tag
(
    id_tag   numeric not null,
    nome_tag text    not null unique,
    primary key (id_tag)
);

create sequence seq_tag
    increment 1
start 1;

create table doador
(
    id_doador  numeric       not null,
    id_usuario numeric       not null,
    valor      decimal(8, 2) not null,
    primary key (id_doador),
    constraint fk_doador_usuario
        foreign KEY (id_usuario)
            references usuario (id_usuario) on delete cascade
);

create table campanha_x_tag
(
    id_campanha numeric not null,
    id_tag      numeric not null,
    primary key (id_campanha, id_tag),
    constraint fk_x_campanha
        foreign key (id_campanha)
            references campanha (id_campanha) on delete cascade,
    constraint fk_x_tag
        foreign key (id_tag)
            references tag (id_tag) on delete cascade
);

create sequence seq_doador
    increment 1
start 1;

create table campanha_x_doador
(
    id_campanha numeric not null,
    id_doador   numeric not null,
    primary key (id_campanha, id_doador),
    constraint fk_x_campanha
        foreign key (id_campanha)
            references campanha (id_campanha) on delete cascade,
    constraint fk_x_doador
        foreign key (id_doador)
            references doador (id_doador) on delete cascade
);

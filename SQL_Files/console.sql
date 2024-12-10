
create table if not exists activitati_profesori
(
    data_inceput   datetime    null,
    tip_activitate varchar(30) null,
    data_final     datetime    null,
    id_activitate  int         not null
        primary key
);

create table if not exists activitati_studenti
(
    data                     datetime    null,
    numar_ore                int         null,
    numar_minim_participanti int         null,
    timp_expirare            datetime    null,
    id_activitate            int         not null
        primary key,
    nume                     varchar(30) null
);

create table if not exists calendar
(
    cursuri int null
);

create table if not exists catalog
(
    curs varchar(30) null
);

create table if not exists cursuri
(
    CNP_profesor         varchar(13) not null,
    activitati_profesori varchar(30) null,
    nume                 varchar(30) not null,
    id                   int         not null
        primary key
);

create table if not exists grupuri_studenti
(
    materie       varchar(30) null,
    id_activitate int         null,
    id_grup       int         not null
        primary key,
    constraint grupuri_studenti_id_activitate_fk
        foreign key (id_activitate) references activitati_studenti (id_activitate)
);

create table if not exists utilizatori
(
    CNP            varchar(13)  not null
        primary key,
    nume           varchar(30)  not null,
    prenume        varchar(30)  not null,
    adresa         varchar(100) not null,
    numar_telefon  varchar(10)  not null,
    email          varchar(30)  not null,
    IBAN           varchar(50)  not null,
    numar_contract int          null,
    tip_utilizator varchar(20)  not null
);


create table if not exists profesori
(
    curs                    varchar(30) not null,
    numar_maxim_ore_predate int         null,
    numar_minim_ore_predate int         null,
    departament             varchar(30) not null,
    CNP                     varchar(13) not null,
    constraint profesori_utilizatori_CNP_fk
        foreign key (CNP) references utilizatori (CNP)
);

create table if not exists profesori_activitati_profesori
(
    id_activitate int         null,
    CNP_profesor  varchar(13) null,
    constraint profesori_activitati_profesori_profesori_CNP_fk
        foreign key (CNP_profesor) references profesori (CNP),
    constraint profesori_id_activitate_fk
        foreign key (id_activitate) references activitati_profesori (id_activitate)
);

create table if not exists studenti
(
    an_de_studiu        int         null,
    CNP                 varchar(13) not null,
    numar_ore_sustinute int         null,
    constraint studenti_utilizatori_CNP_fk
        foreign key (CNP) references utilizatori (CNP)
);

create table if not exists studenti_cursuri
(
    CNP_student varchar(13) null,
    id_curs     int         null,
    constraint studenti_cursuri_cursuri_id_fk
        foreign key (id_curs) references cursuri (id),
    constraint studenti_cursuri_studenti_CNP_fk
        foreign key (CNP_student) references studenti (CNP)
);

create table if not exists studenti_grupuri_studenti
(
    CNP_student varchar(13) null,
    id_grup     int         null,
    constraint studenti_grupuri_studenti_id_grup_fk
        foreign key (id_grup) references grupuri_studenti (id_grup),
    constraint studenti_grupuri_studenti_studenti_CNP_fk
        foreign key (CNP_student) references studenti (CNP)
);


alter table utilizatori
    add rol varchar(20) null;

alter table utilizatori
    add column_name int null;

alter table utilizatori
    add constraint check_name
        check (rol in ('administrator', 'super-administrator', 'student', 'profesor'));

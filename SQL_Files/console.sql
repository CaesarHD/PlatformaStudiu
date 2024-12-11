create table if not exists activitati_profesori
(
    data_inceput        datetime    null,
    tip_activitate      varchar(30) null,
    data_final          datetime    null,
    id_activitate       int         not null
        primary key,
    nr_max_participanti int         null,
    descriere           text        null
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

create table if not exists grupuri_studenti
(
    materie       varchar(30) null,
    id_activitate int         null,
    id_grup       int         not null
        primary key,
    constraint grupuri_studenti_id_activitate_fk
        foreign key (id_activitate) references activitati_studenti (id_activitate)
);

create table if not exists materii
(
    activitati_profesori varchar(30) null,
    nume                 varchar(30) not null,
    id                   int         not null
        primary key,
    pondere_curs         int         null,
    pondere_lab          int         null,
    pondere_seminar      int         null
);

create table if not exists utilizatori
(
    CNP            varchar(13)                                                          not null
        primary key,
    nume           varchar(30)                                                          not null,
    prenume        varchar(30)                                                          not null,
    adresa         varchar(100)                                                         not null,
    numar_telefon  varchar(10)                                                          not null,
    email          varchar(30)                                                          not null,
    IBAN           varchar(50)                                                          not null,
    numar_contract int                                                                  null,
    tip_utilizator varchar(20)                                                          not null,
    rol            enum ('administrator', 'super-administrator', 'profesor', 'student') null,
    column_name    int                                                                  null
);

create table if not exists detalii_profesori
(
    materie                 varchar(30) not null,
    numar_maxim_ore_predate int         null,
    numar_minim_ore_predate int         null,
    departament             varchar(30) not null,
    CNP                     varchar(13) not null,
    constraint detalii_profesori_utilizatori_CNP_fk
        foreign key (CNP) references utilizatori (CNP)
);

create table if not exists detalii_studenti
(
    an_de_studiu        int         null,
    CNP                 varchar(13) not null,
    numar_ore_sustinute int         null,
    constraint detalii_studenti_utilizatori_CNP_fk
        foreign key (CNP) references utilizatori (CNP)
);

create table if not exists materii_studenti
(
    CNP_student  varchar(13) null,
    id_materie   int         null,
    nota_lab     float       null,
    nota_curs    float       null,
    nota_seminar float       null,
    nota_finala  int         null,
    constraint materii_studenti_detalii_studenti_CNP_fk
        foreign key (CNP_student) references detalii_studenti (CNP),
    constraint materii_studenti_materii_id_fk
        foreign key (id_materie) references materii (id)
);

create table if not exists profesori_activitati_profesori
(
    id_activitate int         null,
    CNP_profesor  varchar(13) null,
    constraint profesori_activitati_profesori_detalii_profesori_CNP_fk
        foreign key (CNP_profesor) references detalii_profesori (CNP),
    constraint profesori_id_activitate_fk
        foreign key (id_activitate) references activitati_profesori (id_activitate)
);

create table if not exists profesori_materii
(
    CNP_profesor varchar(13) null,
    id_materie   int         null,
    constraint profesori_materii_detalii_profesori_CNP_fk
        foreign key (CNP_profesor) references detalii_profesori (CNP),
    constraint profesori_materii_materii_id_fk
        foreign key (id_materie) references materii (id)
);

create table if not exists studenti_grupuri_studenti
(
    CNP_student varchar(13) null,
    id_grup     int         null,
    constraint studenti_grupuri_studenti_detalii_studenti_CNP_fk
        foreign key (CNP_student) references detalii_studenti (CNP),
    constraint studenti_grupuri_studenti_id_grup_fk
        foreign key (id_grup) references grupuri_studenti (id_grup)
);



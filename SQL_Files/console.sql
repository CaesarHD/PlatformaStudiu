create database proiect;
use proiect;

create table if not exists activitati_studenti
(
    data                     datetime    null,
    numar_ore                int         null,
    numar_minim_participanti int         null,
    timp_expirare            datetime    null,
    id_activitate            int         auto_increment
        primary key,
    nume                     varchar(30) null
);

create table if not exists materii
(
    nume            varchar(30) not null,
    id              int auto_increment
        primary key,
    pondere_curs    int        null,
    pondere_lab     int         null,
    pondere_seminar int         null,
    constraint materii_pk
        unique (nume)
);

create table if not exists grupuri_studenti
(
    id_materie    int         null,
    id_activitate int         null,
    id_grup       int         auto_increment
        primary key,
    constraint grupuri_studenti_id_activitate_fk
        foreign key (id_activitate) references activitati_studenti (id_activitate),
    constraint grupuri_studenti_materii_id_fk
        foreign key (id_materie) references materii (id)
);

create table if not exists utilizatori
(
    CNP            varchar(13)                                                          not null
        primary key,
    nume           varchar(30)                                                          not null,
    prenume        varchar(30)                                                          not null,
    adresa         varchar(100)                                                         not null,
    numar_telefon  varchar(10)                                                          not null,
    email          varchar(100)                                                         not null
        unique key,
    parola         varchar(50)                                                          not null,
    IBAN           varchar(50)                                                          not null,
    numar_contract int                                                                  null,
    tip_utilizator enum ('administrator', 'super-administrator', 'profesor', 'student') null
);

create table if not exists detalii_profesori
(
    numar_maxim_ore_predate int         null,
    numar_minim_ore_predate int         null,
    departament             varchar(30) not null,
    CNP                     varchar(13) not null,
    constraint detalii_profesori_utilizatori_CNP_fk
        foreign key (CNP) references utilizatori (CNP)
            on delete cascade
);

create table if not exists activitati_profesori
(
    data_inceput        datetime    null,
    tip_activitate      enum('curs', 'laborator', 'seminar'),
    data_final          datetime    null,
    id_activitate       int         auto_increment
        primary key,
    nr_max_participanti int         null,
    descriere           text        null,
    CNP_profesor        varchar(13) null,
    id_materie          int         null,
    constraint activitati_profesori_detalii_profesori_CNP_fk
        foreign key (CNP_profesor) references detalii_profesori (CNP)
            on delete cascade,
    constraint activitati_profesori_materii_id_fk
        foreign key (id_materie) references materii (id)
);

create table if not exists detalii_studenti
(
    an_de_studiu        int         null,
    CNP                 varchar(13) not null,
    numar_ore_sustinute int         null,
    constraint detalii_studenti_utilizatori_CNP_fk
        foreign key (CNP) references utilizatori (CNP)
            on delete cascade
);

create table if not exists materii_studenti
(
    CNP_student  varchar(13) null,
    CNP_profesor varchar(13) null,
    id_materie   int         null,
    nota_finala  int        null,
    constraint materii_studenti_detalii_studenti_CNP_fk
        foreign key (CNP_student) references detalii_studenti (CNP),
    constraint materii_studenti_materii_id_fk
        foreign key (id_materie) references materii (id),
    constraint materii_studenti_profesor_id_fk
        foreign key (CNP_profesor) references detalii_profesori (CNP)
);

create table if not exists note_activitati
(
    nota     float       null,
    CNP_student   varchar(13) null,
    id_activitate int         null,
    constraint note_activitati_activitati_profesori_id_activitate_fk
        foreign key (id_activitate) references activitati_profesori (id_activitate)
            on delete cascade,
    constraint note_activitati_detalii_studenti_CNP_fk
        foreign key (CNP_student) references detalii_studenti(CNP)
            on delete cascade

);


create table if not exists profesori_materii
(
    CNP_profesor varchar(13) null,
    id_materie   int         null,
    constraint profesori_materii_detalii_profesori_CNP_fk
        foreign key (CNP_profesor) references detalii_profesori (CNP)
            on delete cascade,
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

create definer = root@localhost trigger delete_user
    after delete
    on utilizatori
    for each row
BEGIN

    DELETE FROM studenti_grupuri_studenti WHERE CNP_student = OLD.CNP;

    DELETE FROM note_activitati WHERE CNP_student = OLD.CNP;

    DELETE FROM materii_studenti WHERE CNP_student = OLD.CNP;

    DELETE FROM activitati_studenti
    WHERE id_activitate IN (
        SELECT gs.id_activitate
        FROM grupuri_studenti gs
        JOIN studenti_grupuri_studenti sgs ON gs.id_grup = sgs.id_grup
        WHERE sgs.CNP_student = OLD.CNP
    );

    DELETE FROM detalii_studenti WHERE CNP = OLD.CNP;

    DELETE FROM activitati_profesori WHERE CNP_profesor = OLD.CNP;

    DELETE FROM profesori_materii WHERE CNP_profesor = OLD.CNP;

    DELETE FROM detalii_profesori WHERE CNP = OLD.CNP;

END;


ALTER TABLE detalii_studenti
DROP FOREIGN KEY detalii_studenti_utilizatori_CNP_fk;

ALTER TABLE detalii_studenti
ADD CONSTRAINT detalii_studenti_utilizatori_CNP_fk
    FOREIGN KEY (CNP)
    REFERENCES utilizatori(CNP)
    ON DELETE CASCADE;


ALTER TABLE studenti_grupuri_studenti
DROP FOREIGN KEY studenti_grupuri_studenti_detalii_studenti_CNP_fk;

ALTER TABLE studenti_grupuri_studenti
ADD CONSTRAINT studenti_grupuri_studenti_detalii_studenti_CNP_fk
    FOREIGN KEY (CNP_student)
    REFERENCES utilizatori(CNP)
    ON DELETE CASCADE;


alter table note_activitati
    drop foreign key note_activitati_detalii_studenti_CNP_fk;


ALTER TABLE note_activitati
ADD CONSTRAINT note_activitati_detalii_studenti_CNP_fk
    FOREIGN KEY (CNP_student)
    REFERENCES utilizatori(CNP)
    ON DELETE CASCADE;


ALTER TABLE detalii_profesori
DROP FOREIGN KEY detalii_profesori_utilizatori_CNP_fk;

ALTER TABLE detalii_profesori
ADD CONSTRAINT detalii_profesori_utilizatori_CNP_fk
    FOREIGN KEY (CNP)
    REFERENCES utilizatori(CNP)
    ON DELETE CASCADE;


ALTER TABLE activitati_profesori
DROP FOREIGN KEY activitati_profesori_detalii_profesori_CNP_fk;

ALTER TABLE activitati_profesori
ADD CONSTRAINT activitati_profesori_detalii_profesori_CNP_fk
    FOREIGN KEY (CNP_profesor)
    REFERENCES detalii_profesori(CNP)
    ON DELETE CASCADE;


ALTER TABLE profesori_materii
DROP FOREIGN KEY profesori_materii_detalii_profesori_CNP_fk;

ALTER TABLE profesori_materii
ADD CONSTRAINT profesori_materii_detalii_profesori_CNP_fk
    FOREIGN KEY (CNP_profesor)
    REFERENCES detalii_profesori(CNP)
    ON DELETE CASCADE;


ALTER TABLE note_activitati
DROP FOREIGN KEY note_activitati_activitati_profesori_id_activitate_fk;

ALTER TABLE note_activitati
ADD CONSTRAINT note_activitati_activitati_profesori_id_activitate_fk
    FOREIGN KEY (id_activitate)
    REFERENCES activitati_profesori(id_activitate)
    ON DELETE CASCADE;

ALTER TABLE materii_studenti
DROP FOREIGN KEY materii_studenti_detalii_studenti_CNP_fk;

ALTER TABLE materii_studenti
DROP FOREIGN KEY materii_studenti_profesor_id_fk;

ALTER TABLE materii_studenti
ADD CONSTRAINT materii_studenti_profesor_id_fk
    FOREIGN KEY (CNP_profesor)
    REFERENCES detalii_profesori(CNP)
    ON DELETE CASCADE;

DELIMITER $$

CREATE TRIGGER calculeaza_nota_finala
AFTER INSERT ON note_activitati
FOR EACH ROW
BEGIN
    DECLARE curs_pondere INT;
    DECLARE lab_pondere INT;
    DECLARE seminar_pondere INT;
    DECLARE nota_curs FLOAT;
    DECLARE nota_lab FLOAT;
    DECLARE nota_seminar FLOAT;
    DECLARE suma_pondere INT;
    DECLARE nota_finala INT;

    SELECT pondere_curs, pondere_lab, pondere_seminar
    INTO curs_pondere, lab_pondere, seminar_pondere
    FROM materii
    WHERE id = (SELECT id_materie FROM activitati_profesori WHERE id_activitate = NEW.id_activitate);

    SELECT AVG(nota)
    INTO nota_curs
    FROM note_activitati
    WHERE id_activitate IN (
        SELECT id_activitate
        FROM activitati_profesori
        WHERE id_materie = (SELECT id_materie FROM activitati_profesori WHERE id_activitate = NEW.id_activitate)
          AND tip_activitate = 'curs'
    ) AND CNP_student = NEW.CNP_student;

    SELECT AVG(nota)
    INTO nota_lab
    FROM note_activitati
    WHERE id_activitate IN (
        SELECT id_activitate
        FROM activitati_profesori
        WHERE id_materie = (SELECT id_materie FROM activitati_profesori WHERE id_activitate = NEW.id_activitate)
          AND tip_activitate = 'laborator'
    ) AND CNP_student = NEW.CNP_student;

    SELECT AVG(nota)
    INTO nota_seminar
    FROM note_activitati
    WHERE id_activitate IN (
        SELECT id_activitate
        FROM activitati_profesori
        WHERE id_materie = (SELECT id_materie FROM activitati_profesori WHERE id_activitate = NEW.id_activitate)
          AND tip_activitate = 'seminar'
    ) AND CNP_student = NEW.CNP_student;

    SET suma_pondere = IFNULL(curs_pondere, 0) + IFNULL(lab_pondere, 0) + IFNULL(seminar_pondere, 0);

    SET nota_finala = ROUND((
        IFNULL(nota_curs, 0) * IFNULL(curs_pondere, 0) +
        IFNULL(nota_lab, 0) * IFNULL(lab_pondere, 0) +
        IFNULL(nota_seminar, 0) * IFNULL(seminar_pondere, 0)
    ) / suma_pondere);

    UPDATE materii_studenti
    SET nota_finala = nota_finala
    WHERE CNP_student = NEW.CNP_student
      AND id_materie = (SELECT id_materie FROM activitati_profesori WHERE id_activitate = NEW.id_activitate);

END$$

DELIMITER ;


DELIMITER $$

CREATE TRIGGER update_nota_finala
AFTER UPDATE ON note_activitati
FOR EACH ROW
BEGIN
    DECLARE curs_pondere INT;
    DECLARE lab_pondere INT;
    DECLARE seminar_pondere INT;
    DECLARE nota_curs FLOAT;
    DECLARE nota_lab FLOAT;
    DECLARE nota_seminar FLOAT;
    DECLARE suma_pondere INT;
    DECLARE nota_finala INT;

    SELECT pondere_curs, pondere_lab, pondere_seminar
    INTO curs_pondere, lab_pondere, seminar_pondere
    FROM materii
    WHERE id = (SELECT id_materie FROM activitati_profesori WHERE id_activitate = NEW.id_activitate);

    SELECT AVG(nota)
    INTO nota_curs
    FROM note_activitati
    WHERE id_activitate IN (
        SELECT id_activitate
        FROM activitati_profesori
        WHERE id_materie = (SELECT id_materie FROM activitati_profesori WHERE id_activitate = NEW.id_activitate)
          AND tip_activitate = 'curs'
    ) AND CNP_student = NEW.CNP_student;

    SELECT AVG(nota)
    INTO nota_lab
    FROM note_activitati
    WHERE id_activitate IN (
        SELECT id_activitate
        FROM activitati_profesori
        WHERE id_materie = (SELECT id_materie FROM activitati_profesori WHERE id_activitate = NEW.id_activitate)
          AND tip_activitate = 'laborator'
    ) AND CNP_student = NEW.CNP_student;

    SELECT AVG(nota)
    INTO nota_seminar
    FROM note_activitati
    WHERE id_activitate IN (
        SELECT id_activitate
        FROM activitati_profesori
        WHERE id_materie = (SELECT id_materie FROM activitati_profesori WHERE id_activitate = NEW.id_activitate)
          AND tip_activitate = 'seminar'
    ) AND CNP_student = NEW.CNP_student;

    SET suma_pondere = IFNULL(curs_pondere, 0) + IFNULL(lab_pondere, 0) + IFNULL(seminar_pondere, 0);

    SET nota_finala = ROUND((
        IFNULL(nota_curs, 0) * IFNULL(curs_pondere, 0) +
        IFNULL(nota_lab, 0) * IFNULL(lab_pondere, 0) +
        IFNULL(nota_seminar, 0) * IFNULL(seminar_pondere, 0)
    ) / suma_pondere);

    UPDATE materii_studenti
    SET nota_finala = nota_finala
    WHERE CNP_student = NEW.CNP_student
      AND id_materie = (SELECT id_materie FROM activitati_profesori WHERE id_activitate = NEW.id_activitate);

END$$

DELIMITER ;

INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('4095341157595', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 54, Oradea', '0764601491', 'user90850@example.com', 'ZFsxzapxkL', 'RO47BANK8776929935', 1841, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('6104469521664', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 57, Oradea', '0700410904', 'user12013@example.com', '04nKZ5diMd', 'RO17BANK9695545724', 5378, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('0684292625689', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 56, Timișoara', '0795832878', 'user85812@example.com', 'jM0r3NnSex', 'RO72BANK5693046182', 1545, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('8468597808819', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 57, Timișoara', '0747012291', 'user71781@example.com', 'bW3E1cSuHX', 'RO15BANK1532573917', 2018, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('4224011469869', 'Ionescu', 'Maria', 'Strada Lalelelor, Nr. 65, Cluj-Napoca', '0708715470', 'user10679@example.com', 'gw4HNdaIQl', 'RO69BANK8832630828', 7020, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('8417432772807', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 79, Timișoara', '0765517603', 'user6019@example.com', 'WcGBGioPlU', 'RO35BANK3594250815', 1366, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('7685738248381', 'Ionescu', 'Maria', 'Strada Lalelelor, Nr. 66, Cluj-Napoca', '0727933955', 'user60436@example.com', 'SkXqDt58wG', 'RO46BANK4329503171', 8134, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('8829126950914', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 54, Oradea', '0764601491', 'user27095@example.com', 'rbr6VIb6DI', 'RO47BANK8776929935', 1841, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('7473326800017', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 57, Oradea', '0700410904', 'user96946@example.com', 'KBDouyHOkU', 'RO17BANK9695545724', 5378, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('8995884772089', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 56, Timișoara', '0795832878', 'user38932@example.com', 'FTNYIObJUv', 'RO72BANK5693046182', 1545, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('6642974951139', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 57, Timișoara', '0747012291', 'user70101@example.com', 'IqsR68EuqT', 'RO15BANK1532573917', 2018, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('0902773084593', 'Ionescu', 'Maria', 'Strada Lalelelor, Nr. 65, Cluj-Napoca', '0708715470', 'user59858@example.com', 'qSZ1KLUhZp', 'RO69BANK8832630828', 7020, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('9480683926228', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 79, Timișoara', '0765517603', 'user90843@example.com', 'nm0PRDp0Vp', 'RO35BANK3594250815', 1366, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('9623779137893', 'Ionescu', 'Maria', 'Strada Lalelelor, Nr. 66, Cluj-Napoca', '0727933955', 'user90106@example.com', 'P8YImSLIgI', 'RO46BANK4329503171', 8134, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('0276964342825', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 54, Oradea', '0764601491', 'user27460@example.com', 'rBAkrNX8Fg', 'RO47BANK8776929935', 1841, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('5143749520421', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 57, Oradea', '0700410904', 'user63013@example.com', 'mVmJKe7SH9', 'RO17BANK9695545724', 5378, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('5040578352938', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 56, Timișoara', '0795832878', 'user39411@example.com', 'kwYHnQcgc1', 'RO72BANK5693046182', 1545, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('5999333818255', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 57, Timișoara', '0747012291', 'user18251@example.com', 'vk0TXUhRvh', 'RO15BANK1532573917', 2018, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('3646944547942', 'Ionescu', 'Maria', 'Strada Lalelelor, Nr. 65, Cluj-Napoca', '0708715470', 'user41365@example.com', 'RyzhbbopPz', 'RO69BANK8832630828', 7020, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('3831706449480', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 79, Timișoara', '0765517603', 'user65100@example.com', 'mX2RsPA9VA', 'RO35BANK3594250815', 1366, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('1099400049672', 'Ionescu', 'Maria', 'Strada Lalelelor, Nr. 66, Cluj-Napoca', '0727933955', 'user7133@example.com', 'hz6cZd2Aql', 'RO46BANK4329503171', 8134, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('0561588733883', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 54, Oradea', '0764601491', 'user99745@example.com', 'WQEeRSkoGM', 'RO47BANK8776929935', 1841, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('2404023608380', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 57, Oradea', '0700410904', 'user8926@example.com', 'IfVh3yRlXc', 'RO17BANK9695545724', 5378, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('5109973862646', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 56, Timișoara', '0795832878', 'user15214@example.com', 'jYYjhhgswy', 'RO72BANK5693046182', 1545, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('1728127878140', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 57, Timișoara', '0747012291', 'user7838@example.com', 'zpWk9zurDM', 'RO15BANK1532573917', 2018, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('1928611476860', 'Ionescu', 'Maria', 'Strada Lalelelor, Nr. 65, Cluj-Napoca', '0708715470', 'user91419@example.com', 'TL37EVB4Di', 'RO69BANK8832630828', 7020, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('0386592596164', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 79, Timișoara', '0765517603', 'user12418@example.com', 'nHXFoeuGF0', 'RO35BANK3594250815', 1366, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('9484723178453', 'Ionescu', 'Maria', 'Strada Lalelelor, Nr. 66, Cluj-Napoca', '0727933955', 'user70291@example.com', 'z0FT3PWzrM', 'RO46BANK4329503171', 8134, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('4213051539788', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 54, Oradea', '0764601491', 'user16259@example.com', 'EvZ5ynvQta', 'RO47BANK8776929935', 1841, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('9794389736547', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 57, Oradea', '0700410904', 'user38301@example.com', 'ldX52zFddK', 'RO17BANK9695545724', 5378, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('6088122767983', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 56, Timișoara', '0795832878', 'user47869@example.com', 'uBhgYvOi0H', 'RO72BANK5693046182', 1545, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('5079758056055', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 57, Timișoara', '0747012291', 'user72930@example.com', 'l2rmrEdnLx', 'RO15BANK1532573917', 2018, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('7091952483698', 'Ionescu', 'Maria', 'Strada Lalelelor, Nr. 65, Cluj-Napoca', '0708715470', 'user76657@example.com', 'CEqCaFETQ1', 'RO69BANK8832630828', 7020, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('6359711026080', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 79, Timișoara', '0765517603', 'user73182@example.com', 'ag6HWoNL4m', 'RO35BANK3594250815', 1366, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('6021110165120', 'Ionescu', 'Maria', 'Strada Lalelelor, Nr. 66, Cluj-Napoca', '0727933955', 'user60963@example.com', 'Pzwa74aos0', 'RO46BANK4329503171', 8134, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('6583105434210', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 54, Oradea', '0764601491', 'user45994@example.com', 'rMxRNzFIzf', 'RO47BANK8776929935', 1841, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('9673209540461', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 57, Oradea', '0700410904', 'user38673@example.com', 'foa2koTW24', 'RO17BANK9695545724', 5378, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('9703978868542', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 56, Timișoara', '0795832878', 'user37638@example.com', 'tmCXJGjPNG', 'RO72BANK5693046182', 1545, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('5372828678163', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 57, Timișoara', '0747012291', 'user9317@example.com', 'KmT3PmUZoj', 'RO15BANK1532573917', 2018, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('1948434079530', 'Ionescu', 'Maria', 'Strada Lalelelor, Nr. 65, Cluj-Napoca', '0708715470', 'user38296@example.com', 'MGtw2FjuDE', 'RO69BANK8832630828', 7020, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('4615961056796', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 79, Timișoara', '0765517603', 'user72363@example.com', 'sEC80gYm5j', 'RO35BANK3594250815', 1366, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('9805837237664', 'Ionescu', 'Maria', 'Strada Lalelelor, Nr. 66, Cluj-Napoca', '0727933955', 'user88153@example.com', 'BagCVkQZEH', 'RO46BANK4329503171', 8134, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('1756162041010', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 54, Oradea', '0764601491', 'user68479@example.com', 'EZo16p4jfa', 'RO47BANK8776929935', 1841, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('7436910111097', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 57, Oradea', '0700410904', 'user23670@example.com', '2XwHZhYg5B', 'RO17BANK9695545724', 5378, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('5917953849696', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 56, Timișoara', '0795832878', 'user36307@example.com', 'TMeJBZwHe3', 'RO72BANK5693046182', 1545, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('4752196142176', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 57, Timișoara', '0747012291', 'user38896@example.com', 'BNHa1lYVla', 'RO15BANK1532573917', 2018, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('7452736860806', 'Ionescu', 'Maria', 'Strada Lalelelor, Nr. 65, Cluj-Napoca', '0708715470', 'user78371@example.com', 'Oz9ZYCmMJJ', 'RO69BANK8832630828', 7020, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('0346300920140', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 79, Timișoara', '0765517603', 'user93245@example.com', 'H9njYcf2bh', 'RO35BANK3594250815', 1366, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('9847606272399', 'Ionescu', 'Maria', 'Strada Lalelelor, Nr. 66, Cluj-Napoca', '0727933955', 'user2642@example.com', 'KSzChddlLn', 'RO46BANK4329503171', 8134, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('2944677451359', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 54, Oradea', '0764601491', 'user73980@example.com', 'gbOYXLrbkY', 'RO47BANK8776929935', 1841, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('7463636496480', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 57, Oradea', '0700410904', 'user60171@example.com', 'Sv8k9vHV3E', 'RO17BANK9695545724', 5378, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('3392736662265', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 56, Timișoara', '0795832878', 'user70616@example.com', 'XiVSCDoNwg', 'RO72BANK5693046182', 1545, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('7021070909985', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 57, Timișoara', '0747012291', 'user57584@example.com', 'LBT6TFyNzo', 'RO15BANK1532573917', 2018, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('6283550823976', 'Ionescu', 'Maria', 'Strada Lalelelor, Nr. 65, Cluj-Napoca', '0708715470', 'user55257@example.com', '5dmRe2BSEa', 'RO69BANK8832630828', 7020, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('4395753560715', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 79, Timișoara', '0765517603', 'user31336@example.com', 'cJvPDLPipg', 'RO35BANK3594250815', 1366, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('5851761964552', 'Ionescu', 'Maria', 'Strada Lalelelor, Nr. 66, Cluj-Napoca', '0727933955', 'user74403@example.com', 'nHUDa5sm8x', 'RO46BANK4329503171', 8134, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('4911360347795', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 54, Oradea', '0764601491', 'user63636@example.com', 'mQqktlcpEJ', 'RO47BANK8776929935', 1841, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('7052723376911', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 57, Oradea', '0700410904', 'user67636@example.com', '9ITFQeIaRR', 'RO17BANK9695545724', 5378, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('1399976944787', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 56, Timișoara', '0795832878', 'user36981@example.com', 'jwTbaXRb6I', 'RO72BANK5693046182', 1545, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('8088635380514', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 57, Timișoara', '0747012291', 'user83389@example.com', '7lX6Hr2Epi', 'RO15BANK1532573917', 2018, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('5781429640621', 'Ionescu', 'Maria', 'Strada Lalelelor, Nr. 65, Cluj-Napoca', '0708715470', 'user23295@example.com', 'IAshhJa34a', 'RO69BANK8832630828', 7020, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('0042217620436', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 79, Timișoara', '0765517603', 'user98653@example.com', 'sig7YmJWof', 'RO35BANK3594250815', 1366, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('7808352501289', 'Ionescu', 'Maria', 'Strada Lalelelor, Nr. 66, Cluj-Napoca', '0727933955', 'user58236@example.com', 'rFhUBuJNaH', 'RO46BANK4329503171', 8134, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('0665908090508', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 54, Oradea', '0764601491', 'user85200@example.com', '40vGNQXpnl', 'RO47BANK8776929935', 1841, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('4749549800221', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 57, Oradea', '0700410904', 'user23886@example.com', '7r8pw2NMhp', 'RO17BANK9695545724', 5378, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('8816367931388', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 56, Timișoara', '0795832878', 'user78907@example.com', 'ZB5qnGExh4', 'RO72BANK5693046182', 1545, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('4479542646576', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 57, Timișoara', '0747012291', 'user80131@example.com', 'Ees0dgYvrn', 'RO15BANK1532573917', 2018, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('2264098827430', 'Ionescu', 'Maria', 'Strada Lalelelor, Nr. 65, Cluj-Napoca', '0708715470', 'user57799@example.com', 'X6VMCVsJXZ', 'RO69BANK8832630828', 7020, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('6105126772686', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 79, Timișoara', '0765517603', 'user85399@example.com', 'Tt2oPtyobh', 'RO35BANK3594250815', 1366, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('6026054554920', 'Ionescu', 'Maria', 'Strada Lalelelor, Nr. 66, Cluj-Napoca', '0727933955', 'user64687@example.com', 'PVhUioC58j', 'RO46BANK4329503171', 8134, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('1281622380138', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 54, Oradea', '0764601491', 'user88024@example.com', '6ZmMGzH1lQ', 'RO47BANK8776929935', 1841, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('8981395793520', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 57, Oradea', '0700410904', 'user85456@example.com', '6SEzHqwGGN', 'RO17BANK9695545724', 5378, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('5755486704549', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 56, Timișoara', '0795832878', 'user23227@example.com', 'ebCfcribkB', 'RO72BANK5693046182', 1545, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('8105937318451', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 57, Timișoara', '0747012291', 'user67676@example.com', 'RZ5kUMbXmW', 'RO15BANK1532573917', 2018, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('6478156243317', 'Ionescu', 'Maria', 'Strada Lalelelor, Nr. 65, Cluj-Napoca', '0708715470', 'user1140@example.com', 'oyW6PEpzzB', 'RO69BANK8832630828', 7020, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('7520316043481', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 79, Timișoara', '0765517603', 'user92051@example.com', 'GhYRNM94bN', 'RO35BANK3594250815', 1366, 'student');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('8646041072180', 'Ionescu', 'Maria', 'Strada Lalelelor, Nr. 66, Cluj-Napoca', '0727933955', 'user85201@example.com', 'HKzkBxulUA', 'RO46BANK4329503171', 8134, 'student');


INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('9636398095255', 'Petrescu', 'Mihai', 'Strada Mihai Viteazul, Nr. 92, Ploiești', '0787778445', 'prof78286@example.com', 'W9GAUA1o9d', 'RO48BANK7479646067', 1720, 'profesor');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('9924047006153', 'Petrescu', 'Mihai', 'Strada Mihai Viteazul, Nr. 98, Ploiești', '0749853256', 'prof4173@example.com', 'FSJtodB7l8', 'RO86BANK1169437970', 3725, 'profesor');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('8400244911342', 'Avram', 'George', 'Strada Universității, Nr. 66, București', '0770626888', 'prof26934@example.com', 'Aci6MrX8LW', 'RO74BANK3828424558', 6299, 'profesor');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('5969849093080', 'Ene', 'Florin', 'Strada Tineretului, Nr. 17, Iași', '0780649984', 'prof25383@example.com', 'JrZFZXkx4s', 'RO50BANK1001754293', 2318, 'profesor');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('3252625163261', 'Mocanu', 'Victor', 'Strada Carpaților, Nr. 88, Brașov', '0769401261', 'prof28099@example.com', 'ImKjrFWjVe', 'RO16BANK1874647544', 2847, 'profesor');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('9442879290479', 'Neagu', 'Adrian', 'Strada Cetății, Nr. 85, Sibiu', '0702954087', 'prof89595@example.com', 'w19P8HVV0C', 'RO86BANK9226036441', 8357, 'profesor');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('2174897302000', 'Tudor', 'Cristina', 'Strada Delfinului, Nr. 58, Constanța', '0734832348', 'prof66273@example.com', 'WZuxthqdob', 'RO12BANK4078076654', 5173, 'profesor');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('5415821131183', 'Tudor', 'Cristina', 'Strada Delfinului, Nr. 80, Constanța', '0770256996', 'prof27581@example.com', 'KCeej2kcGy', 'RO61BANK8639958003', 7245, 'profesor');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('8425289091609', 'Rădulescu', 'Alina', 'Strada Eroilor, Nr. 27, Galați', '0741727635', 'prof2243@example.com', 'RU1zqpf1Ml', 'RO45BANK2929534367', 5191, 'profesor');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('0127150807876', 'Mocanu', 'Victor', 'Strada Carpaților, Nr. 31, Brașov', '0718495753', 'prof41527@example.com', '2sduf84rlo', 'RO95BANK5916955156', 4854, 'profesor');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('8286157437721', 'Rădulescu', 'Alina', 'Strada Eroilor, Nr. 63, Galați', '0761481612', 'prof70642@example.com', '35n31sciFk', 'RO51BANK6880883270', 8959, 'profesor');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('9741169600624', 'Petrescu', 'Mihai', 'Strada Mihai Viteazul, Nr. 90, Ploiești', '0755989451', 'prof79711@example.com', 'RyNpW7c0tr', 'RO12BANK7415086348', 9773, 'profesor');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('4104733170501', 'Avram', 'George', 'Strada Universității, Nr. 57, București', '0707936010', 'prof59842@example.com', '2pQJWWpo13', 'RO24BANK8265783898', 4884, 'profesor');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('9403029209133', 'Ene', 'Florin', 'Strada Tineretului, Nr. 92, Iași', '0793084096', 'prof94623@example.com', 'NeMP90QpBe', 'RO11BANK6119982525', 9276, 'profesor');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('9242738541942', 'Rădulescu', 'Alina', 'Strada Eroilor, Nr. 86, Galați', '0733001803', 'prof17245@example.com', 'BH1T0Du41r', 'RO33BANK1237027945', 1746, 'profesor');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('0407404275263', 'Sandu', 'Diana', 'Strada Independenței, Nr. 52, Oradea', '0719198844', 'prof93155@example.com', '2XTZCZneFe', 'RO72BANK7826386029', 6326, 'profesor');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('3951032160081', 'Ene', 'Florin', 'Strada Tineretului, Nr. 43, Iași', '0700391390', 'prof18162@example.com', 'EdHIlvuG8R', 'RO61BANK9912306002', 3432, 'profesor');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('5584397183497', 'Ciobanu', 'Raluca', 'Strada Memorandumului, Nr. 73, Cluj-Napoca', '0707892371', 'prof44473@example.com', 'hL3AcryS8l', 'RO48BANK3504147731', 7064, 'profesor');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('0611494230499', 'Mocanu', 'Victor', 'Strada Carpaților, Nr. 51, Brașov', '0730442247', 'prof18474@example.com', 'GgpOrnKTPw', 'RO28BANK7703750413', 8885, 'profesor');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('4490531059273', 'Ciobanu', 'Raluca', 'Strada Memorandumului, Nr. 79, Cluj-Napoca', '0798521168', 'prof83389@example.com', 'gsJSJktjOS', 'RO80BANK3357405494', 2837, 'profesor');


INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('2095611977880', 'Popa', 'Ion', 'Strada Guvernului, Nr. 27, București', '0745355345', 'admin12287@example.com', '4Vo7ITSEVa', 'RO13BANK7732762238', 8187, 'administrator');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('2486360523991', 'Stoica', 'Ana', 'Strada Primăverii, Nr. 22, Cluj-Napoca', '0715595556', 'admin54132@example.com', '0d1Bxr246X', 'RO85BANK1640941043', 5976, 'administrator');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('0408426060477', 'Dobre', 'Vasile', 'Strada Alunișului, Nr. 28, Iași', '0726792252', 'admin34491@example.com', 'LXjIdtrhlF', 'RO31BANK5389750788', 1931, 'administrator');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('7836685343262', 'Niculescu', 'Mirela', 'Strada Libertății, Nr. 22, Timișoara', '0727438484', 'admin23806@example.com', 'Y1TykNRxWJ', 'RO16BANK5310342670', 4875, 'administrator');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('2285221449049', 'Stan', 'Bogdan', 'Strada Brașovului, Nr. 48, Brașov', '0752991142', 'admin42319@example.com', 'B5qRaaUkso', 'RO69BANK8967388339', 6455, 'administrator');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('7854181596552', 'Chiriac', 'Alexandra', 'Strada Principală, Nr. 32, Constanța', '0732197612', 'superadmin34064@example.com', '1URaIfpST9', 'RO77BANK1837415033', 1453, 'super-administrator');
INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator) VALUES ('3637902399444', 'Vlad', 'Răzvan', 'Strada Cetății, Nr. 34, Sibiu', '0762444026', 'superadmin72319@example.com', 'tdaImbrdYE', 'RO17BANK3562609680', 1710, 'super-administrator');

SELECT * from utilizatori where tip_utilizator = 'student';

INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '0042217620436', 35);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (1, '0276964342825', 114);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (2, '0346300920140', 154);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (2, '0386592596164', 152);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '0561588733883', 71);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (1, '0665908090508', 157);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (3, '0684292625689', 127);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (2, '0902773084593', 69);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '1099400049672', 189);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '1281622380138', 198);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '1399976944787', 44);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '1728127878140', 196);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '1756162041010', 165);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (1, '1928611476860', 79);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (2, '1948434079530', 16);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (2, '2264098827430', 170);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '2404023608380', 144);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (3, '2944677451359', 37);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (2, '3392736662265', 131);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '3646944547942', 107);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '3831706449480', 64);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (2, '4095341157595', 133);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (2, '4213051539788', 123);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (3, '4224011469869', 41);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (2, '4395753560715', 131);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (3, '4479542646576', 126);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (1, '4615961056796', 39);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '4749549800221', 73);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (2, '4752196142176', 158);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (1, '4911360347795', 140);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (3, '5040578352938', 63);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (3, '5079758056055', 59);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '5109973862646', 30);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (2, '5143749520421', 140);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '5372828678163', 118);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '5755486704549', 163);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '5781429640621', 195);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (2, '5851761964552', 127);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '5917953849696', 144);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (1, '5999333818255', 64);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (1, '6021110165120', 165);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '6026054554920', 38);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '6088122767983', 153);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '6104469521664', 47);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '6105126772686', 148);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (3, '6283550823976', 30);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (2, '6359711026080', 157);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '6478156243317', 120);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (1, '6583105434210', 155);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (1, '6642974951139', 192);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (3, '7021070909985', 129);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '7052723376911', 141);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (1, '7091952483698', 137);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (3, '7436910111097', 39);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (2, '7452736860806', 179);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (3, '7463636496480', 52);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (3, '7473326800017', 191);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '7520316043481', 55);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (2, '7685738248381', 62);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (2, '7808352501289', 67);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (1, '8088635380514', 132);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '8105937318451', 27);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (2, '8417432772807', 54);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (1, '8468597808819', 57);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (2, '8646041072180', 177);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (1, '8816367931388', 197);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '8829126950914', 15);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (2, '8981395793520', 197);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (1, '8995884772089', 145);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '9480683926228', 161);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (1, '9484723178453', 176);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '9623779137893', 101);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (3, '9673209540461', 81);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '9703978868542', 73);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (3, '9794389736547', 155);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '9805837237664', 167);
INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES (4, '9847606272399', 87);

select * from utilizatori where tip_utilizator = 'profesor';

INSERT INTO detalii_profesori (numar_maxim_ore_predate, numar_minim_ore_predate, departament, CNP) VALUES (187, 98, 'Artificial Intelligence', '0127150807876');
INSERT INTO detalii_profesori (numar_maxim_ore_predate, numar_minim_ore_predate, departament, CNP) VALUES (223, 144, 'Data Science', '0407404275263');
INSERT INTO detalii_profesori (numar_maxim_ore_predate, numar_minim_ore_predate, departament, CNP) VALUES (184, 129, 'Operating Systems', '0611494230499');
INSERT INTO detalii_profesori (numar_maxim_ore_predate, numar_minim_ore_predate, departament, CNP) VALUES (234, 100, 'Databases', '2174897302000');
INSERT INTO detalii_profesori (numar_maxim_ore_predate, numar_minim_ore_predate, departament, CNP) VALUES (229, 212, 'Operating Systems', '3252625163261');
INSERT INTO detalii_profesori (numar_maxim_ore_predate, numar_minim_ore_predate, departament, CNP) VALUES (193, 103, 'Artificial Intelligence', '3951032160081');
INSERT INTO detalii_profesori (numar_maxim_ore_predate, numar_minim_ore_predate, departament, CNP) VALUES (194, 143, 'Data Science', '4104733170501');
INSERT INTO detalii_profesori (numar_maxim_ore_predate, numar_minim_ore_predate, departament, CNP) VALUES (278, 101, 'Algorithms and Complexity', '4490531059273');
INSERT INTO detalii_profesori (numar_maxim_ore_predate, numar_minim_ore_predate, departament, CNP) VALUES (170, 137, 'Data Science', '5415821131183');
INSERT INTO detalii_profesori (numar_maxim_ore_predate, numar_minim_ore_predate, departament, CNP) VALUES (280, 132, 'Artificial Intelligence', '5584397183497');
INSERT INTO detalii_profesori (numar_maxim_ore_predate, numar_minim_ore_predate, departament, CNP) VALUES (101, 82, 'Computer Graphics', '5969849093080');
INSERT INTO detalii_profesori (numar_maxim_ore_predate, numar_minim_ore_predate, departament, CNP) VALUES (279, 215, 'Computer Networks', '8286157437721');
INSERT INTO detalii_profesori (numar_maxim_ore_predate, numar_minim_ore_predate, departament, CNP) VALUES (290, 212, 'Databases', '8400244911342');
INSERT INTO detalii_profesori (numar_maxim_ore_predate, numar_minim_ore_predate, departament, CNP) VALUES (140, 89, 'Human-Computer Interaction', '8425289091609');
INSERT INTO detalii_profesori (numar_maxim_ore_predate, numar_minim_ore_predate, departament, CNP) VALUES (258, 204, 'Data Science', '9242738541942');
INSERT INTO detalii_profesori (numar_maxim_ore_predate, numar_minim_ore_predate, departament, CNP) VALUES (188, 58, 'Computer Graphics', '9403029209133');
INSERT INTO detalii_profesori (numar_maxim_ore_predate, numar_minim_ore_predate, departament, CNP) VALUES (166, 88, 'Data Science', '9442879290479');
INSERT INTO detalii_profesori (numar_maxim_ore_predate, numar_minim_ore_predate, departament, CNP) VALUES (260, 64, 'Artificial Intelligence', '9636398095255');
INSERT INTO detalii_profesori (numar_maxim_ore_predate, numar_minim_ore_predate, departament, CNP) VALUES (140, 89, 'Computer Graphics', '9741169600624');
INSERT INTO detalii_profesori (numar_maxim_ore_predate, numar_minim_ore_predate, departament, CNP) VALUES (119, 59, 'Cybersecurity', '9924047006153');


INSERT INTO materii (nume, id, pondere_curs, pondere_lab, pondere_seminar) VALUES
('Introduction to Programming', 1, 50, 30, 20),
('Data Structures', 2, 60, 40, 0),
('Operating Systems', 3, 40, 30, 30),
('Computer Networks', 4, 70, 0, 30),
('Database Systems', 5, 50, 20, 30),
('Artificial Intelligence', 6, 45, 35, 20),
('Machine Learning', 7, 60, 40, 0),
('Web Development', 8, 50, 30, 20),
('Cybersecurity', 9, 80, 0, 20),
('Mobile Application Development', 10, 65, 25, 10);



INSERT INTO profesori_materii (CNP_profesor, id_materie) VALUES ('0127150807876', 10);
INSERT INTO profesori_materii (CNP_profesor, id_materie) VALUES ('0407404275263', 6);
INSERT INTO profesori_materii (CNP_profesor, id_materie) VALUES ('0611494230499', 2);
INSERT INTO profesori_materii (CNP_profesor, id_materie) VALUES ('2174897302000', 1);
INSERT INTO profesori_materii (CNP_profesor, id_materie) VALUES ('3252625163261', 3);
INSERT INTO profesori_materii (CNP_profesor, id_materie) VALUES ('3951032160081', 4);
INSERT INTO profesori_materii (CNP_profesor, id_materie) VALUES ('4104733170501', 10);
INSERT INTO profesori_materii (CNP_profesor, id_materie) VALUES ('4490531059273', 5);
INSERT INTO profesori_materii (CNP_profesor, id_materie) VALUES ('5415821131183', 5);
INSERT INTO profesori_materii (CNP_profesor, id_materie) VALUES ('5584397183497', 7);
INSERT INTO profesori_materii (CNP_profesor, id_materie) VALUES ('5969849093080', 2);
INSERT INTO profesori_materii (CNP_profesor, id_materie) VALUES ('8286157437721', 5);
INSERT INTO profesori_materii (CNP_profesor, id_materie) VALUES ('8400244911342', 4);
INSERT INTO profesori_materii (CNP_profesor, id_materie) VALUES ('8425289091609', 3);
INSERT INTO profesori_materii (CNP_profesor, id_materie) VALUES ('9242738541942', 1);
INSERT INTO profesori_materii (CNP_profesor, id_materie) VALUES ('9403029209133', 8);
INSERT INTO profesori_materii (CNP_profesor, id_materie) VALUES ('9442879290479', 7);
INSERT INTO profesori_materii (CNP_profesor, id_materie) VALUES ('9636398095255', 6);
INSERT INTO profesori_materii (CNP_profesor, id_materie) VALUES ('9741169600624', 9);
INSERT INTO profesori_materii (CNP_profesor, id_materie) VALUES ('9924047006153', 2);


INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('0042217620436', '3951032160081', 10, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('0276964342825', '9741169600624', 9, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('0346300920140', '9636398095255', 7, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('0386592596164', '4490531059273', 5, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('0561588733883', '9636398095255', 7, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('0665908090508', '8400244911342', 4, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('0684292625689', '3951032160081', 10, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('0902773084593', '5584397183497', 7, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('1099400049672', '0127150807876', 10, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('1281622380138', '0407404275263', 6, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('1399976944787', '8425289091609', 3, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('1728127878140', '0127150807876', 10, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('1756162041010', '4490531059273', 5, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('1928611476860', '0407404275263', 6, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('1948434079530', '3951032160081', 10, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('2264098827430', '2174897302000', 2, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('2404023608380', '2174897302000', 1, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('2944677451359', '9636398095255', 7, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('3392736662265', '4490531059273', 5, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('3646944547942', '5969849093080', 1, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('3831706449480', '5969849093080', 2, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('4095341157595', '0127150807876', 10, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('4213051539788', '9403029209133', 8, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('4224011469869', '9242738541942', 3, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('4395753560715', '8400244911342', 4, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('4479542646576', '9924047006153', 2, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('4615961056796', '0407404275263', 6, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('4749549800221', '9442879290479', 7, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('4752196142176', '5415821131183', 5, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('4911360347795', '9442879290479', 7, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('5040578352938', '9403029209133', 8, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('5079758056055', '5969849093080', 2, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('5109973862646', '5415821131183', 5, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('5143749520421', '3252625163261', 1, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('5372828678163', '4490531059273', 5, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('5755486704549', '2174897302000', 2, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('5781429640621', '0611494230499', 2, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('5851761964552', '2174897302000', 1, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('5917953849696', '5584397183497', 7, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('5999333818255', '9924047006153', 2, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('6021110165120', '4104733170501', 10, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('6026054554920', '4490531059273', 5, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('6088122767983', '0127150807876', 10, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('6104469521664', '9442879290479', 7, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('6105126772686', '0127150807876', 10, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('6283550823976', '5415821131183', 5, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('6359711026080', '9636398095255', 7, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('6478156243317', '0407404275263', 6, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('6583105434210', '0407404275263', 6, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('6642974951139', '0127150807876', 10, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('7021070909985', '9442879290479', 7, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('7052723376911', '9924047006153', 2, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('7091952483698', '0611494230499', 2, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('7436910111097', '3951032160081', 10, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('7452736860806', '9442879290479', 7, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('7463636496480', '9242738541942', 3, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('7473326800017', '0611494230499', 2, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('7520316043481', '5969849093080', 1, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('7685738248381', '0127150807876', 10, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('7808352501289', '9242738541942', 3, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('8088635380514', '3951032160081', 10, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('8105937318451', '8425289091609', 3, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('8417432772807', '8400244911342', 4, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('8468597808819', '5969849093080', 2, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('8646041072180', '4490531059273', 5, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('8816367931388', '0127150807876', 10, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('8829126950914', '8286157437721', 5, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('8981395793520', '9403029209133', 8, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('8995884772089', '3252625163261', 2, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('9480683926228', '2174897302000', 2, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('9484723178453', '9636398095255', 7, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('9623779137893', '5969849093080', 2, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('9673209540461', '8425289091609', 3, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('9703978868542', '9442879290479', 7, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('9794389736547', '4104733170501', 10, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('9805837237664', '9741169600624', 9, 0);
INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala) VALUES ('9847606272399', '9636398095255', 7, 0);


INSERT INTO activitati_profesori (data_inceput, tip_activitate, data_final, nr_max_participanti, descriere, CNP_profesor, id_materie)
VALUES
('2024-01-10 09:00:00', 'seminar', '2024-01-10 12:00:00', 25, 'Seminar de informatica avansata', '2174897302000', 1),
('2024-01-10 09:00:00', 'curs', '2024-01-10 12:00:00', 100, 'Curs informatica avansata', '2174897302000', 1),
('2024-01-10 09:00:00', 'laborator', '2024-01-10 12:00:00', 25, 'Laborator de informatica avansata', '2174897302000', 1),
('2024-02-15 14:00:00', 'laborator', '2024-02-15 16:30:00', 20, 'Workshop despre metode moderne de predare', '0611494230499', 2),
('2024-03-05 10:00:00', 'curs', '2024-03-05 11:30:00', 30, 'Introducere in matematica discreta', '3252625163261', 3),
('2024-03-10 08:30:00', 'laborator', '2024-03-10 11:00:00', 15, 'Laborator de programare in Python', '3951032160081', 4),
('2024-04-01 13:00:00', 'curs', '2024-04-01 17:00:00', 50, 'Conferinta despre inteligenta artificiala', '4490531059273', 5),
('2024-04-20 09:00:00', 'curs', '2024-04-20 11:00:00', 40, 'Analiza matematica - Limite si continuitati', '0407404275263', 6),
('2024-05-10 15:00:00', 'seminar', '2024-05-10 17:00:00', 20, 'Seminar despre metode numerice', '5584397183497', 7),
('2024-06-05 10:30:00', 'curs', '2024-06-05 13:00:00', 25, 'Workshop: Algoritmi de sortare eficienti', '9403029209133', 8),
('2024-07-01 11:00:00', 'curs', '2024-07-01 13:00:00', 10, 'Laborator de fizica - Optica', '9741169600624', 9),
('2024-08-15 09:00:00', 'laborator', '2024-08-15 13:00:00', 60, 'Conferinta: Evolutia tehnologiei', '0127150807876', 10),
('2024-09-10 08:00:00', 'seminar', '2024-09-10 10:00:00', 35, 'Bazele statisticii', '9242738541942', 3),
('2024-10-05 10:00:00', 'seminar', '2024-10-05 12:00:00', 30, 'Seminar de chimie organica', '5969849093080', 2),
('2024-11-20 14:00:00', 'laborator', '2024-11-20 16:00:00', 12, 'Laborator de biologie - Genetica', '8425289091609', 3),
('2024-12-01 15:30:00', 'curs', '2024-12-01 18:00:00', 20, 'Workshop: Dezvoltarea aplicatiilor web', '8400244911342', 4),
('2024-12-20 09:30:00', 'seminar', '2024-12-20 12:30:00', 70, 'Conferinta anuala de stiinte exacte', '4104733170501', 10);

INSERT INTO activitati_studenti (data, numar_ore, numar_minim_participanti, timp_expirare, id_activitate, nume) VALUES ('2024-01-28 23:37:40', 7, 44, '2024-02-05 23:37:40', 1, 'Cybersecurity Talk');
INSERT INTO activitati_studenti (data, numar_ore, numar_minim_participanti, timp_expirare, id_activitate, nume) VALUES ('2024-05-31 08:04:47', 5, 29, '2024-06-09 08:04:47', 2, 'Algorithms Bootcamp');
INSERT INTO activitati_studenti (data, numar_ore, numar_minim_participanti, timp_expirare, id_activitate, nume) VALUES ('2024-07-19 15:17:25', 6, 50, '2024-08-18 15:17:25', 3, 'Networking Basics');
INSERT INTO activitati_studenti (data, numar_ore, numar_minim_participanti, timp_expirare, id_activitate, nume) VALUES ('2024-10-01 13:44:10', 1, 44, '2024-10-07 13:44:10', 4, 'Coding Camp');
INSERT INTO activitati_studenti (data, numar_ore, numar_minim_participanti, timp_expirare, id_activitate, nume) VALUES ('2024-05-21 04:28:12', 1, 34, '2024-05-28 04:28:12', 5, 'Cloud Computing Seminar');
INSERT INTO activitati_studenti (data, numar_ore, numar_minim_participanti, timp_expirare, id_activitate, nume) VALUES ('2024-03-11 14:58:13', 7, 10, '2024-03-28 14:58:13', 6, 'Workshop AI');
INSERT INTO activitati_studenti (data, numar_ore, numar_minim_participanti, timp_expirare, id_activitate, nume) VALUES ('2024-09-12 02:29:08', 8, 9, '2024-09-28 02:29:08', 7, 'Cloud Computing Seminar');
INSERT INTO activitati_studenti (data, numar_ore, numar_minim_participanti, timp_expirare, id_activitate, nume) VALUES ('2024-12-21 19:08:27', 4, 28, '2025-01-20 19:08:27', 8, 'Workshop AI');
INSERT INTO activitati_studenti (data, numar_ore, numar_minim_participanti, timp_expirare, id_activitate, nume) VALUES ('2024-07-18 12:54:13', 7, 23, '2024-07-24 12:54:13', 9, 'Database Optimization');
INSERT INTO activitati_studenti (data, numar_ore, numar_minim_participanti, timp_expirare, id_activitate, nume) VALUES ('2024-11-10 13:49:59', 7, 32, '2024-11-30 13:49:59', 10, 'HCI Workshop');

INSERT INTO grupuri_studenti (id_materie, id_activitate, id_grup) VALUES (1, 4, 1);
INSERT INTO grupuri_studenti (id_materie, id_activitate, id_grup) VALUES (2, 1, 2);
INSERT INTO grupuri_studenti (id_materie, id_activitate, id_grup) VALUES (3, 8, 3);
INSERT INTO grupuri_studenti (id_materie, id_activitate, id_grup) VALUES (4, 9, 4);
INSERT INTO grupuri_studenti (id_materie, id_activitate, id_grup) VALUES (5, 6, 5);

INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('0042217620436', 3);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('0276964342825', 3);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('0346300920140', 2);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('0386592596164', 4);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('0561588733883', 5);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('0665908090508', 4);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('0684292625689', 5);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('0902773084593', 2);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('1099400049672', 5);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('1281622380138', 3);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('1399976944787', 3);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('1728127878140', 5);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('1756162041010', 5);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('1928611476860', 5);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('1948434079530', 1);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('2264098827430', 3);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('2404023608380', 1);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('2944677451359', 2);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('3392736662265', 4);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('3646944547942', 5);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('3831706449480', 3);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('4095341157595', 4);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('4213051539788', 1);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('4224011469869', 1);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('4395753560715', 1);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('4479542646576', 1);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('4615961056796', 5);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('4749549800221', 5);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('4752196142176', 4);
INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES ('4911360347795', 5);





INSERT INTO note_activitati (nota, CNP_student, id_activitate)VALUES
(5.0,  '2404023608380', 1),
(5.0,  '2404023608380', 2),
(5.0,  '2404023608380', 3),

(7.0,  '3646944547942', 1),
(8.0,  '3646944547942', 2),
(9.0,  '3646944547942', 3),


(3.0,  '5143749520421', 1),
(2.0,  '5143749520421', 2),
(1.0,  '5143749520421', 3);

SELECT nota
    FROM note_activitati where id_activitate = 1 and CNP_student = '2404023608380';

# UPDATE materii_studenti
#     SET nota_finala = ROUND(
#         (IFNULL(7, 0) * 30 / 100) +
#         (IFNULL(5, 0) * 20 / 100) +
#         (IFNULL(6, 0) * 50 / 100)
# )
#     WHERE CNP_student = 2404023608380 AND id_materie = 1;
#
# UPDATE note_activitati
# SET nota_curs = 9,
#     nota_seminar = 9,
#     nota_lab = 9
# WHERE CNP_student = '0127595982004' AND id_activitate = 1;
#

#
#     SELECT *
#     FROM activitati_profesori where id_materie = 1 and tip_activitate = 'curs';
#
#     SELECT *
#     FROM activitati_profesori where id_materie = 1 and tip_activitate = 'laborator';
#
#     SELECT *
#     FROM activitati_profesori where id_materie = 1 and tip_activitate = 'seminar';
#
#     SELECT detalii_studenti.CNP
#     FROM detalii_studenti where CNP = '2404023608380';
#
#     SELECT nota
#     FROM note_activitati where id_activitate = 1 and CNP_student = '2404023608380';


DELETE FROM utilizatori WHERE CNP='0042217620436';


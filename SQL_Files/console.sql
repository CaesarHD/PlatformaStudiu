CREATE DATABASE proiect;
use proiect;

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
    nume            varchar(30) not null,
    id              int         not null
        primary key,
    pondere_curs    int         null,
    pondere_lab     int         null,
    pondere_seminar int         null
);

create table if not exists utilizatori
(
    CNP            varchar(13)                                                          not null
        primary key,
    nume           varchar(30)                                                          not null,
    prenume        varchar(30)                                                          not null,
    adresa         varchar(100)                                                         not null,
    numar_telefon  varchar(10)                                                          not null,
    email          varchar(100)                                                         not null,
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
    CNP_profesor  varchar(13) null,
    id_materie   int         null,
    nota_lab     float       null,
    nota_curs    float       null,
    nota_seminar float       null,
    nota_finala  int         null,
    constraint materii_studenti_detalii_studenti_CNP_fk
        foreign key (CNP_student) references detalii_studenti (CNP),
    constraint materii_studenti_materii_id_fk
        foreign key (id_materie) references materii (id),
    constraint materii_studenti_profesor_id_fk
        foreign key (CNP_profesor) references detalii_profesori (CNP)
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






INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, IBAN, numar_contract, tip_utilizator) VALUES
('3510341893599', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 54, Oradea', '0764601491', 'mihai.stancu97@example.com', 'RO47BANK8776929935', 1841, 'student'),
('6731908237597', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 57, Oradea', '0700410904', 'mihai.stancu64@example.com', 'RO17BANK9695545724', 5378, 'student'),
('3876183919971', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 56, Timișoara', '0795832878', 'elena.marinescu61@example.com', 'RO72BANK5693046182', 1545, 'student'),
('6191179367727', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 57, Timișoara', '0747012291', 'elena.marinescu11@example.com', 'RO15BANK1532573917', 2018, 'student'),
('4486366311522', 'Ionescu', 'Maria', 'Strada Lalelelor, Nr. 65, Cluj-Napoca', '0708715470', 'maria.ionescu60@example.com', 'RO69BANK8832630828', 7020, 'student'),
('1911696832276', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 79, Timișoara', '0765517603', 'elena.marinescu78@example.com', 'RO35BANK3594250815', 1366, 'student'),
('1343204997734', 'Ionescu', 'Maria', 'Strada Lalelelor, Nr. 66, Cluj-Napoca', '0727933955', 'maria.ionescu91@example.com', 'RO46BANK4329503171', 8134, 'student'),
('8155970687577', 'Popescu', 'Andrei', 'Strada Mihai Eminescu, Nr. 83, București', '0708400792', 'andrei.popescu57@example.com', 'RO18BANK4947786456', 2425, 'student'),
('7484057531411', 'Vasilescu', 'Cristian', 'Strada Tomis, Nr. 44, Constanța', '0700283038', 'cristian.vasilescu21@example.com', 'RO55BANK1440528629', 7413, 'student'),
('6132673255690', 'Diaconescu', 'Anca', 'Strada Domnească, Nr. 75, Galați', '0798264612', 'anca.diaconescu32@example.com', 'RO41BANK9739404734', 9884, 'student'),
('8743101238916', 'Diaconescu', 'Anca', 'Strada Domnească, Nr. 9, Galați', '0757404831', 'anca.diaconescu2@example.com', 'RO36BANK9757703596', 4995, 'student'),
('2366972367613', 'Diaconescu', 'Anca', 'Strada Domnească, Nr. 40, Galați', '0788921093', 'anca.diaconescu99@example.com', 'RO23BANK4263852127', 1638, 'student'),
('1162768493764', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 65, Oradea', '0756520803', 'mihai.stancu61@example.com', 'RO68BANK5916799881', 5483, 'student'),
('5527018096355', 'Dumitru', 'Alexandru', 'Strada Calea Victoriei, Nr. 63, Brașov', '0715344358', 'alexandru.dumitru36@example.com', 'RO34BANK3161526452', 3260, 'student'),
('4556743134702', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 41, Timișoara', '0738025982', 'elena.marinescu41@example.com', 'RO70BANK1463545020', 4535, 'student'),
('9665495902267', 'Popescu', 'Andrei', 'Strada Mihai Eminescu, Nr. 6, București', '0783787645', 'andrei.popescu65@example.com', 'RO42BANK2406117033', 7482, 'student'),
('1662473049500', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 7, Oradea', '0786420677', 'mihai.stancu37@example.com', 'RO14BANK8386522334', 8545, 'student'),
('6332051459740', 'Popescu', 'Andrei', 'Strada Mihai Eminescu, Nr. 45, București', '0794240572', 'andrei.popescu29@example.com', 'RO87BANK2115700912', 2933, 'student'),
('1461617928882', 'Popescu', 'Andrei', 'Strada Mihai Eminescu, Nr. 1, București', '0744872840', 'andrei.popescu80@example.com', 'RO86BANK9254843028', 9271, 'student'),
('4813880199903', 'Dumitru', 'Alexandru', 'Strada Calea Victoriei, Nr. 57, Brașov', '0791821721', 'alexandru.dumitru42@example.com', 'RO46BANK4865326437', 8075, 'student'),
('9925467175816', 'Georgescu', 'Ioana', 'Strada Ștefan cel Mare, Nr. 88, Iași', '0726770936', 'ioana.georgescu33@example.com', 'RO57BANK4438942815', 4417, 'student'),
('1279824269710', 'Popescu', 'Andrei', 'Strada Mihai Eminescu, Nr. 17, București', '0783790028', 'andrei.popescu62@example.com', 'RO62BANK4584791327', 1490, 'student'),
('0793853011462', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 41, Oradea', '0708264319', 'mihai.stancu9@example.com', 'RO23BANK6823007862', 8971, 'student'),
('6176838073293', 'Diaconescu', 'Anca', 'Strada Domnească, Nr. 55, Galați', '0723041561', 'anca.diaconescu69@example.com', 'RO28BANK7855469136', 4420, 'student'),
('6190900135484', 'Dumitru', 'Alexandru', 'Strada Calea Victoriei, Nr. 36, Brașov', '0717015050', 'alexandru.dumitru10@example.com', 'RO15BANK2385128483', 1132, 'student'),
('2701297587088', 'Iliescu', 'Roxana', 'Strada Gării, Nr. 77, Ploiești', '0712029083', 'roxana.iliescu87@example.com', 'RO47BANK4516665201', 1249, 'student'),
('7816527504923', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 13, Timișoara', '0788657046', 'elena.marinescu60@example.com', 'RO99BANK4729304561', 2336, 'student'),
('2053227381742', 'Radu', 'Bianca', 'Strada Bălcescu, Nr. 81, Sibiu', '0743376718', 'bianca.radu62@example.com', 'RO26BANK6910238483', 2692, 'student'),
('6405843320630', 'Radu', 'Bianca', 'Strada Bălcescu, Nr. 14, Sibiu', '0793924402', 'bianca.radu87@example.com', 'RO53BANK2366435553', 3738, 'student'),
('0828343984581', 'Diaconescu', 'Anca', 'Strada Domnească, Nr. 71, Galați', '0771126383', 'anca.diaconescu80@example.com', 'RO27BANK9237875783', 5602, 'student'),
('8522152453127', 'Radu', 'Bianca', 'Strada Bălcescu, Nr. 27, Sibiu', '0708966463', 'bianca.radu59@example.com', 'RO53BANK3143093104', 1626, 'student'),
('8405615202728', 'Diaconescu', 'Anca', 'Strada Domnească, Nr. 91, Galați', '0751571366', 'anca.diaconescu93@example.com', 'RO55BANK6894884555', 5395, 'student'),
('8828914120525', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 80, Oradea', '0790929379', 'mihai.stancu79@example.com', 'RO14BANK4836813605', 2926, 'student'),
('9663424954074', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 22, Oradea', '0724546304', 'mihai.stancu76@example.com', 'RO84BANK8581523103', 9974, 'student'),
('2109911232373', 'Georgescu', 'Ioana', 'Strada Ștefan cel Mare, Nr. 12, Iași', '0704564555', 'ioana.georgescu50@example.com', 'RO62BANK3104296455', 9383, 'student'),
('5708089482027', 'Diaconescu', 'Anca', 'Strada Domnească, Nr. 29, Galați', '0740110680', 'anca.diaconescu37@example.com', 'RO95BANK2975429033', 2899, 'student'),
('2812731589116', 'Radu', 'Bianca', 'Strada Bălcescu, Nr. 65, Sibiu', '0775684714', 'bianca.radu40@example.com', 'RO76BANK3193939939', 3794, 'student'),
('8569009435856', 'Georgescu', 'Ioana', 'Strada Ștefan cel Mare, Nr. 45, Iași', '0780161283', 'ioana.georgescu58@example.com', 'RO91BANK6036620130', 1641, 'student'),
('0790918702060', 'Iliescu', 'Roxana', 'Strada Gării, Nr. 60, Ploiești', '0794111123', 'roxana.iliescu9@example.com', 'RO46BANK4912838343', 5270, 'student'),
('5657732522623', 'Vasilescu', 'Cristian', 'Strada Tomis, Nr. 94, Constanța', '0795561579', 'cristian.vasilescu96@example.com', 'RO73BANK6078409948', 4468, 'student'),
('1754010381404', 'Diaconescu', 'Anca', 'Strada Domnească, Nr. 33, Galați', '0792173137', 'anca.diaconescu8@example.com', 'RO62BANK5161435204', 9339, 'student'),
('8015904029158', 'Ionescu', 'Maria', 'Strada Lalelelor, Nr. 54, Cluj-Napoca', '0772950438', 'maria.ionescu53@example.com', 'RO13BANK8799320633', 4228, 'student'),
('2145576195996', 'Vasilescu', 'Cristian', 'Strada Tomis, Nr. 42, Constanța', '0730186632', 'cristian.vasilescu20@example.com', 'RO55BANK9498457166', 4463, 'student'),
('8126932283860', 'Popescu', 'Andrei', 'Strada Mihai Eminescu, Nr. 96, București', '0714682289', 'andrei.popescu49@example.com', 'RO13BANK2442658188', 7010, 'student'),
('8130666442770', 'Radu', 'Bianca', 'Strada Bălcescu, Nr. 34, Sibiu', '0700969047', 'bianca.radu76@example.com', 'RO77BANK4894865004', 8098, 'student'),
('5227942018199', 'Iliescu', 'Roxana', 'Strada Gării, Nr. 74, Ploiești', '0785144101', 'roxana.iliescu85@example.com', 'RO30BANK5542482575', 8671, 'student'),
('4312968766160', 'Stancu', 'Mihai', 'Strada Republicii, Nr. 33, Oradea', '0707464740', 'mihai.stancu68@example.com', 'RO56BANK8217168121', 9894, 'student'),
('7554095082134', 'Dumitru', 'Alexandru', 'Strada Calea Victoriei, Nr. 5, Brașov', '0701420539', 'alexandru.dumitru85@example.com', 'RO95BANK2277177818', 5682, 'student'),
('0060471045231', 'Ionescu', 'Maria', 'Strada Lalelelor, Nr. 10, Cluj-Napoca', '0770940696', 'maria.ionescu4@example.com', 'RO40BANK8295948243', 5159, 'student'),
('4146054842012', 'Dumitru', 'Alexandru', 'Strada Calea Victoriei, Nr. 15, Brașov', '0779308166', 'alexandru.dumitru43@example.com', 'RO63BANK7775835386', 3331, 'student'),
('1200952180977', 'Vasilescu', 'Cristian', 'Strada Tomis, Nr. 67, Constanța', '0753384546', 'cristian.vasilescu97@example.com', 'RO77BANK4972780366', 9525, 'student'),
('9287808121344', 'Iliescu', 'Roxana', 'Strada Gării, Nr. 33, Ploiești', '0710203691', 'roxana.iliescu89@example.com', 'RO64BANK7251716675', 9999, 'student'),
('4942350502318', 'Marinescu', 'Elena', 'Strada Libertății, Nr. 78, Timișoara', '0761083426', 'elena.marinescu85@example.com', 'RO51BANK7970343395', 4429, 'student'),
('0127595982004', 'Dumitru', 'Alexandru', 'Strada Calea Victoriei, Nr. 68, Brașov', '0737518346', 'alexandru.dumitru83@example.com', 'RO18BANK6078377534', 9710, 'student'),
('9865159642844', 'Iliescu', 'Roxana', 'Strada Gării, Nr. 94, Ploiești', '0789638775', 'roxana.iliescu81@example.com', 'RO83BANK6038355881', 8190, 'student'),
('6906355105611', 'Iliescu', 'Roxana', 'Strada Gării, Nr. 60, Ploiești', '0748208167', 'roxana.iliescu66@example.com', 'RO85BANK2997001132', 2501, 'student'),
('6603677581723', 'Diaconescu', 'Anca', 'Strada Domnească, Nr. 68, Galați', '0758755414', 'anca.diaconescu10@example.com', 'RO99BANK8434514141', 3578, 'student'),
('3875425121378', 'Popescu', 'Andrei', 'Strada Mihai Eminescu, Nr. 59, București', '0729238393', 'andrei.popescu21@example.com', 'RO50BANK1583670653', 7677, 'student'),
('9905992259372', 'Vasilescu', 'Cristian', 'Strada Tomis, Nr. 22, Constanța', '0738233694', 'cristian.vasilescu81@example.com', 'RO64BANK7727420207', 3844, 'student'),
('3991843822387', 'Vasilescu', 'Cristian', 'Strada Tomis, Nr. 51, Constanța', '0724702245', 'cristian.vasilescu65@example.com', 'RO61BANK7196167233', 3821, 'student');

INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, IBAN, numar_contract, tip_utilizator) VALUES
('5337074475276', 'Petrescu', 'Mihai', 'Strada Mihai Viteazul, Nr. 92, Ploiești', '0787778445', 'mihai.petrescu28@example.com', 'RO48BANK7479646067', 1720, 'profesor'),
('1451796626076', 'Petrescu', 'Mihai', 'Strada Mihai Viteazul, Nr. 98, Ploiești', '0749853256', 'mihai.petrescu18@example.com', 'RO86BANK1169437970', 3725, 'profesor'),
('5191960707832', 'Avram', 'George', 'Strada Universității, Nr. 66, București', '0770626888', 'george.avram16@example.com', 'RO74BANK3828424558', 6299, 'profesor'),
('2057338561397', 'Ene', 'Florin', 'Strada Tineretului, Nr. 17, Iași', '0780649984', 'florin.ene1@example.com', 'RO50BANK1001754293', 2318, 'profesor'),
('3426306266061', 'Mocanu', 'Victor', 'Strada Carpaților, Nr. 88, Brașov', '0769401261', 'victor.mocanu11@example.com', 'RO16BANK1874647544', 2847, 'profesor'),
('8816480108784', 'Neagu', 'Adrian', 'Strada Cetății, Nr. 85, Sibiu', '0702954087', 'adrian.neagu2@example.com', 'RO86BANK9226036441', 8357, 'profesor'),
('7604752748006', 'Tudor', 'Cristina', 'Strada Delfinului, Nr. 58, Constanța', '0734832348', 'cristina.tudor43@example.com', 'RO12BANK4078076654', 5173, 'profesor'),
('9697999556235', 'Tudor', 'Cristina', 'Strada Delfinului, Nr. 80, Constanța', '0770256996', 'cristina.tudor29@example.com', 'RO61BANK8639958003', 7245, 'profesor'),
('2830877367460', 'Rădulescu', 'Alina', 'Strada Eroilor, Nr. 27, Galați', '0741727635', 'alina.rădulescu5@example.com', 'RO45BANK2929534367', 5191, 'profesor'),
('8967756958643', 'Mocanu', 'Victor', 'Strada Carpaților, Nr. 31, Brașov', '0718495753', 'victor.mocanu5@example.com', 'RO95BANK5916955156', 4854, 'profesor'),
('4892390499206', 'Rădulescu', 'Alina', 'Strada Eroilor, Nr. 63, Galați', '0761481612', 'alina.rădulescu42@example.com', 'RO51BANK6880883270', 8959, 'profesor'),
('5152652753684', 'Petrescu', 'Mihai', 'Strada Mihai Viteazul, Nr. 90, Ploiești', '0755989451', 'mihai.petrescu21@example.com', 'RO12BANK7415086348', 9773, 'profesor'),
('6183769267218', 'Avram', 'George', 'Strada Universității, Nr. 57, București', '0707936010', 'george.avram17@example.com', 'RO24BANK8265783898', 4884, 'profesor'),
('2059840727617', 'Ene', 'Florin', 'Strada Tineretului, Nr. 92, Iași', '0793084096', 'florin.ene27@example.com', 'RO11BANK6119982525', 9276, 'profesor'),
('3366976737858', 'Rădulescu', 'Alina', 'Strada Eroilor, Nr. 86, Galați', '0733001803', 'alina.rădulescu33@example.com', 'RO33BANK1237027945', 1746, 'profesor'),
('2393493227855', 'Sandu', 'Diana', 'Strada Independenței, Nr. 52, Oradea', '0719198844', 'diana.sandu33@example.com', 'RO72BANK7826386029', 6326, 'profesor'),
('1539258833000', 'Ene', 'Florin', 'Strada Tineretului, Nr. 43, Iași', '0700391390', 'florin.ene8@example.com', 'RO61BANK9912306002', 3432, 'profesor'),
('8605851055721', 'Ciobanu', 'Raluca', 'Strada Memorandumului, Nr. 73, Cluj-Napoca', '0707892371', 'raluca.ciobanu20@example.com', 'RO48BANK3504147731', 7064, 'profesor'),
('2697958861215', 'Mocanu', 'Victor', 'Strada Carpaților, Nr. 51, Brașov', '0730442247', 'victor.mocanu38@example.com', 'RO28BANK7703750413', 8885, 'profesor'),
('1178031729824', 'Ciobanu', 'Raluca', 'Strada Memorandumului, Nr. 79, Cluj-Napoca', '0798521168', 'raluca.ciobanu50@example.com', 'RO80BANK3357405494', 2837, 'profesor');




INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, IBAN, numar_contract, tip_utilizator) VALUES
('6577956209703', 'Popa', 'Ion', 'Strada Guvernului, Nr. 27, București', '0745355345', 'ion.popa@example.com', 'RO13BANK7732762238', 8187, 'administrator'),
('4970040068574', 'Stoica', 'Ana', 'Strada Primăverii, Nr. 22, Cluj-Napoca', '0715595556', 'ana.stoica@example.com', 'RO85BANK1640941043', 5976, 'administrator'),
('2977127588414', 'Dobre', 'Vasile', 'Strada Alunișului, Nr. 28, Iași', '0726792252', 'vasile.dobre@example.com', 'RO31BANK5389750788', 1931, 'administrator'),
('9466534949356', 'Niculescu', 'Mirela', 'Strada Libertății, Nr. 22, Timișoara', '0727438484', 'mirela.niculescu@example.com', 'RO16BANK5310342670', 4875, 'administrator'),
('8052532073205', 'Stan', 'Bogdan', 'Strada Brașovului, Nr. 48, Brașov', '0752991142', 'bogdan.stan@example.com', 'RO69BANK8967388339', 6455, 'administrator'),
('1867362592391', 'Chiriac', 'Alexandra', 'Strada Principală, Nr. 32, Constanța', '0732197612', 'alexandra.chiriac@example.com', 'RO77BANK1837415033', 1453, 'super-administrator'),
('8497790498966', 'Vlad', 'Răzvan', 'Strada Cetății, Nr. 34, Sibiu', '0762444026', 'răzvan.vlad@example.com', 'RO17BANK3562609680', 1710, 'super-administrator');


INSERT INTO detalii_studenti (an_de_studiu, CNP, numar_ore_sustinute) VALUES
(3, '0060471045231', 177),
(1, '0127595982004', 3),
(3, '0790918702060', 51),
(2, '0793853011462', 136),
(3, '0828343984581', 238),
(4, '1162768493764', 14),
(3, '1200952180977', 235),
(1, '1279824269710', 41),
(1, '1343204997734', 30),
(4, '1461617928882', 300),
(3, '1662473049500', 35),
(2, '1754010381404', 100),
(1, '1911696832276', 198),
(3, '2053227381742', 223),
(2, '2109911232373', 69),
(3, '2145576195996', 230),
(3, '2366972367613', 44),
(2, '2701297587088', 297),
(1, '2812731589116', 70),
(1, '3510341893599', 103),
(3, '3875425121378', 204),
(4, '3876183919971', 36),
(3, '3991843822387', 123),
(2, '4146054842012', 209),
(3, '4312968766160', 229),
(4, '4486366311522', 20),
(4, '4556743134702', 100),
(3, '4813880199903', 11),
(2, '4942350502318', 43),
(1, '5227942018199', 119),
(2, '5527018096355', 69),
(1, '5657732522623', 271),
(2, '5708089482027', 62),
(1, '6132673255690', 0),
(3, '6176838073293', 216),
(4, '6190900135484', 248),
(2, '6191179367727', 219),
(2, '6332051459740', 82),
(4, '6405843320630', 219),
(4, '6603677581723', 180),
(3, '6731908237597', 199),
(3, '6906355105611', 194),
(2, '7484057531411', 168),
(2, '7554095082134', 300),
(2, '7816527504923', 192),
(1, '8015904029158', 2),
(3, '8126932283860', 263),
(1, '8130666442770', 249),
(3, '8155970687577', 8),
(2, '8405615202728', 111),
(4, '8522152453127', 35),
(4, '8569009435856', 274),
(1, '8743101238916', 60),
(4, '8828914120525', 60),
(1, '9287808121344', 209),
(3, '9663424954074', 30),
(2, '9665495902267', 71),
(2, '9865159642844', 136),
(4, '9905992259372', 105),
(1, '9925467175816', 125);


INSERT INTO detalii_profesori (numar_maxim_ore_predate, numar_minim_ore_predate, departament, CNP) VALUES
(122, 37, 'Networks', '1178031729824'),
(140, 36, 'AI and ML', '1451796626076'),
(93, 30, 'Computer Science', '1539258833000'),
(137, 32, 'Computer Science', '2057338561397'),
(140, 40, 'Cybersecurity', '2059840727617'),
(176, 47, 'Networks', '2393493227855'),
(167, 33, 'Computer Science', '2697958861215'),
(68, 18, 'AI and ML', '2830877367460'),
(150, 12, 'Cybersecurity', '3366976737858'),
(74, 18, 'Computer Science', '3426306266061'),
(129, 14, 'Computer Science', '4892390499206'),
(186, 12, 'Cybersecurity', '5152652753684'),
(129, 18, 'Cybersecurity', '5191960707832'),
(109, 29, 'AI and ML', '5337074475276'),
(127, 46, 'Computer Science', '6183769267218'),
(132, 17, 'Data Science', '7604752748006'),
(56, 46, 'Cybersecurity', '8605851055721'),
(134, 10, 'AI and ML', '8816480108784'),
(121, 32, 'Data Science', '8967756958643'),
(54, 33, 'Software Engineering', '9697999556235');



INSERT INTO materii (nume, id, pondere_curs, pondere_lab, pondere_seminar) VALUES
('Introduction to Programming', 1, 0, 0, 0),
('Data Structures', 2, 0, 0, 0),
('Operating Systems', 3, 0, 0, 0),
('Computer Networks', 4, 0, 0, 0),
('Database Systems', 5, 0, 0, 0),
('Artificial Intelligence', 6, 0, 0, 0),
('Machine Learning', 7, 0, 0, 0),
('Web Development', 8, 0, 0, 0),
('Cybersecurity', 9, 0, 0, 0),
('Mobile Application Development', 10, 0, 0, 0);


INSERT INTO profesori_materii (CNP_profesor, id_materie) VALUES
('1178031729824', 9),
('1451796626076', 8),
('1539258833000', 1),
('2057338561397', 7),
('2059840727617', 9),
('2393493227855', 8),
('2697958861215', 3),
('2830877367460', 1),
('3366976737858', 10),
('3426306266061', 2),
('4892390499206', 10),
('5152652753684', 5),
('5191960707832', 6),
('5337074475276', 6),
('6183769267218', 3),
('7604752748006', 7),
('8605851055721', 3),
('8816480108784', 8),
('8967756958643', 4),
('9697999556235', 8);

INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_lab, nota_curs, nota_seminar, nota_finala) VALUES
('0060471045231', '2830877367460', 1, 2.28, 7.62, 4.71, 4),
('0127595982004', '2830877367460', 1, 7.69, 9.05, 3.7, 6),
('0790918702060', '1539258833000', 1, 8.5, 2.21, 2.67, 4),
('0793853011462', '1539258833000', 1, 6.86, 5.9, 3.38, 5),
('0828343984581', '2830877367460', 1, 1.03, 3.12, 1.91, 2),
('1162768493764', '2830877367460', 1, 3.57, 5.55, 1.11, 3),
('1200952180977', '2830877367460', 1, 2.01, 3.63, 4.46, 3),
('1279824269710', '1539258833000', 1, 5.31, 3.5, 3.36, 4),
('1343204997734', '1539258833000', 1, 2.79, 8.16, 9.54, 6),
('1461617928882', '2830877367460', 1, 9.5, 4.48, 3.73, 5),
('1662473049500', '1539258833000', 1, 2.79, 2.1, 2.32, 2),
('1754010381404', '1539258833000', 1, 2.26, 7.92, 8.53, 6),
('1911696832276', '1539258833000', 1, 2.72, 6.17, 1.17, 3),
('2053227381742', '1539258833000', 1, 4.14, 3.42, 3.53, 3),
('2109911232373', '1539258833000', 1, 1.87, 1.13, 1.88, 1),
('2145576195996', '1539258833000', 1, 9.27, 3.85, 5.63, 6),
('2366972367613', '1539258833000', 1, 1.73, 8.26, 9.63, 6),
('2701297587088', '1539258833000', 1, 6.98, 9.19, 3.63, 6),
('2812731589116', '1539258833000', 1, 5.8, 7.52, 2.33, 5),
('3510341893599', '1539258833000', 1, 8.47, 2.4, 8.18, 6),
('3875425121378', '1539258833000', 1, 7.5, 6.99, 6.75, 7),
('3876183919971', '2830877367460', 1, 8.61, 6.43, 9.23, 8),
('3991843822387', '1539258833000', 1, 1.12, 3.85, 1.99, 2),
('4146054842012', '2830877367460', 1, 9.19, 3.84, 4.52, 5),
('4312968766160', '2830877367460', 1, 3.82, 3.94, 4.86, 4),
('4486366311522', '1539258833000', 1, 8.63, 8.31, 3.95, 6),
('4556743134702', '2830877367460', 1, 2.1, 8.25, 6.14, 5),
('4813880199903', '2830877367460', 1, 1.04, 1.43, 1.94, 1),
('4942350502318', '2830877367460', 1, 5.06, 7.82, 3.96, 5),
('5227942018199', '1539258833000', 1, 5.16, 2.51, 7.96, 5),
('5527018096355', '2830877367460', 1, 5.75, 3.7, 2.83, 4),
('5657732522623', '2830877367460', 1, 3.88, 3.39, 1.66, 2),
('5708089482027', '2830877367460', 1, 3.45, 7.28, 6.23, 5),
('6132673255690', '2830877367460', 1, 9.95, 1.98, 1.09, 4),
('6176838073293', '2830877367460', 1, 1.64, 4.07, 8.58, 4),
('6190900135484', '2830877367460', 1, 6.51, 2.08, 6.19, 4),
('6191179367727', '1539258833000', 1, 1.63, 8.13, 6.15, 5),
('6332051459740', '2830877367460', 1, 4.9, 3.18, 1.05, 3),
('6405843320630', '2830877367460', 1, 6.39, 2.5, 3.52, 4),
('6603677581723', '1539258833000', 1, 2.92, 1.2, 1.0, 1),
('6731908237597', '2830877367460', 1, 4.04, 5.72, 2.62, 4),
('6906355105611', '1539258833000', 1, 1.92, 8.26, 5.78, 5),
('7484057531411', '1539258833000', 1, 1.66, 8.63, 6.97, 5),
('7554095082134', '2830877367460', 1, 7.55, 8.36, 3.41, 6),
('7816527504923', '2830877367460', 1, 5.74, 6.95, 1.19, 4),
('8015904029158', '1539258833000', 1, 2.08, 4.02, 6.86, 4),
('8126932283860', '2830877367460', 1, 2.39, 5.22, 5.38, 4),
('8130666442770', '1539258833000', 1, 1.79, 9.21, 1.51, 4),
('8155970687577', '1539258833000', 1, 1.91, 5.25, 4.14, 3),
('8405615202728', '1539258833000', 1, 2.59, 8.08, 5.99, 5),
('8522152453127', '2830877367460', 1, 4.53, 8.74, 2.69, 5),
('8569009435856', '2830877367460', 1, 9.93, 1.51, 4.48, 5),
('8743101238916', '1539258833000', 1, 1.73, 4.13, 9.8, 5),
('8828914120525', '1539258833000', 1, 5.79, 2.59, 9.42, 5),
('9287808121344', '2830877367460', 1, 3.52, 4.07, 4.9, 4),
('9663424954074', '1539258833000', 1, 7.41, 1.06, 1.28, 3),
('9665495902267', '1539258833000', 1, 1.41, 2.09, 1.42, 1),
('9865159642844', '2830877367460', 1, 6.84, 9.9, 1.04, 5),
('9905992259372', '1539258833000', 1, 2.92, 2.84, 4.28, 3),
('9925467175816', '2830877367460', 1, 6.86, 2.73, 1.29, 3);

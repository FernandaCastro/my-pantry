CREATE TABLE IF NOT EXISTS purchase.PRODUCT(
    ID BIGSERIAL NOT NULL PRIMARY KEY,
    CODE VARCHAR(30) NOT NULL UNIQUE,
    DESCRIPTION VARCHAR(30),
    SIZE VARCHAR(10),
    CATEGORY VARCHAR(30)
);

DROP SEQUENCE IF EXISTS PRODUCT_ID_SEQ CASCADE;

INSERT INTO purchase.PRODUCT ("id","code","description","size") VALUES
(29,'ABSORVENTE','',''),
(30,'TABS LOU�AS','Rossmann',''),
(31,'SAB�O DE ROUPAS','Calgon',''),
(32,'TABS BANHEIRO','Wc tabs',''),
(33,'AMACIANTE DE ROUPAS','',''),
(34,'SABONETE CRIAN�A','B�bchen',''),
(35,'SABONETE ADULTO','',''),
(36,'SABONETE PARA M�OS','',''),
(37,'PASTA DE DENTE CRIAN�A','',''),
(38,'PASTA DE DENTE ADULTO','',''),
(39,'ESCOVA DE DENTE CRIAN�A','',''),
(40,'ESCOVA DE DENTE ADULTO','',''),
(41,'DIABO VERDE','',''),
(42,'LIMPADOR DE GORDURA','',''),
(43,'HIMMBEERE','',''),
(44,'BANANA','',''),
(45,'CEBOLA','',''),
(46,'ALHO','',''),
(47,'MA��','',''),
(48,'P�RA','',''),
(49,'BATATA','',''),
(50,'BETERRABA','',''),
(51,'BATATA PEQUENA','Assar no forno',''),
(52,'MORANGO','',''),
(53,'MIRTILO','',''),
(54,'SUPPEN GRUPPE','',''),
(55,'CUMINHO','',''),
(56,'COENTRO','',''),
(57,'CEBOLINHA','',''),
(58,'SALSINHA','',''),
(59,'PIPOCA SALGADA','',''),
(60,'BARRINHA CEREAL','',''),
(61,'MOUSSE','',''),
(62,'ACTIMEL','',''),
(63,'LASAGNA','',''),
(64,'CAPPELETTI','',''),
(65,'CABANOSSI','',''),
(66,'MANTEIGA','',''),
(67,'QUEIJO','',''),
(68,'GRANA PADANO','',''),
(69,'QUEIJO EM CUBOS','',''),
(70,'QUEIJO FETA','',''),
(71,'SALAMINHO','',''),
(72,'PEITO DE PERU','',''),
(73,'CARNE MO�DA','',''),
(74,'COXINHA DE FRANGO','',''),
(75,'PEITO DE FRANGO','',''),
(76,'HAMBURGUER','',''),
(77,'CARNE CHURRASCO','',''),
(78,'BOCKWURST','',''),
(79,'BRATWURST','',''),
(80,'OVO','',''),
(81,'PIMENTA DO REINO','Schwarze Pfeffer',''),
(82,'LEITE INTEGRAL','Vollmilch',''),
(83,'LEITE MATHEUS','',''),
(84,'LEITE LET�CIA','',''),
(85,'CALDO DE GALINHA','',''),
(86,'CALDO DE LEGUMES','',''),
(87,'CALDO DE CARNE','',''),
(88,'CREME DE LEITE','Sahne zum kochen',''),
(89,'FARINHA DE TRIGO','Mehl',''),
(90,'WAFFLE','',''),
(91,'BROWNIE','',''),
(92,'MINI MUFFIN','',''),
(93,'CAF�','',''),
(94,'MILCHBROTCHEN','',''),
(95,'P�O MATHEUS','Keiserbrot',''),
(96,'P�O FRANC�S PEQUENO','',''),
(97,'P�O CIABATTA','',''),
(98,'P�O FORMA INTEGRAL','',''),
(99,'P�O HAMBURGUER','',''),
(100,'P�O HOT DOG','',''),
(101,'DONUTS','',''),
(102,'FILTRO CAF�','',''),
(103,'KABA MORANGO','',''),
(104,'AZEITE','',''),
(105,'�LEO','',''),
(106,'VINAGRE','',''),
(107,'ERVILHA GESCH�LT','Erbsen gesch�lt',''),
(108,'A��CAR','',''),
(109,'SAL','',''),
(110,'SOJA SAUCE','',''),
(111,'MOLHO DE TOMATE','',''),
(112,'ARROZ','Basmati',''),
(113,'KETCHUP','',''),
(114,'MACARR�O','',''),
(115,'SACO DE LIXO','',''),
(116,'SACO LIXO PAPEL','',''),
(117,'TEMPERO SALM�O','Sahne Lachs',''),
(118,'OTHELO KEKS','',''),
(119,'OREO','',''),
(120,'BATATA PALHA','',''),
(121,'MINI BRETZEL','',''),
(122,'PIPOCA DOCE','',''),
(123,'BISCOITO DE ALHO REDONDO','Bake Rolls',''),
(124,'BISCOITO SALGADO REDONDO','',''),
(125,'BISCOITO TUC','',''),
(126,'VINHO','',''),
(127,'CERVEJA','',''),
(128,'ICE','',''),
(129,'BACKPAPIER FORNO','',''),
(130,'PAPEL HIGI�NICO','',''),
(131,'COCA','',''),
(132,'CAPRI-SUN','',''),
(133,'MARACUJA SAFT','',''),
(134,'APFEL SAFT','',''),
(135,'ACE SAFT','',''),
(136,'ICE TEA','',''),
(137,'WILD LACHS','',''),
(138,'PEIXE EMPANADO','',''),
(139,'SCHNITZEL','',''),
(140,'BATATA CONGELADA','',''),
(141,'SORVETE DE BAUNILHA','',''),
(142,'SORVETE CHOCOLATE','',''),
(143,'PIZZA','',''),
(144,'FARINHA MANDIOCA','','500g'),
(145,'POLVILHO','P�o de queijo','500g'),
(146,'FERMENTO','Backpulver','6 x 15g'),
(147,'LEITE CONDENSADO','Russo','304ml'),
(148,'MILHARINA','Polenta Maisgrie�','500g'),
(149,'COOKIE CHOCOLATE','','225g'),
(150,'BISCOITO AMANTEIGADO REDONDO','Butterspritzgeb�ck','400g'),
(151,'N�RNBERG ROSTBRATWURST','14 St�ck','300g'),
(152,'DESINFETANTE CH�O','Allweck & Boden Reiniger','1,5L'),
(153,'DESINFETANTE BANHEIRO','Sagrotan Badreiniger','750ml'),
(154,'DESINFETANTE MOFO','Sagrotan Schimmel Frei','750ml'),
(155,'DESINFETANTE KALK','Frosch Essig Reiniger','1L'),
(156,'SAL LOU�AS','Spezial-Salz Lava Lou�as','1,2Kg'),
(157,'SALSICHA HOT-DOG','7 St�ck','250g'),
(158,'DESINFETANTE COZINHA','Domol K�chen Hygiene Reiniger','750ml'),
(159,'DESENGORDURANTE COZINHA','Bref Power gegen Fett','750ml'),
(160,'BOLINHO DE ZITRONE LEL�','',''),
(161,'PAPEL TOALHA','Pacote com 4','4 un');

ALTER TABLE purchase.PURCHASE_ITEM DROP COLUMN IF EXISTS PRODUCT_CODE;
ALTER TABLE purchase.PURCHASE_ITEM DROP COLUMN IF EXISTS PRODUCT_DESCRIPTION;
ALTER TABLE purchase.PURCHASE_ITEM DROP COLUMN IF EXISTS PRODUCT_SIZE;

ALTER TABLE purchase.PURCHASE_ITEM DROP CONSTRAINT IF EXISTS PURCHASE_ITEM_PRODUCT_ID_FK;
ALTER TABLE purchase.PURCHASE_ITEM ADD CONSTRAINT PURCHASE_ITEM_PRODUCT_ID_FK FOREIGN KEY (PRODUCT_ID) REFERENCES purchase.PRODUCT (ID); --NOT VALID


CREATE TABLE IF NOT EXISTS purchase.PROPERTIES(
    PROPERTY_KEY VARCHAR(50)  PRIMARY KEY NOT NULL,
    PROPERTY_VALUE JSONB NOT NULL
);

INSERT INTO PROPERTIES VALUES
('product.categories', '{"categories": ["HORTIFRUTI", "CARNES", "FRIOS", "LATIC�NEO", "PADARIA", "LIMPEZA", "HIGIENE PESSOAL", "MERCEARIA", "BEBIDAS"]}');
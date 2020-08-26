CREATE TABLE book (
  id bigint NOT NULL IDENTITY,
  company varchar(255) not null,
  name varchar(255) not null,
  email varchar(255) DEFAULT NULL,
  phone varchar(255) DEFAULT NULL,
  shortphone varchar(255) DEFAULT NULL,
  mobilephone varchar(255) DEFAULT NULL,
  ip varchar(255) DEFAULT NULL,
  position varchar(255) DEFAULT NULL,
  city varchar(255) DEFAULT NULL,
  office varchar(255) DEFAULT NULL,
  cabinet varchar(255) DEFAULT NULL,
  department varchar(255) DEFAULT NULL,
  primary key (id));

INSERT INTO book (id,company,     name,   email,         phone,shortphone,mobilephone, ip, position, city,office,cabinet,department) VALUES
                 (10,'Компания 1','админ','test@test.ru','+54654654','461','+79098001122', '172.16.2.170','Ламер','Город','Офис1','257','IT'),
                 (11,'Компания 2','админ','admin@test.ru','+54654654','461','+79098001122','127.0.0.1','Администратор','Город','Офис2','774','АХО');
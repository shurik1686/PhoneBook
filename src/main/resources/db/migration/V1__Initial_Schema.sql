CREATE TABLE BOOK (
  id bigint auto_increment,
  company varchar(255) not null,
  name varchar(255) not null,
  email varchar(255) not null,
  phone varchar(255) not null,
  shortPhone varchar(255) not null,
  mobilePhone varchar(255) not null,
  primary key (id));

INSERT INTO BOOK (id,company,name,email,phone,shortPhone,mobilePhone) VALUES
(10,'Компания 1','админ','1@dvtg.ru','132465','1324','+54654654');
CREATE TABLE book (
  id bigint NOT NULL IDENTITY,
  company varchar(255) not null,
  name varchar(255) not null,
  email varchar(255) DEFAULT NULL,
  phone varchar(255) DEFAULT NULL,
  shortphone varchar(255) DEFAULT NULL,
  mobilephone varchar(255) DEFAULT NULL,
  ip varchar(255) DEFAULT NULL,
  primary key (id));

INSERT INTO book (id,company,name,email,phone,shortphone,mobilephone, ip) VALUES
  (10,'Компания 1','админ','172.16.2.170','461','463','+54654654','127.0.0.1');
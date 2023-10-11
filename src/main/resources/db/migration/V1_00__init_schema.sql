CREATE TABLE API_USERS (
  login varchar(50) NOT NULL,
  email varchar(128) NOT NULL,
  enabled boolean NOT NULL,
  nom varchar(50) NOT NULL,
  prenom varchar(50) NOT NULL,
  password varchar(155) NOT NULL,
  derniere_connexion timestamp,
  role varchar(10) NOT NULL,
  PRIMARY KEY (login)
) ;


INSERT INTO API_USERS(login,email,enabled,nom,prenom,password,role) VALUES ('batman', 'batman@yopmail.fr', true, 'Wayne', 'Bruce', '$2a$10$tp3K84d73j.cyP4tF0TKV.f7piG8S21w8g4oAXLyVI897M88eXUYC', 'SYSTEM');
INSERT INTO API_USERS(login,email,enabled,nom,prenom,password,role) VALUES ('spiderman', 'spiderman@yopmail.fr', true, 'Parker', 'Peter', '$2a$10$tp3K84d73j.cyP4tF0TKV.f7piG8S21w8g4oAXLyVI897M88eXUYC', 'ADMIN');
INSERT INTO API_USERS(login,email,enabled,nom,prenom,password,role) VALUES ('invisiblegirl', 'invisiblegirl@yopmail.fr', true, 'Storm', 'Jane', '$2a$10$tp3K84d73j.cyP4tF0TKV.f7piG8S21w8g4oAXLyVI897M88eXUYC', 'USER');

CREATE TABLE `user` (
  `login` varchar(50) NOT NULL,
  `email` varchar(128) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `nom` varchar(50) NOT NULL,
  `prenom` varchar(50) NOT NULL,
  `password` varchar(155) NOT NULL,
  `derniere_connexion` timestamp,
  `role` varchar(10) NOT NULL,
  PRIMARY KEY (`login`)
) ;
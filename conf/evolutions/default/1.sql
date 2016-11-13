# User schema

# --- !Ups
create table `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `first_name` TEXT NOT NULL,
  `last_name` TEXT NOT NULL,
  `email` TEXT NOT NULL,
  `password` TEXT NOT NULL
);

create table `books` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `title` TEXT NOT NULL,
  `author` TEXT NOT NULL,
  `language` TEXT NOT NULL,
  `year` TEXT NOT NULL,
  `publisher` TEXT NOT NULL
);

# --- !Downs
drop table `user`;
drop table `books`;


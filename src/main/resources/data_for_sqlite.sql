create table users
(
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    login    varchar(100) NOT NULL UNIQUE,
    password varchar      NOT NULL
);

create table locations
(
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    location_name varchar(100),
    user_id       INTEGER,
    latitude      decimal,
    longitude     decimal,
    foreign key (user_id) references users (id)
);

create table sessions
(
    id         uuid PRIMARY KEY,
    user_id    INTEGER,
    expires_at timestamp,
    foreign key (user_id) references users (id)
);
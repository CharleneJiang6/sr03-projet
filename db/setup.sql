CREATE TABLE IF NOT EXISTS user
(
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    firstname TEXT    NOT NULL,
    lastname  TEXT    NOT NULL,
    mail      TEXT    NOT NULL,
    password  TEXT    NOT NULL,
    activated BOOLEAN NOT NULL DEFAULT TRUE,
    admin     BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS channel
(
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    title           TEXT     NOT NULL,
    description     TEXT     NOT NULL,
    creation_date   DATETIME NOT NULL,
    expiration_date DATETIME NOT NULL,
    owner_id         INTEGER NOT NULL,
    type TEXT NOT NULL CHECK(type IN ('GROUP', 'PRIVATE')),
    FOREIGN KEY (owner_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS participation
(
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id    INTEGER NOT NULL,
    channel_id INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user (id),
    FOREIGN KEY (channel_id) REFERENCES channel (id)
);

CREATE TABLE IF NOT EXISTS message
(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    content TEXT NOT NULL,
    creation_date DATETIME NOT NULL,
    sender_id INTEGER NOT NULL,
    channel_id INTEGER NOT NULL,
    FOREIGN KEY (sender_id) REFERENCES user(id),
    FOREIGN KEY (channel_id) REFERENCES channel(id)
);

CREATE TABLE IF NOT EXISTS invitation
(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    sender_id INTEGER NOT NULL,
    receiver_id INTEGER NOT NULL,
    channel_id INTEGER NOT NULL,
    status TEXT NOT NULL CHECK(status IN ('PENDING', 'ACCEPTED', 'REFUSED')),
    creation_date DATETIME NOT NULL,
    FOREIGN KEY (sender_id) REFERENCES user(id),
    FOREIGN KEY (receiver_id) REFERENCES user(id),
    FOREIGN KEY (channel_id) REFERENCES channel(id)
);
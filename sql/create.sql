DROP TABLE IF EXISTS Photo;
DROP TABLE IF EXISTS Users;

-- Entities

CREATE TABLE Users (
    userID BIGINT NOT NULL,
    username VARCHAR(64) NOT NULL,
    fname VARCHAR(32) NOT NULL,
    lname VARCHAR(32) NOT NULL,
    pwd CHAR(64) NOT NULL,
    PRIMARY KEY(username)
);

CREATE TABLE Photo (
    pid BIGINT NOT NULL,
    username VARCHAR(64) NOT NULL,
    title CHAR(128) NOT NULL,
    likes BIGINT NOT NULL,
    dislikes BIGINT NOT NULL, 
    pdate DATE NOT NULL,
    PRIMARY KEY (pid),
    FOREIGN KEY (username) REFERENCES Users(username)
);

-- Relations

----------------------------
-- INSERT DATA STATEMENTS --
----------------------------

COPY Users (
    userID,
    username,
    fname,
    lname,
    pwd
)
FROM 'Users.csv'
WITH DELIMITER ',';

COPY Photo (
    pid,
    username,	
    title,
    likes,
    dislikes,
    pdate	
)
FROM 'Photo.csv'
WITH DELIMITER ',';

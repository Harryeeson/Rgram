DROP TABLE IF EXISTS Photo;
DROP TABLE IF EXISTS Users;

-- Entities

CREATE TABLE Users (
    userID BIGINT NOT NULL,
    fname VARCHAR(32) NOT NULL,
    lname VARCHAR(32) NOT NULL,
    PRIMARY KEY(userID)
);

CREATE TABLE Photo (
    pid BIGINT NOT NULL,
    userID BIGINT NOT NULL,
    title CHAR(128) NOT NULL,
    likes BIGINT NOT NULL,
    dislikes BIGINT NOT NULL, 
    pdate DATE NOT NULL,
    PRIMARY KEY (pid),
    FOREIGN KEY (userID) REFERENCES Users(userID)
);

-- Relations

----------------------------
-- INSERT DATA STATEMENTS --
----------------------------

COPY Users (
    userID,
    fname,
    lname
)
FROM 'Users.csv'
WITH DELIMITER ',';

COPY Photo (
    pid,
    userID,	
    title,
    likes,
    dislikes,
    pdate	
)
FROM 'Photo.csv'
WITH DELIMITER ',';

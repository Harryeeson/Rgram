DROP TABLE IF EXISTS Followers;
DROP TABLE IF EXISTS PhotoComments;
DROP TABLE IF EXISTS Tags;
DROP TABLE IF EXISTS Photo;
DROP TABLE IF EXISTS Users;

-- Entities

CREATE TABLE Users (
    userID BIGINT NOT NULL,
    username VARCHAR(64) NOT NULL,
    fname VARCHAR(32) NOT NULL,
    lname VARCHAR(32) NOT NULL,
    pwd VARCHAR(64) NOT NULL,
    PRIMARY KEY(username)
);

CREATE TABLE Photo (
    pid BIGINT NOT NULL,
    username VARCHAR(64) NOT NULL,
    title CHAR(128) NOT NULL,
    likes BIGINT NOT NULL,
    dislikes BIGINT NOT NULL, 
    pdate DATE NOT NULL,
    PRIMARY KEY(pid),
    FOREIGN KEY(username) REFERENCES Users(username)
);

CREATE TABLE PhotoComments (
    cid BIGINT NOT NULL,
    pid BIGINT NOT NULL,
    commentor VARCHAR(64) NOT NULL,
    comments VARCHAR(128) NOT NULL,
    PRIMARY KEY(cid),
    FOREIGN KEY(pid) REFERENCES Photo(pid)
);

CREATE TABLE Tags (
    tid BIGINT NOT NULL,
    pid BIGINT NOT NULL,
    tagging VARCHAR(128) NOT NULL,
    PRIMARY KEY(tid),
    FOREIGN KEY(pid) REFERENCES Photo(pid)
);

CREATE TABLE Followers (
    username VARCHAR(64) NOT NULL,
    following_usr VARCHAR(64) NOT NULL,
    PRIMARY KEY(following_usr),
    FOREIGN KEY(username) REFERENCES Users(username)
); 

-- Relations

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

COPY PhotoComments (
    cid,
    pid,
    commentor,
    comments
)
FROM 'PhotoComments.csv'
WITH DELIMITER ',';

COPY Tags (
    tid,
    pid,
    tagging
)
FROM 'Tags.csv'
WITH DELIMITER ',';

COPY Followers (
    username,
    following_usr
)
FROM 'Followers.csv'
WITH DELIMITER ',';
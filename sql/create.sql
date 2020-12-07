CREATE TABLE Users (
    uid INTEGER NOT NULL,
    fname VARCHAR(32),
    lname VARCHAR(32),
    num_follows INTEGER,
    num_followers INTEGER,
    PRIMARY KEY(uid)
);


CREATE TABLE Photo (
    pid INTEGER NOT NULL,
    uid INTEGER NOT NULL,
    title CHAR(32) NOT NULL,
    likes INTEGER NOT NULL,
    dislikes INTEGER NOT NULL, 
    date CHAR(32) NOT NULL,
    publishing_user CHAR(32) NOT NULL, 
    PRIMARY KEY (pid),    
    FOREIGN KEY (uid) REFERENCES Users(uid)
);

CREATE TABLE Has (
    userid  INTEGER NOT NULL,
    photoid  INTEGER NOT NULL,
    PRIMARY KEY(userid, photoid),
    FOREIGN KEY (userid) REFERENCES Users (uid),
    FOREIGN KEY (photoid) REFERENCES Photo(pid)
);

CREATE TABLE Follows(
    userid1  INTEGER NOT NULL,
    userid2  INTEGER NOT NULL,
    FOREIGN KEY (userid1) REFERENCES Users (uid),
    FOREIGN KEY (userid2) REFERENCES Users (uid),
    PRIMARY KEY(userid1,userid2)
);


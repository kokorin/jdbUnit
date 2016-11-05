CREATE TABLE Test (
    tBoolean BIT,
    tInt INT,
    tLong BIGINT,
    tFloat REAL,
    tDouble DOUBLE,
    tDate DATE,
    tTime TIME,
    tTimestamp TIMESTAMP,
    tString VARCHAR(255)
);

CREATE TABLE SUser (
    id INT PRIMARY KEY,
    login VARCHAR(255),
    password VARCHAR(255)
);

CREATE TABLE SRole (
    id INT PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE SUser_SRole (
    user_id INT NOT NULL,
    role_id INT NOT NULL,

    FOREIGN KEY (user_id) REFERENCES SUser(id),
    FOREIGN KEY (role_id) REFERENCES SRole(id)
);




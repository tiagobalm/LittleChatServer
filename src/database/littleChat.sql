PRAGMA foreign_keys = ON;

DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS Message;
DROP TABLE IF EXISTS Room;
DROP TABLE IF EXISTS Friend;
DROP TABLE IF EXISTS UserRoom;

CREATE TABLE User (
	userID INTEGER PRIMARY KEY AUTOINCREMENT,
	username VARCHAR(20) UNIQUE NOT NULL,
	password VARCHAR(20) NOT NULL
);

CREATE TABLE UserConnection (
	userID INTEGER PRIMARY KEY,
	ip VARCHAR(15),
	port INTEGER
);

CREATE TABLE Message (
	messageID INTEGER PRIMARY KEY AUTOINCREMENT,
	userID INTEGER,
	roomID INTEGER,
	message VARCHAR(1000),
	sentDate DATETIME,
	FOREIGN KEY(userID) REFERENCES User(userID)
				ON UPDATE CASCADE,
	FOREIGN KEY(roomID) REFERENCES Room(roomID)
				ON UPDATE CASCADE
);

CREATE TABLE Room (
	roomID INTEGER PRIMARY KEY AUTOINCREMENT,
	name VARCHAR(50)
);

CREATE TABLE Friend (
	firstUserID INTEGER,
	secondUserID INTEGER,
	FOREIGN KEY(firstUserID) REFERENCES User(firstUserID)
				ON UPDATE CASCADE,
	FOREIGN KEY(secondUserID) REFERENCES User(secondUserID)
				ON UPDATE CASCADE,
	PRIMARY KEY(firstUserID, secondUserID)
);

CREATE TABLE UserRoom (
	userID INTEGER,
	roomID INTEGER,
	FOREIGN KEY(userID) REFERENCES User(userID)
				ON UPDATE CASCADE,
	FOREIGN KEY(roomID) REFERENCES Room(roomID)
				ON UPDATE CASCADE,
	PRIMARY KEY(userID, roomID)
);

INSERT INTO User(username, password) VALUES ('vascoUP', 'vascoUP');

PRAGMA foreign_keys = ON;

DROP TABLE IF EXISTS UserConnection;
DROP TABLE IF EXISTS Message;
DROP TABLE IF EXISTS Friend;
DROP TABLE IF EXISTS UserRoom;
DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS Room;

CREATE TABLE User (
	userID INTEGER PRIMARY KEY AUTOINCREMENT,
	username VARCHAR(20) UNIQUE NOT NULL,
	password VARCHAR(20) NOT NULL
);

CREATE TABLE UserConnection (
	userID INTEGER,
	ip VARCHAR(15),
	port INTEGER,
	FOREIGN KEY(userID) REFERENCES User(userID)
		ON UPDATE CASCADE
);

CREATE TABLE Message (
	messageID INTEGER PRIMARY KEY AUTOINCREMENT,
	userID INTEGER,
	roomID INTEGER,
	message VARCHAR(1000),
	sentDate DATETIME DEFAULT NOW,
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
	friendStatus BOOLEAN DEFAULT 0,
	FOREIGN KEY(firstUserID) REFERENCES User(userID)
		ON UPDATE CASCADE,
	FOREIGN KEY(secondUserID) REFERENCES User(userID)
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

CREATE TABLE MessageClass (
	messageClassID INTEGER PRIMARY KEY AUTOINCREMENT,
	header varchar(500),
	message varchar(5000)
);

CREATE TABLE StringList(
	stringListID INTEGER PRIMARY KEY AUTOINCREMENT,
	messageClassID INTEGER,
	string varchar(500),
	FOREIGN KEY(messageClassID) REFERENCES MessageClass(messageClassID)
		ON UPDATE CASCADE
);

INSERT INTO User(username, password) VALUES ('vascoUP', 'vascoUP');
INSERT INTO User(username, password) VALUES('saraUP', 'saraUP');
INSERT INTO User(username, password) VALUES('tiagoUP', 'tiagoUP');
INSERT INTO User(username, password) VALUES('arianaUP', 'arianaUP');

INSERT INTO Room(name) VALUES('Chat Room 1');
INSERT INTO Room(name) VALUES('Chat Room 2');
INSERT INTO Room(name) VALUES('Chat Room 3');
INSERT INTO Room(name) VALUES('Chat Room 4');
INSERT INTO Room(name) VALUES('Chat Room 5');

INSERT INTO UserRoom(userID, roomID) VALUES (1,1);
INSERT INTO UserRoom(userID, roomID) VALUES (1,2);
INSERT INTO UserRoom(userID, roomID) VALUES (2,2);
INSERT INTO UserRoom(userID, roomID) VALUES (2,3);
INSERT INTO UserRoom(userID, roomID) VALUES (2,5);
INSERT INTO UserRoom(userID, roomID) VALUES (3,4);
INSERT INTO UserRoom(userID, roomID) VALUES (3,5);
INSERT INTO UserRoom(userID, roomID) VALUES (4,1);
INSERT INTO UserRoom(userID, roomID) VALUES (4,3);
INSERT INTO UserRoom(userID, roomID) VALUES (4,4);

INSERT INTO Friend(firstUserID, secondUserID, friendStatus) VALUES(1,2,1);
INSERT INTO Friend(firstUserID, secondUserID, friendStatus) VALUES(1,4,1);
INSERT INTO Friend(firstUserID, secondUserID, friendStatus) VALUES(2,1,1);
INSERT INTO Friend(firstUserID, secondUserID, friendStatus) VALUES(2,3,1);
INSERT INTO Friend(firstUserID, secondUserID, friendStatus) VALUES(2,4,1);
INSERT INTO Friend(firstUserID, secondUserID, friendStatus) VALUES(3,2,1);
INSERT INTO Friend(firstUserID, secondUserID, friendStatus) VALUES(3,4,1);
INSERT INTO Friend(firstUserID, secondUserID, friendStatus) VALUES(4,1,1);
INSERT INTO Friend(firstUserID, secondUserID, friendStatus) VALUES(4,2,1);
INSERT INTO Friend(firstUserID, secondUserID, friendStatus) VALUES(4,3,1);

INSERT INTO Message(userID, roomID, message, sentDate) VALUES (1,1,'Hi friends!', "2017-05-08 15:22:00");
INSERT INTO Message(userID, roomID, message, sentDate) VALUES (4,1,'Oi amiguinho', "2017-05-08 15:23:00");
INSERT INTO Message(userID, roomID, message, sentDate) VALUES (1,2,'Oi amiguinho', "2017-05-09 15:00:00");
INSERT INTO Message(userID, roomID, message, sentDate) VALUES (2,2,'Estou ocupado, não posso falar', "2017-05-09 15:00:40");
INSERT INTO Message(userID, roomID, message, sentDate) VALUES (2,3,'Temos que falar', "2017-05-06 00:00:00");
INSERT INTO Message(userID, roomID, message, sentDate) VALUES (2,5,'Hoje não fiz nada', "2017-05-11 05:00:00");
INSERT INTO Message(userID, roomID, message, sentDate) VALUES (3,5,'Nem eu! Que seca', "2017-05-11 05:02:00");
INSERT INTO Message(userID, roomID, message, sentDate) VALUES (3,4,'Que chato que ele é', "2017-05-11 06:40:00");
INSERT INTO Message(userID, roomID, message, sentDate) VALUES (4,4,'...', "2017-05-11 06:40:56");
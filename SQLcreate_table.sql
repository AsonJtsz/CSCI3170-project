/*
drop table if exists Takes;
drop table if exists Drives;
drop table if exists Makes;
*/
drop table if exists Request;
drop table if exists Trips;
drop table if exists Taxi_Stop;
drop table if exists Passenger;
drop table if exists Driver;
drop table if exists Vehicle;

create table Vehicle(
  VID VARCHAR(6) NOT NULL,
  Model VARCHAR(30) NOT NULL, 
  Seats Integer NOT NULL,
  PRIMARY KEY(VID)
);

create table Driver(
  DID Integer NOT NULL,
  Name VARCHAR(30) NOT NULL, 
  VID VARCHAR(6) NOT NULL, 
  Driving_Years Integer NOT NULL, 
  PRIMARY KEY(DID),
  FOREIGN KEY(VID) REFERENCES Vehicle(VID)
);

create table Passenger(
  PID Integer NOT NULL,
  Name VARCHAR(30) NOT NULL, 
  PRIMARY KEY(PID)
);

create table Taxi_Stop(
  Name VARCHAR(20) NOT NULL,
  X_Coordinate Integer NOT NULL,
  Y_Coordinate Integer NOT NULL,
  PRIMARY KEY(Name)
);

create table Trips(
  TID Integer NOT NULL,
  DID Integer NOT NULL,
  PID Integer NOT NULL,
  Start_Time DATETIME NOT NULL,
  End_Time DATETIME,
  Start_Location VARCHAR(20) NOT NULL,
  Destination VARCHAR(20) NOT NULL,
  Fee Integer,
  PRIMARY KEY(TID),
  FOREIGN KEY(DID) REFERENCES Driver(DID),
  FOREIGN KEY(PID) REFERENCES Passenger(PID),
  FOREIGN KEY(Start_Location) REFERENCES Taxi_Stop(Name),
  FOREIGN KEY(Destination) REFERENCES Taxi_Stop(Name)
);

create table Request(
  RID Integer NOT NULL,
  PID Integer NOT NULL,
  Number_Of_Passengers Integer NOT NULL,
  Start_Location VARCHAR(20) NOT NULL,
  Destination VARCHAR(20) NOT NULL,
  Partial_Model VARCHAR(30),
  Minimum_Driving_Years Integer,
  Taken BOOLEAN DEFAULT false,
  PRIMARY KEY(RID),
  FOREIGN KEY(Start_Location) REFERENCES Taxi_Stop(Name),
  FOREIGN KEY(Destination) REFERENCES Taxi_Stop(Name)
);

/*
create table Makes(
  PID Integer NOT NULL,
  RID Integer NOT NULL,
  PRIMARY KEY(PID, RID),
  FOREIGN KEY(PID) REFERENCES Passenger(PID),
  FOREIGN KEY(RID) REFERENCES Request(RID)
);

create table Drives(
  DID Integer NOT NULL,
  VID VARCHAR(6) NOT NULL,
  PRIMARY KEY(DID, VID),
  FOREIGN KEY(DID) REFERENCES Driver(DID),
  FOREIGN KEY(VID) REFERENCES Vehicle(VID)
);

create table Takes(
  DID Integer NOT NULL,
  RID Integer NOT NULL,
  TID Integer NOT NULL,
  PRIMARY KEY(DID, RID, TID),
  FOREIGN KEY(DID) REFERENCES Driver(DID),
  FOREIGN KEY(RID) REFERENCES Request(RID),
  FOREIGN KEY(TID) REFERENCES Trips(TID)
);
*/
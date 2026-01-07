DROP TABLE Registrations CASCADE CONSTRAINTS;
DROP TABLE Workshops CASCADE CONSTRAINTS;

CREATE TABLE Workshops (
  WorkshopID   NUMBER PRIMARY KEY,
  Title        VARCHAR2(120) NOT NULL,
  Category     VARCHAR2(40)  NOT NULL,
  EventDate    DATE          NOT NULL,
  Location     VARCHAR2(80)  NOT NULL,
  Capacity     NUMBER(4)     NOT NULL
);

CREATE TABLE Registrations (
  WorkshopID   NUMBER       NOT NULL,
  StudentID    VARCHAR2(12) NOT NULL,
  RegisteredOn DATE         NOT NULL,
  PRIMARY KEY (WorkshopID, StudentID),
  FOREIGN KEY (WorkshopID) REFERENCES Workshops(WorkshopID)
);


-- Workshops

INSERT INTO Workshops VALUES (101, 'Intro to Data Visualization', 'Tech', DATE '2025-11-05', 'Innovation Hall 204', 40);
INSERT INTO Workshops VALUES (102, 'Resume & Cover Letter Clinic', 'Career', DATE '2025-11-06', 'Johnson Center 3rd Fl', 50);
INSERT INTO Workshops VALUES (103, 'Mindfulness for Midterms', 'Wellness', DATE '2025-11-07', 'SUB I Room 1201', 30);
INSERT INTO Workshops VALUES (104, 'Git & GitHub Basics', 'Tech', DATE '2025-11-08', 'Engineering 110', 35);
INSERT INTO Workshops VALUES (105, 'Networking for Internships', 'Career', DATE '2025-11-10', 'Career Center Hall A', 60);
INSERT INTO Workshops VALUES (106, 'Public Speaking Fundamentals', 'Career', DATE '2025-11-12', 'Johnson Center Cinema', 90);
INSERT INTO Workshops VALUES (107, 'Stretch & Study: Desk Mobility', 'Wellness', DATE '2025-11-13', 'Recreation Center Studio B', 25);
INSERT INTO Workshops VALUES (108, 'Time Management for Finals', 'Wellness', DATE '2025-11-15', 'SUB I Room 1101', 40);


-- Registrations

INSERT INTO Registrations VALUES (101, 'G01230001', DATE '2025-10-28');
INSERT INTO Registrations VALUES (101, 'G01230002', DATE '2025-10-28');
INSERT INTO Registrations VALUES (101, 'G01230003', DATE '2025-10-29');

INSERT INTO Registrations VALUES (102, 'G01230001', DATE '2025-10-29');
INSERT INTO Registrations VALUES (102, 'G01230004', DATE '2025-10-30');
INSERT INTO Registrations VALUES (102, 'G01230005', DATE '2025-10-30');

INSERT INTO Registrations VALUES (103, 'G01230002', DATE '2025-10-30');
INSERT INTO Registrations VALUES (103, 'G01230006', DATE '2025-10-31');

INSERT INTO Registrations VALUES (104, 'G01230003', DATE '2025-10-31');
INSERT INTO Registrations VALUES (104, 'G01230007', DATE '2025-10-31');
INSERT INTO Registrations VALUES (104, 'G01230008', DATE '2025-11-01');

INSERT INTO Registrations VALUES (105, 'G01230001', DATE '2025-11-01');
INSERT INTO Registrations VALUES (105, 'G01230009', DATE '2025-11-01');
INSERT INTO Registrations VALUES (105, 'G01230010', DATE '2025-11-02');

INSERT INTO Registrations VALUES (106, 'G01230004', DATE '2025-11-02');
INSERT INTO Registrations VALUES (106, 'G01230011', DATE '2025-11-02');
INSERT INTO Registrations VALUES (106, 'G01230012', DATE '2025-11-03');

INSERT INTO Registrations VALUES (107, 'G01230002', DATE '2025-11-03');
INSERT INTO Registrations VALUES (107, 'G01230013', DATE '2025-11-03');

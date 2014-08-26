CREATE TABLE CMT_OABA_BATCHJOB (
 ID NUMERIC(19) NOT NULL,
 DESCRIPTION VARCHAR(255) NULL,
 EXTERNAL_ID VARCHAR(255) NULL,
 FRACTION_COMPLETE INTEGER NULL,
 STATUS INTEGER NULL,
 TRANSACTION_ID NUMERIC(19) NULL,
 TYPE VARCHAR(255) NULL,
 PRIMARY KEY (ID)
);

CREATE TABLE CMT_OABA_BATCHJOB_TIMESTAMPS (
 BATCHJOB_ID NUMERIC(19) NULL,
 TIMESTAMP DATETIME NULL,
 STATUS INTEGER NULL
);

ALTER TABLE CMT_OABA_BATCHJOB_TIMESTAMPS
 ADD CONSTRAINT CMTBBTCHJBTMSTMBTCHJBD
  FOREIGN KEY (BATCHJOB_ID) REFERENCES CMT_OABA_BATCHJOB (ID);

--CREATE TABLE CMT_SEQUENCE (
-- SEQ_NAME VARCHAR(50) NOT NULL,
-- SEQ_COUNT NUMERIC(28) NULL,
-- PRIMARY KEY (SEQ_NAME)
--);
--
--INSERT INTO CMT_SEQUENCE(SEQ_NAME, SEQ_COUNT)
-- values ('OABA_BATCHJOB', 0);

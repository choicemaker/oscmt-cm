USE [EPI_MPIMODELING]
GO

INSERT INTO [dbo].[CMT_NAMED_CONFIG2]
           ([NC_ID]
           ,[ABA_BLOCKSET]
           ,[ABA_SINGLESET]
           ,[ABA_MATCHES]
           ,[ABA_SINGLETABLE]
           ,[BLOCKING_CONF]
           ,[NC_DESC]
           ,[NC_NAME]
           ,[DATA_SOURCE]
           ,[HIGH_THRESHOLD]
           ,[CLASS]
           ,[LOW_THRESHOLD]
           ,[MODEL]
           ,[OABA_INTERVAL]
           ,[OABA_BLOCKSIZE]
           ,[OABA_CHUNKSIZE]
           ,[OABA_MATCHES]
           ,[OABA_OVERSIZE]
           ,[OABA_SINGLE]
           ,[OABA_OSFIELDS]
           ,[OPTLOCK]
           ,[QUERY_DBCONF]
           ,[QUERY_RS_DEDUPED]
           ,[QUERY_SQL]
           ,[RS_TYPE]
           ,[REF_DBACC]
           ,[REF_DBCONF]
           ,[REF_DBREAD]
           ,[REF_SQL]
           ,[RIGOR]
           ,[FILE_URI]
           ,[SVR_FILES_COUNT]
           ,[SVR_FILE_ENTRIES]
           ,[SVR_THREADS]
           ,[TASK]
           ,[FORMAT]
           ,[GRAPH]
           ,[UUID])
SELECT
    -- <NC_ID, numeric(19,0),>
       [NC_ID]
    -- <ABA_BLOCKSET, int,>
      ,[LIMIT_BLOCKSET]
    -- <ABA_SINGLESET, int,>
      ,[LIMIT_SINGLESET]
    -- <ABA_MATCHES, int,>
      ,[ABA_MAX_MATCHES]
    -- <ABA_SINGLETABLE, int,>
      ,[LIMIT_SINGLETABLE]
    -- <BLOCKING_CONF, varchar(255),>
      ,[BLOCKING_CONF]
    -- <NC_DESC, varchar(255),>
      ,[NC_DESC]
    -- <NC_NAME, varchar(255),>
      ,[NC_NAME]
    -- <DATA_SOURCE, varchar(255),>
      ,[DATA_SOURCE]
    -- <HIGH_THRESHOLD, real,>
      ,[HIGH_THRESHOLD]
    -- <CLASS, varchar(255),>
      ,[CLASS]
    -- <LOW_THRESHOLD, real,>
      ,[LOW_THRESHOLD]
    -- <MODEL, varchar(255),>
      ,[MODEL]
    -- <OABA_INTERVAL, int,>
      ,[INTERVAL]
    -- <OABA_BLOCKSIZE, int,>
      ,[MAX_BLOCKSIZE]
    -- <OABA_CHUNKSIZE, int,>
      ,[MAX_CHUNKSIZE]
    -- <OABA_MATCHES, int,>
      ,[MAX_MATCHES]
    -- <OABA_OVERSIZE, int,>
      ,[MAX_OVERSIZE]
    -- <OABA_SINGLE, int,>
      ,[MAX_SINGLE]
    -- <OABA_OSFIELDS, int,>
      ,[MIN_FIELDS]
    -- <OPTLOCK, int,>
      ,[OPTLOCK]
    -- <QUERY_DBCONF, varchar(255),>
      ,[QUERY_DBCONF]
    -- <QUERY_RS_DEDUPED, bit,>
      ,[QUERY_RS_DEDUPED]
    -- <QUERY_SQL, varchar(255),>
      ,[QUERY_SQL]
    -- <RS_TYPE, varchar(255),>
      ,[RS_TYPE]
    -- <REF_DBACC, varchar(255),>
      ,[REF_DBACC]
    -- <REF_DBCONF, varchar(255),>
      ,[REF_DBCONF]
    -- <REF_DBREAD, varchar(255),>
      ,[REF_DBREAD]
    -- <REF_SQL, varchar(255),>
      ,[REF_SQL]
    -- <RIGOR, varchar(255),>
      ,[RIGOR]
    -- <FILE_URI, varchar(255),>
      ,[FILE_URI]
    -- <SVR_FILES_COUNT, int,>
      ,[MAX_CHUNK_COUNT]
    -- <SVR_FILE_ENTRIES, int,>
      ,[MAX_CHUNK_SIZE]
    -- <SVR_THREADS, int,>
      ,[MAX_THREADS]
    -- <TASK, varchar(255),>
      ,[TASK]
    -- <FORMAT, varchar(255),>
      ,[FORMAT]
    -- <GRAPH, varchar(255),>
      ,[GRAPH]
    -- <UUID, varchar(255),>
      ,[UUID]
  FROM [dbo].[CMT_NAMED_CONFIG]
GO




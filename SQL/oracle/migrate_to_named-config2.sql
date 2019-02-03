INSERT INTO "CMT_NAMED_CONFIG2"
           ("NC_ID"
           ,"ABA_BLOCKSET"
           ,"ABA_SINGLESET"
           ,"ABA_MATCHES"
           ,"ABA_SINGLETABLE"
           ,"BLOCKING_CONF"
           ,"NC_DESC"
           ,"NC_NAME"
           ,"DATA_SOURCE"
           ,"HIGH_THRESHOLD"
           ,"CLASS"
           ,"LOW_THRESHOLD"
           ,"MODEL"
           ,"OABA_INTERVAL"
           ,"OABA_BLOCKSIZE"
           ,"OABA_CHUNKSIZE"
           ,"OABA_MATCHES"
           ,"OABA_OVERSIZE"
           ,"OABA_SINGLE"
           ,"OABA_OSFIELDS"
           ,"OPTLOCK"
           ,"QUERY_DBCONF"
           ,"QUERY_RS_DEDUPED"
           ,"QUERY_SQL"
           ,"RS_TYPE"
           ,"REF_DBACC"
           ,"REF_DBCONF"
           ,"REF_DBREAD"
           ,"REF_SQL"
           ,"RIGOR"
           ,"FILE_URI"
           ,"SVR_FILES_COUNT"
           ,"SVR_FILE_ENTRIES"
           ,"SVR_THREADS"
           ,"TASK"
           ,"FORMAT"
           ,"GRAPH"
           ,"UUID")
SELECT
    -- <"NC_ID", number(19,0)>
       "NC_ID"
    -- <"ABA_BLOCKSET", number(10,0)>
      ,"LIMIT_BLOCKSET"
    -- <"ABA_SINGLESET", number(10,0)>
      ,"LIMIT_SINGLESET"
    -- <"ABA_MATCHES", number(10,0)>
      ,"ABA_MAX_MATCHES"
    -- <"ABA_SINGLETABLE", number(10,0)>
      ,"LIMIT_SINGLETABLE"
    -- <"BLOCKING_CONF", varchar2(255)>
      ,"BLOCKING_CONF"
    -- <"NC_DESC", varchar2(255)>
      ,"NC_DESC"
    -- <"NC_NAME", varchar2(255)>
      ,"NC_NAME"
    -- <"DATA_SOURCE", varchar2(255)>
      ,"DATA_SOURCE"
    -- <"HIGH_THRESHOLD", float>
      ,"HIGH_THRESHOLD"
    -- <"CLASS", varchar2(255)>
      ,"CLASS"
    -- <"LOW_THRESHOLD", float>
      ,"LOW_THRESHOLD"
    -- <"MODEL", varchar2(255)>
      ,"MODEL"
    -- <"OABA_INTERVAL", number(10,0)>
      ,"INTERVAL"
    -- <"OABA_BLOCKSIZE", number(10,0)>
      ,"MAX_BLOCKSIZE"
    -- <"OABA_CHUNKSIZE", number(10,0)>
      ,"MAX_CHUNKSIZE"
    -- <"OABA_MATCHES", number(10,0)>
      ,"MAX_MATCHES"
    -- <"OABA_OVERSIZE", number(10,0)>
      ,"MAX_OVERSIZE"
    -- <"OABA_SINGLE", number(10,0)>
      ,"MAX_SINGLE"
    -- <"OABA_OSFIELDS", number(10,0)>
      ,"MIN_FIELDS"
    -- <"OPTLOCK", number(10,0)>
      ,"OPTLOCK"
    -- <"QUERY_DBCONF", varchar2(255)>
      ,"QUERY_DBCONF"
    -- <"QUERY_RS_DEDUPED", number(1,0)>
      ,"QUERY_RS_DEDUPED"
    -- <"QUERY_SQL", varchar2(255)>
      ,"QUERY_SQL"
    -- <"RS_TYPE", varchar2(255)>
      ,"RS_TYPE"
    -- <"REF_DBACC", varchar2(255)>
      ,"REF_DBACC"
    -- <"REF_DBCONF", varchar2(255)>
      ,"REF_DBCONF"
    -- <"REF_DBREAD", varchar2(255)>
      ,"REF_DBREAD"
    -- <"REF_SQL", varchar2(255)>
      ,"REF_SQL"
    -- <"RIGOR", varchar2(255)>
      ,"RIGOR"
    -- <"FILE_URI", varchar2(255)>
      ,"FILE_URI"
    -- <"SVR_FILES_COUNT", number(10,0)>
      ,"MAX_CHUNK_COUNT"
    -- <"SVR_FILE_ENTRIES", number(10,0)>
      ,"MAX_CHUNK_SIZE"
    -- <"SVR_THREADS", number(10,0)>
      ,"MAX_THREADS"
    -- <"TASK", varchar2(255)>
      ,"TASK"
    -- <"FORMAT", varchar2(255)>
      ,"FORMAT"
    -- <"GRAPH", varchar2(255)>
      ,"GRAPH"
    -- <"UUID", varchar2(255)>
      ,"UUID"
  FROM "CMT_NAMED_CONFIG"
;


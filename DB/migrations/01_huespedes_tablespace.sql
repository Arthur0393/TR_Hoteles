-- Ejecutar como SYS en DbHuespedes. Conserva los datos existentes.
WHENEVER SQLERROR EXIT SQL.SQLCODE
ALTER SESSION SET CONTAINER = FREEPDB1;
ALTER USER HUESPEDES DEFAULT TABLESPACE USERS QUOTA UNLIMITED ON USERS;

DECLARE
   v_tablespace VARCHAR2(128);
BEGIN
   SELECT tablespace_name INTO v_tablespace
     FROM dba_tables WHERE owner = 'HUESPEDES' AND table_name = 'HUESPEDES';
   IF v_tablespace <> 'USERS' THEN
      EXECUTE IMMEDIATE 'ALTER TABLE HUESPEDES.HUESPEDES MOVE TABLESPACE USERS';
   END IF;
   FOR idx IN (
      SELECT index_name FROM dba_indexes
       WHERE owner = 'HUESPEDES' AND table_name = 'HUESPEDES'
         AND (tablespace_name <> 'USERS' OR status = 'UNUSABLE')
   ) LOOP
      EXECUTE IMMEDIATE 'ALTER INDEX HUESPEDES.'
         || DBMS_ASSERT.ENQUOTE_NAME(idx.index_name, FALSE)
         || ' REBUILD TABLESPACE USERS';
   END LOOP;
END;
/
EXIT;

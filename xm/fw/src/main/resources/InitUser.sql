
create tablespace jiabi

logging
datafile 'C:\database\jiabi.dbf'
size 10m
autoextend on
next 10m maxsize 20480m
extent management local;


create user jiabi
  identified by jiabi
  default tablespace jiabi;

grant dba to jiabi;

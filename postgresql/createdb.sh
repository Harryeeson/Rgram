#!/bin/bash
folder=/tmp/$(logname)/mydb
PGDATA=$folder/data
PGSOCKETS=$folder/sockets
export PGDATA
export PGSOCKETS

root=$(realpath $(dirname "$0"))
echo $root
root=$(dirname $root)
echo $root
dbname=$(logname)_db
echo "creating db named ... $dbname"
createdb -h localhost $dbname
pg_ctl status


echo "Initializing tables .. "
psql -h localhost $dbname < ../sql/create.sql

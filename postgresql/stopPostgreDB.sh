#! /bin/bash
export PGPORT=9998                                                                                                                                                                                                                           export PGDATA=/tmp/$USER/myDB/data
pg_ctl -o "-c unix_socket_directories=$PGSOCKETS -p $PGPORT" -D $PGDATA -l $folder/logfile stop

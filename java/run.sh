#!/bin/bash
root=$(realpath $(dirname "$0"))
root=$(dirname $root)
dbname=$(logname)_db

cd $root/java

# Example: ./run.sh
java -cp lib/*:bin/ Rgram jshal001_db 5432 jshal001

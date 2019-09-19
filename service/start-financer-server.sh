#!/bin/bash

PID=$(lsof -t -i :3500)

if [[ ! -z "$PID" && "$PID" != " " ]];
then
        echo 'Financer server is currently running on pid:' "$PID"
        echo 'Killing process ...'
        kill -9 $PID
fi

cd ./de.raphaelmuesseler.financer.server/target/
java -jar financer-server.jar --database=prod &

echo 'Restarted financer server successfully!'
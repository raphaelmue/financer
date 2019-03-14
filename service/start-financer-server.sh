#!/bin/bash

kill -9 $(lsof -t -i :3500)
cd ./../de.raphaelmuesseler.financer.server/target/
java -jar de.raphaelmuesseler.financer.server-1.0-SNAPSHOT-jar-with-dependencies.jar --database=prod &
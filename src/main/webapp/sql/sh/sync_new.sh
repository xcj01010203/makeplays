#!/bin/bash
port=3306
host=123.57.235.81
db=app1
user=app
password=0okmnji98uhb7ygv
output=/tmp/pg-2-mysql.sql
echo "delete from $db.tab_apply_case;" > $output
#echo > $output
export PGPASSWORD=bxs188
pg_dump -Ubxs -h 192.168.10.150 -a taxi -t bxs.tab_apply_case --inserts >> $output
sed -i '2,16d' $output
mysql -h $host --port $port -p$password -u $user $db < $output
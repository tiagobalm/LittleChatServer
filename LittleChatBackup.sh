 #!/bin/bash

find -name "*.java" > sources.txt
mkdir out
javac -d ./out -cp ".:./sqlite-jdbc-3.8.10.2.jar:annotations-13.0.jar:" @sources.txt
cp -a ./src/keys ./out/
cp -a ./src/database/littleChat.sql ./out/database/
cat ./out/database/littleChat.sql | sqlite3 ./out/database/database.db
rm src/database/database.db && cat src/database/littleChat.sql | sqlite3 src/database/database.db
rm sources.txt

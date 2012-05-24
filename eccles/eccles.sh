#!/bin/sh
echo `date` - Starting Eccles >> /var/log/$NAME/${NAME}.out
exec $JAVA_HOME/bin/java -jar $ECCLES_HOME/eccles.jar \
     $ECCLES_HOME/eccles.properties >> /var/log/$NAME/${NAME}.out 2>&1


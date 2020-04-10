#!/bin/sh
JAVA_HOME=/usr/lib/jvm/java-13
CATALINA_PID=/opt/apache/tomcat/temp/tomcat.pid
JAVA_OPTS="$JAVA_OPTS -Darchie.home=/opt/hilel14/archie/beeri"
JAVA_OPTS="$JAVA_OPTS -Darchie.environment=develop"

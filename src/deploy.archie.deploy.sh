set -e

export JAVA_HOME=/usr/lib/jvm/java-11
echo Java home is $JAVA_HOME

mvn clean install # -DskipTests
mv target/archie-beeri-ws*.war /opt/apache/tomcat/webapps/

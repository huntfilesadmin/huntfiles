rem # create project:
rem # mvn archetype:generate -DgroupId=org.bcjj.huntfiles -DartifactId=huntfiles -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false

set JAVA_HOME=C:\jdk1.8.0_144

mvn clean install dependency:copy-dependencies dependency:sources

rem # mvn dependency:copy-dependencies # copia las dependencias a target/dependencies/

rem # cd target/
rem # java -cp huntfiles-0.1.jar HuntFiles

java -jar target/huntfiles-0.1-boot.jar






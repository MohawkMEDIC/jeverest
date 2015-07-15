SET JAVA_HOME=""C:\Program Files (x86)\Java\jdk1.7.0_60""
SET MAVEN_OPTS=-Xmx1048m
C:\data\mvn\bin\mvn clean site site-deploy compile package deploy 
C:\data\mvn\bin\mvn javadoc:jar deploy 
pause
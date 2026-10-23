@echo off
set PATH=C:\tools\apache-maven-3.10.0\bin;%PATH%
cd /d C:\tools\cloudsim-dynamic-scheduler
mvn exec:java -Dexec.mainClass=com.college.cloudsim.App -Dexec.args="fcfs 25"
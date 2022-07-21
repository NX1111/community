start cmd /k "D:\environments\kafka_2.12-2.2.0\kafka_2.12-2.2.0\bin\windows\zookeeper-server-start.bat  D:\environments\kafka_2.12-2.2.0\kafka_2.12-2.2.0\config\zookeeper.properties"
choice /t 10 /d y /n >nu
start cmd /k "D:\environments\kafka_2.12-2.2.0\kafka_2.12-2.2.0\bin\windows\kafka-server-start.bat  D:\environments\kafka_2.12-2.2.0\kafka_2.12-2.2.0\config\server.properties"
choice /t 10 /d y /n >nu
start cmd /k "D:\environments\elasticsearch-6.4.3\elasticsearch-6.4.3\bin\elasticsearch.bat"
choice /t 10 /d y /n >nu
start cmd /k "D:\environments\apache-tomcat-8.0.50-windows-x64\apache-tomcat-8.0.50\bin\startup.bat"






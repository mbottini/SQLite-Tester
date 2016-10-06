windows:
	echo "java -classpath `",;sqlite-jdbc-3.14.2.1.jar`" SQLiteJDBC" > run.ps1
	javac *.java

linux:
	echo "java -classpath \".:sqlite-jdbc-3.14.2.1.jar\" SQLiteJDBC" > run.sh
	javac *.java

	

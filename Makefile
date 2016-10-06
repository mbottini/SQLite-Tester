windows:
	$string = "java -classpath `",;sqlite-jdbc-3.14.2.1.jar`" SQLiteJDBC"
	$string | Out-File run.ps1

linux:
	echo "java -classpath \".:sqlite-jdbc-3.14.2.1.jar\" SQLiteJDBC" > run.sh
	javac *.java

	

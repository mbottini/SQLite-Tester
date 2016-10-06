ifdef SYSTEMROOT
    CREATE_RUN_SCRIPT="echo java -classpath ".;sqlite-jdbc-3.14.2.1.jar"\
    SQLiteJDBC > run.ps1"
else
    CREATE_RUN_SCRIPT=echo "java -classpath \".:sqlite-jdbc-3.14.2.1.jar\"\
    SQLiteJDBC > run.sh"
endif
	
all:
	$(CREATE_RUN_SCRIPT)
	javac *.java


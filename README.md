# SQLite-Tester
Testing repo for playing around with SQLite in Java.

# Dependencies:

Git, obviously.  
Make, if you want an easier time. 
Java JDK  
jdbc .jar, gotten from
[here](https://bitbucket.org/xerial/sqlite-jdbc/downloads).

#Linux 

Definitely working, mostly because I use Linux. Use your favored package manager to install Java if you haven't already.
If you're on Gentoo, compile OpenJDK from source or something, you glorious madman.

#Windows

* Install Git from [here](https://git-scm.com/download/win), if you haven't already. The download will start automatically.

* Install Java JDK-8 from 
[here](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html),
if you haven't already. Add C:\Program Files\Java\jdk1.8.0_101\bin\ to your PATH environment variable.

* Install Cygwin or MinGW if you want to use `make`. Otherwise, compile with
  `javac *.java` for now.

* Set your 
[Script Execution
Policy](https://technet.microsoft.com/en-us/library/ee176961.aspx) to
RemoteSigned.

#Usage:

Clone into some folder with the following:

    mkdir SQLite-Tester 
    cd SQLite-Tester
    git clone https://github.com/mbottini/SQLite-Tester.git .
    
Stick the jdbc jar inside this directory.
    
If you're running Linux, `make` with, um, `make`. Run with `sh run.sh`.

If you're running Windows, `make` with `mingw32-make`.

Run with `.\run.ps1` if you're using MinGW and `sh run.sh` if you're using
Cygwin.

# pathfinder app

### [task-1]

Used to find files in the given ```rootPath``` on a given ```depth``` containing a given ```mask``` (in *glob* format).
Algorithm used to traverse file tree - BFS on queues (with no recursion).
Usage: 
```gradlew run -DrootPath=<rootPath> -Ddepth=<depth> -Dmask=<mask>```
Also includes unit-tests.

### [task-2]
Changed **task-1** to work in 2 separate threads. One for searching files, another for printing. 

### [task-3]
Rewrote program to act like telnet server.  
Usage:  
```gradlew run -DrootPath=<rootPath> -Dport=<port>```  
where ```port``` is a valid port number between 1024 and 65535.  
Files are searched in **ONE** separate thread and cache is filled during that.  
Also, rootPath and all found subpaths are tracked if new file is created, or existing one is deleted.   
To connect - use ```telnet``` with options ```host port```.  
Execution example:
```
Ready for new search. (Q - to exit)
w
Wrong input. Try again.
eg.: d=3; m=*mask

Ready for new search. (Q - to exit)
d=2; m=*.txt
	/home/administrator/GoLand-2021.3.3/Install-Linux-tar.txt
	/home/administrator/Idea-IC/build.txt
	/home/administrator/Idea-IC/LICENSE.txt
	/home/administrator/Idea-IC/Install-Linux-tar.txt
	/home/administrator/GoLand-2021.3.3/build.txt
	/home/administrator/Idea-IC/NOTICE.txt
Found 6

Ready for new search. (Q - to exit)
```

[task-1]: https://github.com/DmytroTHR/pathfinder/tree/task-1
[task-2]: https://github.com/DmytroTHR/pathfinder/tree/task-2
[task-3]: https://github.com/DmytroTHR/pathfinder/tree/task-3
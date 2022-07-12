# pathfinder app

### [task-1]

Used to find files in the given ```rootPath``` on a given ```depth``` containing a given ```mask``` (in *glob* format).
Algorithm used to traverse file tree - BFS on queues (with no recursion).
Usage: 
```gradlew run -DrootPath=<rootPath> -Ddepth=<depth> -Dmask=<mask>```
Also includes unit-tests.

### [task-2]
Changed **task-1** to work in 2 separate threads. One for searching files, another for printing. 

[task-1]: https://github.com/DmytroTHR/pathfinder/tree/task-1
[task-2]: https://github.com/DmytroTHR/pathfinder/tree/task-2
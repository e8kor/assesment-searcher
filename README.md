## Searcher
Application serves single purpose of scanning files content where root directory passed as first argument, and providing interactive CLI which accepts word/words separated by space, or if passed single word *:quit* to exit from program

### Design
Project uses Hexagon architecture as its main pattern. Actual trait implementation is not used inside of app, instead preferred way to access its implementation is via companion object. Command is handy sealed structure that safely parse raw input.

### Assembly
To prepare new release sbt requires to be in path. Distributive located under
```${REPO_HOME}/target/scala-2.12/app.jar```

#### Assembly Command:
```sbt assembly```

### How To
Command to run application:
```
java -jar app.jar /path/to/search/root
```

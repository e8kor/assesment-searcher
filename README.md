## Searcher
Application serves single purpose of scanning files content

### Design
Project uses Hexagon architecture as its main pattern. Actual trait implementation is not used inside of app, instead preferred way to access its implementation is via companion object. Command is handy sealed structure that safely parse raw input. Application preforms simple tokenization, so words like ```home?```, ```home.```, ```home``` considered as same word, logic applied for both scanned files and input words.

### Assembly
To prepare new release sbt requires to be in path. **Newly** assembled distributive located under
```${REPO_HOME}/app.jar```

#### Assembly Command:
```sbt assembly```

### How To
Command to run application:
```
java -jar app.jar /path/to/search/root
```

### Usage:
Reserved words:
* ```:quit``` exiting application

Files home location must be passed as a first argument
Strings separated by whitespace considered as list of argument to search for
Single word argument must contain no spaces.

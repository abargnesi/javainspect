# JavaInspect

Visualize java class relationships as Graphviz.

Forked from the original work from [Svjatoslav Agejenko](http://www.svjatoslav.eu/).
This repository only provides minor improvements.

Original [git repository](http://www2.svjatoslav.eu/repositories/javainspect.git)

Original [documentation](http://www2.svjatoslav.eu/gitbrowse/javainspect/doc/index.html)

Minor enhancements:

- Provides java main to run as CLI tool.
- Produces single executable with dependencies.
- Default to render SVG.
- Default to output to `user.dir` (i.e. working directory).

### Building

1. Install [Maven](http://maven.apache.org/).
2. Package JAR:

    `mvn clean package`

### Running

1. You will need to add the classes would like to visualize to your classpath.
2. Provide a package glob pattern to identify the classes you want to visualize.
3. Name your output.

```bash
java \
  -cp .:/path/to/your/classes.jar:./target/javainspect-{VERSION}.jar \
  eu.svjatoslav.inspector.java.methods.Main \
  /home/user/work/myproject/src/main/java/ com.myproject.* myproject
```

Here we want to visualize _com.myproject.*_ classes found in
*/home/user/work/myproject/src/main/java/*. The resulting *dot* and *svg*
file will be prefixed with *myproject*.


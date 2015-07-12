# threadannotations
threadannotations is a light-weight Java-library for thread annotations with Java-agent to enforce them.

Annotate your classes and methods using
* ```@SwingThread```
* ```@SingleThread```
* ```@MultiThread```

found from ```taif-X.Y.jar```.

Enforce annotatios using Java-agent while testing your code
* ```-javaagent:/path/to/lib/taagent-X.Y.jar```

You can leave the annotations in your production code; without Java-agent,
they cause no performance penalty.

[Get the latest release!](../../releases/)

straightjacket
==============

Straightjacket is a proof of concept to create a safe environment for running 3rd party or end user scripts on the JVM.

Java 8's [Nashorn](http://www.oracle.com/technetwork/articles/java/jf14-nashorn-2126515.html) provides a fast, seemingly full featured JavaScript engine. However, it also exposes all Java classes/packages to the scripts. This is undesirable when you're running 3rd party scripts on your server. Straightjacket addresses this concern (at least somewhat) by providing a custom classloader and simple framework to give you control over which Java classes are accessible from the script's execution environment.

Creating a Straightjacket Engine
--------------------------------

To create a simple script engine and call the JavaScript function ```add_numbers()``` declared in add_numbers.js, do this:

```java

ScriptEngine engine = new Straightjacket().createJavaScriptEngine();

engine.eval(getScript("js/add_numbers.js"));

int result = (int) ((Invocable) engine).invokeFunction("add_numbers", 1, 3);

```

With this default configuration, Straightjacket's script engine will refuse to load any Java classes/packages, even if the script tries to use them.

Exposing Java Packages/Classes
------------------------------

Let's say your script has a need for a particular Java package, for example:

```js

function open_url_connection(u) {
    var url = new java.net.URL(u);
    var connection = url.openConnection();
    var content = connection.getContent();

    return content;
}

```

By default, Straightjacket will prevent the script from loading ```java.net.URL```. You'll need to explicitly allow access to the ```java.net``` package and any subpackages:

```java

Straightjacket straightjacket = new Straightjacket().exposeJavaClass("java.net.*");
ScriptEngine engine = straightjacket.createJavaScriptEngine();

```

Straightjacket can expose any number of Java classes or glob expressions representing Java packages.

Security
--------

This is just a simple proof of concept for now. There are almost certainly ways around Straightjacket's security model for class loading. Even if your scripters have no malicious intent, there are other potential problems with blindly running 3rd party scripts on your server: a badly behaving script could hog lots of memory or otherwise impact performance. Use caution and do your own testing if you go down this road.

package org.hodor.engine.impl.natives.java;

import org.hodor.engine.impl.Callable;
import org.hodor.engine.impl.Interpreter;
import org.hodor.engine.impl.Namespace;
import org.hodor.engine.impl.Scope;
import org.hodor.parser.HodorParser;

import java.util.HashMap;
import java.util.Map;

public class ImportFunction implements Callable {
    private final Map<String, JavaPackage> packages = new HashMap<String, JavaPackage>();

    public Namespace bindParameters(Interpreter interpreter, HodorParser.CallContext callSite, Namespace parent, Parameters parameters) {
        Namespace namespace = parent.enter(null);
        Object first = parameters.values().iterator().next();
        namespace.define("name", true).set(first);
        return namespace;
    }

    public Object call(Interpreter interpreter, HodorParser.CallContext callSite, Scope scope) {
        String name = (String) scope.get("name").get();
        return getPackage(name);
    }

    public JavaPackage getPackage(String name) {
        JavaPackage result = packages.get(name);
        if (result == null) {
            result = new JavaPackage(this, name);
            packages.put(name, result);
        }
        return result;
    }
}

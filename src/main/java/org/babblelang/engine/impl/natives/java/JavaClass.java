package org.babblelang.engine.impl.natives.java;

import org.babblelang.engine.impl.*;
import org.babblelang.parser.BabbleParser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

class JavaClass implements Resolver, Callable {
    private final Class _class;
    private final Map<String, Slot> members = new HashMap<String, Slot>();

    JavaClass(Class _class) {
        this._class = _class;
    }

    public Scope bindParameters(Interpreter interpreter, BabbleParser.CallContext callSite, Scope parent, Parameters parameters) {
        Scope scope = parent.enter(null);
        try {
            Constructor constructor = _class.getConstructor(parameters.typesArray());
            scope.define("constructor", true).set(constructor);
            scope.define("parameters", true).set(parameters);
            return scope;
        } catch (NoSuchMethodException nsme) {
            throw new IllegalArgumentException(nsme);
        }
    }

    public Object call(Interpreter interpreter, BabbleParser.CallContext callSite, Resolver resolver) {
        Constructor constructor = (Constructor) resolver.get("constructor").get();
        Parameters parameters = (Parameters) resolver.get("parameters").get();
        try {
            return new JavaObject(this, constructor.newInstance(parameters.valuesArray()));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Slot define(String key, boolean _final) {
        throw new IllegalStateException("Cannot define anything in a Java class");
    }

    public boolean isDeclared(String key) {
        return get(key) != null;
    }

    public Slot get(String key) {
        Slot result = members.get(key);

        if (result == null) {
            result = new Slot(key, true);
            members.put(key, result);

            for (Class _class2 : _class.getClasses()) {
                if (Modifier.isPublic(_class2.getModifiers())) {
                    result.set(new JavaClass(_class2));
                    break;
                }
            }

            if (!result.isSet()) {
                for (Method method : _class.getMethods()) {
                    if (Modifier.isPublic(method.getModifiers())) {
                        result.set(new JavaMethod(_class, key));
                        break;
                    }
                }
            }

            if (!result.isSet()) {
                throw new IllegalArgumentException("No such key in " + _class.getCanonicalName() + " : " + key);
            }
        }

        return result;
    }
}

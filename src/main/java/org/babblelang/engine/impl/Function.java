package org.babblelang.engine.impl;

import org.babblelang.parser.BabbleBaseVisitor;
import org.babblelang.parser.BabbleParser;

public class Function extends BabbleBaseVisitor<Object> implements Callable {
    private final BabbleParser.FunctionLiteralContext definition;
    private final Scope closure;

    public Function(BabbleParser.FunctionLiteralContext definition, Scope scope) {
        this.definition = definition;
        this.closure = scope;
    }

    public Scope bindParameters(Interpreter interpreter, Scope parent, Parameters parameters) {
        Scope scope = closure.enter(null);
        scope.define("$recurse", this);

        int count = 0;
        for (BabbleParser.ParameterDeclarationContext parameter : definition.functionType().parametersDeclaration().parameterDeclaration()) {
            String pos = "$" + (count++);
            String name = parameter.ID().getText();
            Object value;
            if (parameters.containsKey(name)) {
                value = parameters.get(name);
            } else if (parameters.containsKey(pos)) {
                value = parameters.get(pos);
            } else if (parameter.defaultValue != null) {
                value = interpreter.visit(parameter.defaultValue);
            } else {
                throw new IllegalArgumentException("Missing parameter : " + name);
            }
            checkType(parameter, value);
            scope.define(name, value);
        }
        return scope;
    }

    public Object call(Interpreter interpreter, Scope scope) {
        return interpreter.visit(definition.functionBlock);
    }

    private void checkType(BabbleParser.ParameterDeclarationContext parameter, Object value) {
        if (value instanceof Scope) {
            throw new IllegalArgumentException("Parameter type mismatch : " + parameter.ID().getText() + " is expected to be of type " + parameter.type().getText() + ", got " + value.getClass().getCanonicalName());
        }
    }
}

package org.babblelang.engine;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import java.util.Collections;
import java.util.List;

public class BabbleScriptEngineFactory implements ScriptEngineFactory {
    public static final BabbleScriptEngineFactory INSTANCE = new BabbleScriptEngineFactory();

    @Override
    public String getEngineName() {
        return "Babble";
    }

    @Override
    public String getEngineVersion() {
        return "0.1";
    }

    @Override
    public List<String> getExtensions() {
        return Collections.singletonList("ba");
    }

    @Override
    public List<String> getMimeTypes() {
        return Collections.singletonList("application/x-babble");
    }

    @Override
    public List<String> getNames() {
        return Collections.singletonList("babble");
    }

    @Override
    public String getLanguageName() {
        return "Babble";
    }

    @Override
    public String getLanguageVersion() {
        return "0.1";
    }

    @Override
    public Object getParameter(String key) {
        return null;
    }

    @Override
    public String getMethodCallSyntax(String obj, String m, String... args) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(obj);
        buffer.append('.');
        buffer.append(m);
        buffer.append('(');
        for (int i = 0, l = args.length; i < l; i++) {
            if (i > 0) {
                buffer.append(',');
            }
            buffer.append(args[i]);
        }
        buffer.append(')');
        return buffer.toString();
    }

    @Override
    public String getOutputStatement(String toDisplay) {
        return "print(" + toDisplay + ")";
    }

    @Override
    public String getProgram(String... statements) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0, l = statements.length; i < l; i++) {
            if (i > 0) {
                buffer.append('\n');
            }
            buffer.append(statements[i]);
        }
        return buffer.toString();
    }

    @Override
    public ScriptEngine getScriptEngine() {
        return new BabbleScriptEngine(this);
    }
}

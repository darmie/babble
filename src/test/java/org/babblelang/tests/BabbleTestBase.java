package org.babblelang.tests;

import junit.framework.TestCase;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.babblelang.engine.BabbleScriptEngineFactory;
import org.babblelang.parser.BabbleLexer;
import org.babblelang.parser.BabbleParser;

import java.io.FileReader;
import java.io.IOException;

public abstract class BabbleTestBase extends TestCase {
    protected BabbleParser.FileContext parse(String path) throws IOException {
        CharStream input = new ANTLRFileStream(path);
        BabbleLexer lexer = new BabbleLexer(input);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        BabbleParser parser = new BabbleParser(tokenStream);
        parser.setErrorHandler(new BailErrorStrategy());
        return parser.file();
    }

    protected Object interpret(String path) throws Exception {
        return BabbleScriptEngineFactory.INSTANCE.getScriptEngine().eval(new FileReader(path));
    }
}

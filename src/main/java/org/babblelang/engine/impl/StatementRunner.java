package org.babblelang.engine.impl;

import org.babblelang.parser.BabbleBaseVisitor;
import org.babblelang.parser.BabbleLexer;
import org.babblelang.parser.BabbleParser;

import java.util.List;

public class StatementRunner extends BabbleBaseVisitor<Object> {
    private final Scope scope;

    public StatementRunner(Scope scope) {
        this.scope = scope;
    }

    public Object run(List<BabbleParser.StatementContext> statements) {
        Object result = null;
        for (BabbleParser.StatementContext statement : statements) {
            result = run(statement);
        }
        return result;
    }

    public Object run(BabbleParser.StatementContext statement) {
        return visit(statement);
    }

    @Override
    public Object visitDefStatement(BabbleParser.DefStatementContext ctx) {
        String id = ctx.ID().getText();
        Object value = visit(ctx.expression());
        scope.put(id, value);
        return value;
    }

    @Override
    public Object visitParen(BabbleParser.ParenContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Object visitBlock(BabbleParser.BlockContext ctx) {
        return new StatementRunner(new Scope(scope)).run(ctx.statement());
    }

    @Override
    public Object visitSelector(BabbleParser.SelectorContext ctx) {
        Scope base = scope;
        if (ctx.selector() != null) {
            base = (Scope) visit(ctx.selector());
        }
        String name = ctx.ID().getText();
        if (base.containsKey(name)) {
            return base.get(name);
        } else {
            throw new IllegalArgumentException("ID not found : " + name);
        }
    }

    @Override
    public Object visitBinaryOp(BabbleParser.BinaryOpContext ctx) {
        Object a = visit(ctx.expression(0));
        Object b = visit(ctx.expression(1));

        switch (ctx.op.getType()) {
            case BabbleLexer.MUL:
                return (Double) a * (Double) b;

            case BabbleLexer.DIV:
                return (Double) a / (Double) b;

            case BabbleLexer.PLUS:
                if (a instanceof String) {
                    return (String) a + b;
                } else {
                    return (Double) a + (Double) b;
                }

            case BabbleLexer.MINUS:
                return (Double) a - (Double) b;

            case BabbleLexer.LT:
                return ((Comparable) a).compareTo(b) < 0;

            case BabbleLexer.LTE:
                return ((Comparable) a).compareTo(b) <= 0;

            case BabbleLexer.EQ:
                return ((Comparable) a).compareTo(b) == 0;

            case BabbleLexer.GTE:
                return ((Comparable) a).compareTo(b) >= 0;

            case BabbleLexer.GT:
                return ((Comparable) a).compareTo(b) > 0;

            default:
                throw new UnsupportedOperationException("Bad op : " + ctx.op.getText());
        }
    }

    @Override
    public Object visitNumber(BabbleParser.NumberContext ctx) {
        return Double.parseDouble(ctx.getText());
    }

    @Override
    public Object visitString(BabbleParser.StringContext ctx) {
        String literal = ctx.STRING().getText();
        return literal.substring(1, literal.length() - 1);
    }
}

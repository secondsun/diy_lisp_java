package net.saga.diy.lisp.parser.operation.math;

import net.saga.diy.lisp.parser.AST;
import net.saga.diy.lisp.parser.Evaluator;
import net.saga.diy.lisp.parser.operation.Operation;
import net.saga.diy.lisp.parser.types.LispException;

public class MathOperation implements Operation<Operation> {

    private final Operand op;

    public MathOperation(AST.Token token) {
        op = Operand.fromSymbol((String) token.value);
    }

    @Override
    public Operation operate(final AST.Token token) {

        final int value = verifyAndEvaluate(token);
        
        switch (op) {
            case ADD:
                return (nextToken -> (value + verifyAndEvaluate(nextToken)));

            case SUB:
                return ((nextToken) -> {
                    return value - verifyAndEvaluate(nextToken);
                });
            case DIV:
                return ((nextToken) -> {
                    return value / verifyAndEvaluate(nextToken);
                });
            case MOD:
                return ((nextToken) -> {
                    return value % verifyAndEvaluate(nextToken);
                });
            case GT:
                return ((nextToken) -> {
                    return value > verifyAndEvaluate(nextToken);
                });
            case LT:
                return ((nextToken) -> {
                    return value < verifyAndEvaluate(nextToken);
                });
            case MULT:
                return ((nextToken) -> {
                    return value * verifyAndEvaluate(nextToken);
                });
            default:
                throw new AssertionError(op.name());
        }
    };

    private Integer verifyAndEvaluate(AST.Token token) {

        if (token.type == Integer.class) {
            return (Integer) token.value;
        }

        if (token.tree != null) {
            Object result = Evaluator.evaluate(token.tree, null);
            if (result instanceof Integer) {
                return (Integer) result;
            }
        }

        throw new LispException("Math operations expect a Integer");

    }

}

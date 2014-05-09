package net.saga.diy.lisp.parser.operation.math;

import net.saga.diy.lisp.parser.AST;
import net.saga.diy.lisp.parser.Evaluator;
import net.saga.diy.lisp.parser.operation.Operation;
import net.saga.diy.lisp.parser.types.Environment;
import net.saga.diy.lisp.parser.types.LispException;

public class MathOperation implements Operation<Operation> {

    private final Operand op;

    public MathOperation(AST.Token token) {
        op = Operand.fromSymbol((String) token.value);
    }

    @Override
    public Operation operate(final AST.Token token, Environment env) {

        final int value = verifyAndEvaluate(token, env);
        
        switch (op) {
            case ADD:
                return ((nextToken, env2) -> (value + verifyAndEvaluate(nextToken, env2)));

            case SUB:
                return ((nextToken, env2) -> {
                    return value - verifyAndEvaluate(nextToken, env2);
                });
            case DIV:
                return ((nextToken, env2) -> {
                    return value / verifyAndEvaluate(nextToken, env2);
                });
            case MOD:
                return ((nextToken, env2) -> {
                    return value % verifyAndEvaluate(nextToken, env2);
                });
            case GT:
                return ((nextToken, env2) -> {
                    return value > verifyAndEvaluate(nextToken, env2);
                });
            case LT:
                return ((nextToken, env2) -> {
                    return value < verifyAndEvaluate(nextToken, env2);
                });
            case MULT:
                return ((nextToken, env2) -> {
                    return value * verifyAndEvaluate(nextToken, env2);
                });
            default:
                throw new AssertionError(op.name());
        }
    };

    private Integer verifyAndEvaluate(AST.Token token, Environment env) {

        if (token.type == Integer.class) {
            return (Integer) token.value;
        }

        if (token.tree != null) {
            Object result = Evaluator.evaluate(token.tree, env);
            if (result instanceof Integer) {
                return (Integer) result;
            }
        }

        throw new LispException("Math operations expect a Integer");

    }

}

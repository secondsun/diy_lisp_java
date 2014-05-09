package net.saga.diy.lisp.parser.operation.math;

import net.saga.diy.lisp.parser.AST;
import net.saga.diy.lisp.parser.operation.Operation;

public class MathOperation implements Operation<Operation> {

    private final Operand op;
    
    public MathOperation(AST.Token token) {
        op = Operand.fromSymbol((String) token.value);
    }

    
    
    @Override
    public Operation operate(final AST.Token token) {
        final int value = (int) token.value;
        switch(op) {    
            case ADD:
                return ((nextToken) -> {return value + (int) nextToken.value;});
            case SUB:
                return ((nextToken) -> {return value - (int) nextToken.value;});
            case DIV:
                return ((nextToken) -> {return value / (int) nextToken.value;});
            case MOD:
                return ((nextToken) -> {return value % (int) nextToken.value;});
            case GT:
                return ((nextToken) -> {return value > (int) nextToken.value;});
            case LT:
                return ((nextToken) -> {return value < (int) nextToken.value;});
            case MULT:
                return ((nextToken) -> {return value * (int) nextToken.value;});
            default:
                throw new AssertionError(op.name());
        }
    };
    
}

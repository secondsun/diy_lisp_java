/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.saga.diy.lisp.parser.operation;

import net.saga.diy.lisp.parser.AST;
import net.saga.diy.lisp.parser.Evaluator;

public class IfOperation implements Operation<Object>{

    private final Operation evaluateForFalse = (token1) -> {return (Operation)(token2) -> Evaluator.evaluate(new AST(token2), null);};
    private final Operation evaluateForTrue = (token1) -> {return (Operation)(token2) -> Evaluator.evaluate(new AST(token1), null);};
    
    @Override
    public Object operate(AST.Token token) {
        if ((boolean)Evaluator.evaluate(new AST(token), null)) {
            return evaluateForTrue;
        } else {
            return evaluateForFalse;
        }
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.saga.diy.lisp.parser.operation;

import net.saga.diy.lisp.parser.AST;
import net.saga.diy.lisp.parser.Evaluator;
import net.saga.diy.lisp.parser.types.Environment;

public class IfOperation implements Operation<Object>{

    private final Operation evaluateForFalse = (token1,env1) -> {return (Operation)(token2,env2) -> Evaluator.evaluate(new AST(token2), env2);};
    private final Operation evaluateForTrue = (token1,env1) -> {return (Operation)(token2,env2) -> Evaluator.evaluate(new AST(token1), env1);};
    
    @Override
    public Object operate(AST.Token token, Environment env) {
        if ((boolean)Evaluator.evaluate(new AST(token), env)) {
            return evaluateForTrue;
        } else {
            return evaluateForFalse;
        }
    }
    
}

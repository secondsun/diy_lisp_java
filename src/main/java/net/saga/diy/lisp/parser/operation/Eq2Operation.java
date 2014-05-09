/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.saga.diy.lisp.parser.operation;

import net.saga.diy.lisp.parser.AST;
import net.saga.diy.lisp.parser.Evaluator;
import net.saga.diy.lisp.parser.types.Environment;

/**
 *
 * @author summers
 */
public class Eq2Operation implements Operation<Boolean> {
    private final AST.Token firstToken;

    public Eq2Operation(AST.Token firstArgument) {
        this.firstToken = firstArgument;
    }

    @Override
    public Boolean operate(AST.Token secondToken, Environment env) {
        AtomOperation isAtom = new AtomOperation();
        if (isAtom.operate(firstToken, env) && isAtom.operate(secondToken, env)) {
            Object firstValue;
            Object secondValue;
            if (firstToken.tree == null) {
                firstValue = (Evaluator.evaluate(new AST(firstToken), env));
            } else {
                firstValue = (Evaluator.evaluate(firstToken.tree, env));
            }
            if (secondToken.tree == null) {
                secondValue = (Evaluator.evaluate(new AST(secondToken), env));
            } else {
                secondValue = (Evaluator.evaluate(secondToken.tree, env));
            }
            return firstValue.equals(secondValue);
        }
        return false;
    }
    
}

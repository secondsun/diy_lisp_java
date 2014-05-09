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
    public Boolean operate(AST.Token secondToken) {
        AtomOperation isAtom = new AtomOperation();
        if (isAtom.operate(firstToken) && isAtom.operate(secondToken)) {
            Object firstValue;
            Object secondValue;
            if (firstToken.tree == null) {
                firstValue = (Evaluator.evaluate(new AST(firstToken), new Environment()));
            } else {
                firstValue = (Evaluator.evaluate(firstToken.tree, new Environment()));
            }
            if (secondToken.tree == null) {
                secondValue = (Evaluator.evaluate(new AST(secondToken), new Environment()));
            } else {
                secondValue = (Evaluator.evaluate(secondToken.tree, new Environment()));
            }
            return firstValue.equals(secondValue);
        }
        return false;
    }
    
}

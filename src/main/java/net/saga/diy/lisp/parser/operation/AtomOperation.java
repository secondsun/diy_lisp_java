/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.saga.diy.lisp.parser.operation;

import net.saga.diy.lisp.parser.AST;
import net.saga.diy.lisp.parser.Evaluator;
import net.saga.diy.lisp.parser.operation.Operation;
import net.saga.diy.lisp.parser.types.Environment;

/**
 *
 * @author summers
 */
public class AtomOperation implements Operation<Boolean> {

    public AtomOperation() {
    }

    @Override
    public Boolean operate(AST.Token token) {
        if (token.tree == null) {
            return !(Evaluator.evaluate(new AST(token), new Environment()).getClass().isArray());
        }
        return !(Evaluator.evaluate(token.tree, new Environment()).getClass().isArray());
    }
    
}

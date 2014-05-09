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
public class QuoteOperation implements Operation<Object> {

    @Override
    public Object operate(AST.Token token, Environment env) {
        if (token.type == String.class) {
            return (String) token.value;
        } else {
            return Evaluator.evaluate(token.tree, env);
        }
    }
    
}

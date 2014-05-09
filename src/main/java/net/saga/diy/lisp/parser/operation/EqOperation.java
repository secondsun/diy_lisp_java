/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.saga.diy.lisp.parser.operation;

import net.saga.diy.lisp.parser.AST;
import net.saga.diy.lisp.parser.types.Environment;

/**
 *
 * @author summers
 */
public class EqOperation implements Operation<Eq2Operation> {

    public EqOperation() {
    }

    @Override
    public Eq2Operation operate(AST.Token token, Environment env) {
        return new Eq2Operation(token);
    }
    
}

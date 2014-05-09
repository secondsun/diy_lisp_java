/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.saga.diy.lisp.parser.operation;

import net.saga.diy.lisp.parser.AST;
import net.saga.diy.lisp.parser.Evaluator;
import net.saga.diy.lisp.parser.types.Environment;
import net.saga.diy.lisp.parser.types.LispException;

public class DefineOperation implements Operation<Operation>{

    
    @Override
    public Operation<Void> operate(AST.Token name, Environment env) {
        
        if (name.type != String.class || !(name.value instanceof String)) {
            throw new LispException("Illegal define name token");
        }
        
        return ((value, env2) ->{
            env2.set((String) name.value, Evaluator.evaluate(new AST(value), env2));
            return null;
        });
    }
    
}

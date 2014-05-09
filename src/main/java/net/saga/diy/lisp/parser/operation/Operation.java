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
public interface Operation<T> {

    public T operate(AST.Token token, Environment env);
    
}

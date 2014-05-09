/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.saga.diy.lisp.parser.types;

/**
 *
 * @author summers
 */
public class Environment {

    private final Object[] variables;

    public Environment() {
        this(null);
    }

    public Environment(Object[] variables) {
        this.variables = variables;
    }
}

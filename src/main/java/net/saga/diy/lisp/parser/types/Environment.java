/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.saga.diy.lisp.parser.types;

import java.util.HashMap;
import static net.saga.diy.lisp.parser.types.Utils.getOrThrow;

/**
 *
 * @author summers
 */
public class Environment {

    private final HashMap<String, Object> variables;

    public Environment() {
        this(null);
    }

    public Environment(HashMap variables) {
        if (variables == null) {
            this.variables = new HashMap<>();
        } else {
            this.variables = new HashMap<>(variables);
        }
    }
    
    public Object lookup(String varName) {
        return getOrThrow(variables,varName);
    }
    
    public Environment extend(String name, Object value) {
        HashMap<String, Object> newVars = new HashMap<>(variables);
        newVars.put(name, value);
        return new Environment(newVars);
    }

    public void set(String name, Object value) {
        if (variables.containsKey(name)) {
            throw new LispException("Variable " + name + " is already defined");
        }
        variables.put(name, value);
    }

    
}

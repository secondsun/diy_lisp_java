/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.saga.diy.lisp.parser.operation.math;

import java.util.HashMap;
import java.util.Map;
import net.saga.diy.lisp.parser.types.LispException;

/**
 *
 * @author summers
 */
public enum Operand {

    ADD, SUB, DIV, MULT, MOD, GT, LT;

    private static final Map<String, Operand> symbolMap = new HashMap<String, Operand>(6);

    static {
        symbolMap.put("+", ADD);
        symbolMap.put("-", SUB);
        symbolMap.put("/", DIV);
        symbolMap.put("*", MULT);
        symbolMap.put("mod", MOD);
        symbolMap.put(">", GT);
        symbolMap.put("<", LT);
    }
    
    public static Operand fromSymbol(String symbol) {
        if (!symbolMap.containsKey(symbol)) {
            throw new LispException("Illegal symbol " + symbol);
        }
        return symbolMap.get(symbol);

    }

}

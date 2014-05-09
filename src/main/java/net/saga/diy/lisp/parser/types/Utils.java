/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.saga.diy.lisp.parser.types;

import java.util.Map;

/**
 *
 * @author summers
 */
public class Utils {

    public static <K, V> V getOrThrow(Map<K, V> map, K key) {
        
        V value = map.get(key);
        
        if (value == null) {
            throw new LispException("No value " + key);
        }
        
        return value;
    }

}

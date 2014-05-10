/**
 * Copyright Summers Pittman, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

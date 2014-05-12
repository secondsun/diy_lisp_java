/*
 * Copyright 2014 summers.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.saga.diy.lisp.parser.operation;

import net.saga.diy.lisp.parser.AST;
import net.saga.diy.lisp.parser.Evaluator;
import net.saga.diy.lisp.parser.types.Environment;
import net.saga.diy.lisp.parser.types.LispException;

public class EmptyOperation implements Operation<Boolean>{

    @Override
    public Boolean operate(AST.Token listToken, Environment firstEnv) {
        if (listToken.tree == null) {
            throw new LispException(listToken + " is not a list");
        }
        Object res = Evaluator.evaluate(listToken.tree, firstEnv);
        if (res instanceof AST) {
            return ((AST)res).tokens.isEmpty();
        } else if (res instanceof Object[]) {
            return ((Object[])res).length == 0;
        } 
        
        throw new LispException(listToken + " is not a list");
    }
    
}

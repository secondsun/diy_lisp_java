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

import java.util.ArrayList;
import java.util.List;
import net.saga.diy.lisp.parser.AST;
import net.saga.diy.lisp.parser.AST.Token;
import net.saga.diy.lisp.parser.Evaluator;
import net.saga.diy.lisp.parser.types.Closure;
import net.saga.diy.lisp.parser.types.Environment;

public class ClosureOperation implements Operation<Object>{

    private final Closure closure;
    private final List<AST.Token> tokens = new ArrayList<AST.Token>();
    
    public ClosureOperation(Closure closure) {
        this.closure = closure;
    }
    
    @Override
    public Object operate(AST.Token token, Environment env) {
        tokens.add(token);
        if (tokens.size() == closure.getParams().size()) {
            Environment closureEnvironment = closure.getEnv();
            for (int i = 0; i < tokens.size(); i++) {
                String varname = closure.getParams().get(i);
                Token variableToken = tokens.get(i);
                Object tokenValue = Evaluator.evaluate(new AST(variableToken), env); 
                closureEnvironment = closureEnvironment.extend(varname, tokenValue);
                
            }
            
            return Evaluator.evaluate(closure.getBody(), closureEnvironment);
            
        } else {
            return this;
        }
        
    }

    
    
}

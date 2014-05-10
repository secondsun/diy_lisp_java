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

package net.saga.diy.lisp.parser.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.saga.diy.lisp.parser.AST;
import net.saga.diy.lisp.parser.AST.Token;

public class Closure {

    private final List<String> params = new ArrayList<String>();
    private final Environment env;
    private final AST.Token body;
    
    public Closure(Environment env, Token params, Token body) {
        this.env = env;
        
        if (params.tree == null) {
            throw new LispException("params not a list");
        }
        
        params.tree.tokens.forEach((varToken)->this.params.add((String) varToken.value));
        
        this.body = body;
    }
    
    public Environment getEnv() {
        return env;
    }

    public List<String> getParams() {
        return new ArrayList<>(params);
    }

    public Token getBody() {
        return body;
    }
    
    
    
}

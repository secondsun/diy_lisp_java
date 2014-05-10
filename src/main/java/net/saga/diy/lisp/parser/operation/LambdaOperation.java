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
import net.saga.diy.lisp.parser.types.Closure;
import net.saga.diy.lisp.parser.types.Environment;
import net.saga.diy.lisp.parser.types.LispException;

/**
 *
 * @author summers
 */
public class LambdaOperation implements Operation<Operation<Closure>>{

    @Override
    public Operation<Closure> operate(AST.Token token, Environment env) {
        extractParams(token);
        return ((bodyToken, bodyEnv) -> {return new Closure(bodyEnv, token, bodyToken);});
    }

    private List<String> extractParams(AST.Token token) {
        ArrayList<String> params = new ArrayList<>();
        if (token.tree != null) {
            token.tree.iterator().forEachRemaining(varToken->{
                assert varToken.type == String.class;
                params.add((String)varToken.value);
            });
        } else {
               throw new LispException("Lambda vars not list");
        }
        return params;
    }
    
}

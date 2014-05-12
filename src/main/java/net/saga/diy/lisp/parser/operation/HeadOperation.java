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
package net.saga.diy.lisp.parser.operation;

import net.saga.diy.lisp.parser.AST;
import net.saga.diy.lisp.parser.AST.Token;
import static net.saga.diy.lisp.parser.Evaluator.evaluate;
import net.saga.diy.lisp.parser.types.Environment;
import net.saga.diy.lisp.parser.types.LispException;

public class HeadOperation implements Operation<Object> {

    @Override
    public Object operate(AST.Token listToken, Environment env) {
        if (listToken.tree == null) {
            throw new LispException(listToken + " is not a list");
        } else if (listToken.tree.tokens.isEmpty() ) {
            throw new LispException(listToken + " is empty");
        }
        
        Object result = evaluate(listToken.tree, env);
        
        if (result instanceof AST) {
            AST ast = (AST) result;
            if (ast.tokens.isEmpty() ) {
                throw new LispException(listToken + " is empty");
            }
            return evaluate(ast.tokens.get(0), env);
        } else if (result.getClass().isArray()) {
            return ((Object[])result)[0];
        }
        
        throw new LispException(listToken + " is not a list");
    }
}

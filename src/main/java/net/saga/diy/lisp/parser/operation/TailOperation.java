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

import java.util.Arrays;
import static net.saga.diy.lisp.parser.Evaluator.evaluate;
import net.saga.diy.lisp.parser.types.Environment;
import net.saga.diy.lisp.parser.types.LispException;

/**
 * 
 * @author summers
 */
public class TailOperation implements Operation<Object[]> {

    @Override
    public Object[] operate(Object listToken, Environment env) {
        
        if (!listToken.getClass().isArray()) {
            throw new LispException(listToken + " is not a list");
        }
        
        Object[] listArr = (Object[]) listToken;
        
        if (listArr.length == 0) {
            throw new LispException(listToken + " is empty");
        }
        
        Object result = evaluate(listArr, env);
        
        if (result.getClass().isArray()) {
            Object[] ast = (Object[]) result;
            if (ast.length == 0 ) {
                throw new LispException(ast + " is empty");
            }
            return Arrays.copyOfRange(ast, 1, ast.length);
        } 
        throw new LispException(result + " is not a list");
    }
}

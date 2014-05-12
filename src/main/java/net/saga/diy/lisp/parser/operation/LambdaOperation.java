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

import java.util.ArrayList;
import java.util.List;
import net.saga.diy.lisp.parser.types.Closure;
import net.saga.diy.lisp.parser.types.Environment;
import net.saga.diy.lisp.parser.types.LispException;
import static net.saga.diy.lisp.parser.types.Utils.isList;

/**
 * 
 * @author summers
 */
public class LambdaOperation implements Operation<Operation<Closure>> {

    @Override
    public Operation<Closure> operate(Object token, Environment env) {
        extractParams(token);
        return ((bodyToken, bodyEnv) -> {return new Closure(bodyEnv, (Object[]) token, bodyToken);});
    }

    private List<String> extractParams(Object token) {
        ArrayList<String> params = new ArrayList<>();
        if (isList(token)) {
            for (Object varToken : (Object[])token) {
                assert varToken instanceof String;
                params.add((String)varToken);
            }
        } else {
               throw new LispException("Lambda vars not list");
        }
        return params;
    }
}

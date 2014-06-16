/**
 * Copyright Summers Pittman, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * This project is based on, borrows heavily from, and copies the documentation
 * of https://github.com/kvalle/diy-lisp/
 */
package net.saga.diy.lisp.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static net.saga.diy.lisp.types.Utils.isList;

public class CompiledClosure {

    private final List<String> params;
    private final CompilerContext lambdaContext;

    public CompiledClosure(CompilerContext lambdaContext, Object[] params) {
        this.lambdaContext = lambdaContext;

        this.params = extractParams(params);
    }

    public CompilerContext getContext() {
        return lambdaContext;
    }

    public Object[] getParams() {
        return new ArrayList<>(params).toArray();
    }

    private List<String> extractParams(Object token) {
        ArrayList<String> params = new ArrayList<>();
        if (isList(token)) {
            for (Object varToken : (Object[]) token) {
                assert varToken instanceof String;
                params.add((String) varToken);
            }
        } else {
            throw new LispException("Lambda vars not list");
        }
        return params;
    }
    
}

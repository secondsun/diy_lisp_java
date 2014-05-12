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
package net.saga.diy.lisp.parser.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Closure {

    private final List<Object> params;
    private final Environment env;
    private final Object body;

    public Closure(Environment env, Object[] params, Object body) {
        this.env = env;

        this.params = Arrays.asList(params);
        
        
        this.body = body;
        
    }

    public Environment getEnv() {
        return env;
    }

    public Object[] getParams() {
        return new ArrayList<>(params).toArray();
    }

    public Object getBody() {
        return body;
    }

}

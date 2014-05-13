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
 *
 * This project is based on, borrows heavily from, and copies the documentation of
 * https://github.com/kvalle/diy-lisp/
 */
package net.saga.diy.lisp.parser.types;

import java.util.HashMap;

/**
 * 
 * @author summers
 */
public class Environment {

    private final HashMap<String, Object> variables;

    public Environment() {
        throw new IllegalStateException("not implemented");
    }

    public Environment(HashMap variables) {
        throw new IllegalStateException("not implemented");
    }

    public Object lookup(String varName) {
        throw new IllegalStateException("not implemented");
    }

    public Environment extend(String name, Object value) {
        throw new IllegalStateException("not implemented");
    }

    public void set(String name, Object value) {
        throw new IllegalStateException("not implemented");
    }

}

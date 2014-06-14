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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.qmx.jitescript.JiteClass;
import me.qmx.jitescript.internal.org.objectweb.asm.Opcodes;
import me.qmx.jitescript.util.CodegenUtils;

/**
 *
 * @author summers
 */
public class CompilerContext {

    public final JiteClass jiteClass;
    private int childClassCount = 0;
    private final Map<String, Object> variables = new HashMap<>();
    private final Set<String> methods = new HashSet<>();
    private CompilerContext parentContext = null;
    
    public CompilerContext() {
        this("anonymous");
    }

    public CompilerContext(String applicationName) {
        jiteClass = new JiteClass(applicationName) {
            {
                defineDefaultConstructor();
            }
        };
    }

    public CompilerContext defineVariable(String variableName, Object value) throws LispException {
        if (variables.containsKey(variableName)) {
            throw new LispException(variableName + " already defined");
        } else {
            variables.put(variableName, value);
        }

        jiteClass.defineField(variableName, Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL, CodegenUtils.ci(Object.class), value);
        
        return this;

    }

    public Object lookup(String var) throws LispException {
        Object res = variables.get(var);
        if (res == null) {
            if (parentContext == null) {
                throw new LispException(var + " is not defined");
            }
            return parentContext.lookup(var);
        }
        return res;
    }

    public CompilerContext extend(String variableName, Object value) {
        childClassCount++;
        String childClassName = jiteClass.getClassName() + "_" + childClassCount;
        CompilerContext childContext = new CompilerContext(childClassName);
        childContext.parentContext = this;
        childContext.defineVariable(variableName, value);
        return childContext;
    }
    
    public String getClassName() {
        return jiteClass.getClassName();
    }
    
}
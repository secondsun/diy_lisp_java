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
import me.qmx.jitescript.CodeBlock;
import static me.qmx.jitescript.CodeBlock.newCodeBlock;
import me.qmx.jitescript.JiteClass;
import me.qmx.jitescript.internal.org.objectweb.asm.Opcodes;
import me.qmx.jitescript.util.CodegenUtils;
import static me.qmx.jitescript.util.CodegenUtils.p;
import static me.qmx.jitescript.util.CodegenUtils.sig;

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
    private CodeBlock currentBlock = newCodeBlock();

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

    public CompilerContext(String applicationName, JiteClass parentClass) {
        jiteClass = new JiteClass(applicationName) {
            {

                defineField("context", ACC_PRIVATE | ACC_FINAL, CodegenUtils.ci(Object.class), null);

                defineMethod("<init>", ACC_PUBLIC, CodegenUtils.sig(void.class, Object.class),
                        newCodeBlock()
                        .aload(0)
                        .aload(1)
                        .putfield(applicationName, "context", CodegenUtils.ci(Object.class))
                        .aload(0)
                        .invokespecial(p((Class) Object.class), "<init>", sig(void.class))
                        .voidreturn()
                );
            }
        };
    }

    public CompilerContext defineVariable(String variableName, Object value) throws LispException {
        if (variables.containsKey(variableName)) {
            throw new LispException(variableName + " already defined");
        } else {
            variables.put(variableName, value);
        }

        if (!(value instanceof CompiledClosure)) {
            jiteClass.defineField(variableName, Opcodes.ACC_PUBLIC, CodegenUtils.ci(Object.class), value);
        }
        
        return this;

    }

    public Object lookup(String var) throws LispException {
        Object res = variables.get(var);
        if (res == null && !variables.containsKey(var)) {
            if (parentContext == null) {
                throw new LispException(var + " is not defined");
            }
            return parentContext.lookup(var);
        }
        return res;
    }

    /**
     *
     * @param var the variable to generate a lookup for
     * @return the depth of the lookups for applying context objects
     * @throws LispException
     */
    public int getFieldDepth(String var) throws LispException {
        int depth = 0;
        Object res = lookup(var); //Confirm the variable exists
        
        if (!variables.containsKey(var)) {
            depth = 1;
            depth += parentContext.getFieldDepth(var);
        } 

        return depth;
        
    }

    public CompilerContext extend(String variableName, Object value) {
        childClassCount++;
        String childClassName = jiteClass.getClassName() + "_" + childClassCount;
        CompilerContext childContext = new CompilerContext(childClassName);
        childContext.parentContext = this;
        childContext.defineVariable(variableName, value);
        jiteClass.addChildClass(childClassName, childContext.jiteClass);

        return childContext;
    }

    public String getClassName() {
        return jiteClass.getClassName();
    }

    public CompilerContext blockToMethod(String methodName) {
        if (methods.contains(methodName)) {
            throw new LispException(methodName + " already defined");
        }
        jiteClass.defineMethod(methodName, Opcodes.ACC_PUBLIC, CodegenUtils.sig(Object.class), currentBlock);
        methods.add(methodName);
        currentBlock = newCodeBlock();
        return this;
    }

    public CodeBlock currentBlock() {
        return this.currentBlock;
    }

    public Set<String> getMethods() {
        return new HashSet<>(methods);//defensive copy
    }

    public CompilerContext extend() {
        childClassCount++;
        String childClassName = jiteClass.getClassName() + "$" + childClassCount;
        CompilerContext childContext = new CompilerContext(childClassName, jiteClass);
        childContext.parentContext = this;
        jiteClass.addChildClass(childContext.jiteClass);
        return childContext;
    }

    public CompilerContext getParentContext() {
        return parentContext;
    }

    public String getClassSig() {
        return "L" + getClassName() + ";";
    }
    
}

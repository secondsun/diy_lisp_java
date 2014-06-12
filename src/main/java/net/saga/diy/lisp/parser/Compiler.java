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
package net.saga.diy.lisp.parser;

import static me.qmx.jitescript.CodeBlock.newCodeBlock;
import me.qmx.jitescript.JiteClass;
import static me.qmx.jitescript.internal.org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static me.qmx.jitescript.internal.org.objectweb.asm.Opcodes.ACC_STATIC;
import me.qmx.jitescript.util.CodegenUtils;
import static me.qmx.jitescript.util.CodegenUtils.c;

/**
 *
 * @author summers
 */
public class Compiler {

    private static class DynamicClassLoader extends ClassLoader {

        public Class<?> define(JiteClass jiteClass) {
            byte[] classBytes = jiteClass.toBytes();
            return super.defineClass(c(jiteClass.getClassName()), classBytes, 0, classBytes.length);
        }
    }

    public static Class<?> compile(Object parse) {
        JiteClass jiteClass = new JiteClass("anonymous") {
            {
                defineDefaultConstructor();

            }
        };

        if (parse.getClass() == Boolean.class) {
            if ((Boolean)parse) {
                jiteClass.defineMethod("main", ACC_PUBLIC | ACC_STATIC, CodegenUtils.sig(boolean.class),
                    newCodeBlock().iconst_1().ireturn());
            } else {
                jiteClass.defineMethod("main", ACC_PUBLIC | ACC_STATIC, CodegenUtils.sig(boolean.class),
                    newCodeBlock().iconst_0().ireturn());
            }
            
        } else {
            jiteClass.defineMethod("main", ACC_PUBLIC | ACC_STATIC, CodegenUtils.sig(Object.class),
                    newCodeBlock().aconst_null().areturn());
        }

        return new DynamicClassLoader().define(jiteClass);
    }

}

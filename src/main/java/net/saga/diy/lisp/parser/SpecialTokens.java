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
package net.saga.diy.lisp.parser;

import java.util.HashSet;
import java.util.Set;
import static net.saga.diy.lisp.parser.AST.Token.create;

public final class SpecialTokens {
    private SpecialTokens() {
    }

    public static final AST.Token QUOTE = create(String.class, "quote");
    public static final AST.Token ATOM = create(String.class, "atom");
    public static final AST.Token EQ = create(String.class, "eq");
    public static final AST.Token IF = create(String.class, "if");
    public static final AST.Token DEFINE = create(String.class, "define");

    public static final Set<AST.Token> MATHS = new HashSet<>(6);
    static {
        MATHS.add(create(String.class, "+"));
        MATHS.add(create(String.class, "-"));
        MATHS.add(create(String.class, "*"));
        MATHS.add(create(String.class, "/"));
        MATHS.add(create(String.class, "mod"));
        MATHS.add(create(String.class, "<"));
        MATHS.add(create(String.class, ">"));
    }

}

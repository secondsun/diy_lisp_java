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
package net.saga.diy.lisp;

import java.util.HashSet;
import java.util.Set;

public final class SpecialTokens {
    private SpecialTokens() {
    }

    public static final String QUOTE = "quote";
    public static final String ATOM = "atom";
    public static final String EQ = "eq";
    public static final String IF = "if";
    public static final String DEFINE = "define";
    public static final String LAMBDA = "lambda";
    public static final String CONS = "cons";
    public static final String HEAD = "head";
    public static final String TAIL = "tail";
    public static final String EMPTY = "empty";

    public static final Set MATHS = new HashSet<>(6);
    public static final Set ALL_TOKENS = new HashSet();

    static {
        MATHS.add("+");
        MATHS.add("-");
        MATHS.add("*");
        MATHS.add("/");
        MATHS.add("mod");
        MATHS.add("<");
        MATHS.add(">");

        ALL_TOKENS.add(QUOTE);
        ALL_TOKENS.add(ATOM);
        ALL_TOKENS.add(EQ);
        ALL_TOKENS.add(IF);
        ALL_TOKENS.add(DEFINE);
        ALL_TOKENS.add(LAMBDA);
        ALL_TOKENS.add(CONS);
        ALL_TOKENS.add(HEAD);
        ALL_TOKENS.add(TAIL);
        ALL_TOKENS.add(EMPTY);

        ALL_TOKENS.addAll(MATHS);

    }

}

/*
 * Copyright 2014 summers.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.saga.diy.lisp.parser.operation;

import java.util.List;
import net.saga.diy.lisp.parser.AST;
import net.saga.diy.lisp.parser.AST.Token;
import static net.saga.diy.lisp.parser.AST.Token.create;
import net.saga.diy.lisp.parser.Evaluator;
import net.saga.diy.lisp.parser.SpecialTokens;
import net.saga.diy.lisp.parser.types.Environment;
import net.saga.diy.lisp.parser.types.LispException;

public class ConsOperation implements Operation<Operation> {

    @Override
    public Operation operate(AST.Token toAddToken, Environment env) {

        return ((listToken, listEnv) -> {

            Object toAddValue;
            if (toAddToken.tree != null) {
                toAddValue = Evaluator.evaluate(toAddToken.tree, env);
            } else {
                toAddValue = toAddToken.value;
            }

            if (listToken.tree == null) {
                throw new LispException(listToken + " is not a list");
            }

            Token[] tokens;

            if (SpecialTokens.ALL_TOKENS.contains(listToken.tree.tokens.get(0))) {
                List<Token> listOfTokens = ((AST) Evaluator.evaluate(listToken.tree, env)).tokens;
                tokens = new Token[listOfTokens.size() + 1];
                tokens[0] = toAddToken;
                for (int i = 1; i < tokens.length; i++) {
                    tokens[i] = listOfTokens.get(i - 1);
                }
            } else {
                tokens = new Token[listToken.tree.tokens.size() + 1];
                tokens[0] = toAddToken;
                for (int i = 1; i < tokens.length; i++) {
                    tokens[i] = listToken.tree.tokens.get(i - 1);
                }
            }

            return (new AST(tokens));
        });
    }

}

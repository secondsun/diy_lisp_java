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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import static net.saga.diy.lisp.parser.SpecialTokens.QUOTE;
import net.saga.diy.lisp.parser.operation.AtomOperation;
import net.saga.diy.lisp.parser.operation.DefineOperation;
import net.saga.diy.lisp.parser.operation.EqOperation;
import net.saga.diy.lisp.parser.operation.IfOperation;
import net.saga.diy.lisp.parser.operation.LambdaOperation;
import net.saga.diy.lisp.parser.operation.LookupOperation;
import net.saga.diy.lisp.parser.operation.Operation;
import net.saga.diy.lisp.parser.operation.QuoteOperation;
import net.saga.diy.lisp.parser.operation.math.MathOperation;
import net.saga.diy.lisp.parser.types.Environment;
import net.saga.diy.lisp.parser.types.LispException;

public class Evaluator {

    public static Object evaluate(AST ast, Environment env) {
        ArrayList value = new ArrayList();
        Operation operation = null;
        Iterator<AST.Token> tokensItem = ast.tokens.iterator();
        while (tokensItem.hasNext()) {
            AST.Token token = tokensItem.next();
            if (token.tree != null) {
                Object res = evaluate(token.tree, env);
                if (res.getClass().isArray()) {
                    Object[] arrayRes = (Object[]) res;
                    for (Object arrayObject : arrayRes) {
                        if (arrayObject == Void.TYPE) {
                            if (arrayRes.length > 1) {
                                throw new LispException("Too many arguments " + token);
                            }
                        }
                    }
                    value.addAll(Arrays.asList((Object[]) res));
                } else {
                    if (res != Void.TYPE) {
                        value.add(res);
                    }
                }
            } else {
                if (token.type == String.class) {
                    if (QUOTE.equals(token)) {
                        operation = new QuoteOperation();

                    } else if (SpecialTokens.ATOM.equals(token)) {
                        operation = new AtomOperation();

                    } else if (SpecialTokens.EQ.equals(token)) {
                        operation = new EqOperation();

                    } else if (SpecialTokens.IF.equals(token)) {
                        operation = new IfOperation();

                    } else if (SpecialTokens.MATHS.contains(token)) {
                        operation = new MathOperation(token);
                    } else if (SpecialTokens.DEFINE.equals(token)) {
                        operation = new DefineOperation();
                    } else if (SpecialTokens.LAMBDA.equals(token)) {
                        operation = new LambdaOperation();
                    }else {
                        operation = new LookupOperation();
                        value.add(operation.operate(token, env));
                        continue;
                    }

                    Object res = operation.operate(tokensItem.next(), env);

                    while (res instanceof Operation) {
                        if (!tokensItem.hasNext()) {
                            throw new LispException("Missing token");
                        }
                        res = ((Operation) res).operate(tokensItem.next(), env);
                    }
                    if (res == null) {
                        value.add(Void.TYPE);
                    } else if (res.getClass().isArray()) {
                        value.addAll(Arrays.asList((Object[]) res));
                    } else {
                        value.add(res);
                    }

                    operation = null;

                } else if (token.type == Boolean.class) {
                    value.add(token.value);
                } else if (token.type == Integer.class) {
                    value.add(token.value);
                }
            }
        }

        if (value.size() == 1) {
            return value.get(0);
        }
        if (value.isEmpty()) {
            return Void.class;
        } else {
            return value.toArray();
        }
    }

}

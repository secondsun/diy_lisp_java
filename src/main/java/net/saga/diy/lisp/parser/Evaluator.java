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
import java.util.List;
import net.saga.diy.lisp.parser.AST.Token;
import static net.saga.diy.lisp.parser.AST.Token.create;
import static net.saga.diy.lisp.parser.SpecialTokens.QUOTE;
import net.saga.diy.lisp.parser.operation.AtomOperation;
import net.saga.diy.lisp.parser.operation.ConsOperation;
import net.saga.diy.lisp.parser.operation.DefineOperation;
import net.saga.diy.lisp.parser.operation.EmptyOperation;
import net.saga.diy.lisp.parser.operation.EqOperation;
import net.saga.diy.lisp.parser.operation.HeadOperation;
import net.saga.diy.lisp.parser.operation.IfOperation;
import net.saga.diy.lisp.parser.operation.LambdaOperation;
import net.saga.diy.lisp.parser.operation.LookupOperation;
import net.saga.diy.lisp.parser.operation.Operation;
import net.saga.diy.lisp.parser.operation.QuoteOperation;
import net.saga.diy.lisp.parser.operation.TailOperation;
import net.saga.diy.lisp.parser.operation.math.MathOperation;
import net.saga.diy.lisp.parser.types.Closure;
import net.saga.diy.lisp.parser.types.Environment;
import net.saga.diy.lisp.parser.types.LispException;

public class Evaluator {

    public static Object evaluate(Token token, Environment env) {
        if (token.tree != null) {
            validateTreeForEvaluation(token.tree, env);
            return evaluate(token.tree, env);
        } else if (token.type == Boolean.class) {
            return (token.value);
        } else if (token.type == Integer.class) {
            return (token.value);
        } else if (token.type == String.class) {
            Object value = env.lookup((String) token.value);
            if (value != null) {
                return value;
            }

        }
        throw new LispException("Illegal token");
    }

    public static Object evaluate(AST ast, Environment env) {
        ArrayList value = new ArrayList();
        Operation operation = null;
        Iterator<AST.Token> tokensItem = ast.tokens.iterator();
        while (tokensItem.hasNext()) {
            AST.Token token = tokensItem.next();
            if (token.tree != null) {
                validateTreeForEvaluation(token.tree, env);
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
                    } else if (SpecialTokens.CONS.equals(token)) {
                        operation = new ConsOperation();
                    } else if (SpecialTokens.HEAD.equals(token)) {
                        operation = new HeadOperation();
                    } else if (SpecialTokens.TAIL.equals(token)) {
                        operation = new TailOperation();
                    }  else if (SpecialTokens.EMPTY.equals(token)) {
                        operation = new EmptyOperation();
                    } else if (SpecialTokens.LAMBDA.equals(token)) {
                        operation = new LambdaOperation();
                    } else {
                        operation = new LookupOperation();
                        Object result = operation.operate(token, env);
                        if (!(result instanceof Operation)) {
                            value.add(result);
                            continue;
                        } else {
                            operation = (Operation) result;
                        }

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
                } else if (token.type == Closure.class) {

                    Closure closure = (Closure) token.value;
                    Environment envClosure = new Environment(closure.getEnv());
                    List<String> vars = closure.getParams();
                    vars.forEach(var -> envClosure.set(var, evaluate(new AST(tokensItem.next()), env)));
                    value.add(evaluate((closure).getBody(), envClosure));
                }
            }
        }

        if (value.size() == 1) {
            return value.get(0);
        }
        if (value.isEmpty()) {
            return Void.class;
        } else {

            Object[] valueArray = value.toArray();
            if (valueArray[0] instanceof Closure) {
                List<Token> closureTokens = new ArrayList<>(valueArray.length);
                for (Object obj : valueArray) {
                    closureTokens.add(create(obj.getClass(), obj));
                }

                return evaluate(new AST(closureTokens.toArray(new Token[0])), env);

            } else {
                throw new LispException("Illegal tokens");
                //return valueArray;
            }
        }
    }

    /**
     * 
     * Make sure the AST is a function call or a call to a special form.
     * 
     * @param tree
     */
    private static void validateTreeForEvaluation(AST tree, Environment env) {
        Token token = tree.tokens.get(0);
        if (token.tree != null) {// call to another list
            return;
        } else {
            if (token.type == String.class) {
                if (SpecialTokens.ALL_TOKENS.contains(token)) {
                    return;
                } else {
                    LookupOperation operation = new LookupOperation();
                    Object result = operation.operate(token, env);
                    if (!(result instanceof Operation)) {
                        throw new LispException("List is not a function call");
                    } else {
                        return;
                    }

                }

            } else if (token.type == Boolean.class) {
                throw new LispException("List is not a function call");
            } else if (token.type == Integer.class) {
                throw new LispException("List is not a function call");
            } else if (token.type == Closure.class) {
                return;
            }
        }
    }

}

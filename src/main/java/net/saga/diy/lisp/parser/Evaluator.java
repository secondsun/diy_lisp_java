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
import static net.saga.diy.lisp.parser.types.Utils.isList;

public class Evaluator {

    private static Object evaluateSingle(Object token, Environment env) {
        if (token.getClass().isArray()) {
            validateTreeForEvaluation((Object[])token, env);
            return evaluate((Object[])token, env);
        } else if (token.getClass() == Boolean.class) {
            return (token);
        } else if (token.getClass() == Integer.class) {
            return (token);
        } else if (token.getClass() == String.class) {
            Object value = env.lookup((String) token);
            if (value != null) {
                return value;
            }

        }
        throw new LispException("Illegal token");
    }

    public static Object evaluate(Object input, Environment env) {
        if (!isList(input)) {
            return evaluateSingle(input, env);
        }
        Object[]ast = (Object[]) input;
        validateTreeForEvaluation(ast, env);
        ArrayList value = new ArrayList();
        Operation operation = null;
        int length = ast.length;
        for (int pointer = 0; pointer < length; pointer++) {
            Object token = ast[pointer];
            if (token.getClass().isArray()) {
                Object res = evaluate((Object[])token, env);
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
                if (token.getClass() == String.class) {
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

                    Object res = operation.operate(ast[++pointer], env);

                    while (res instanceof Operation) {
                        if ((pointer + 1) >= ast.length) {
                            throw new LispException("Missing token");
                        }
                        res = ((Operation) res).operate(ast[++pointer], env);
                    }
                    if (res == null) {
                        value.add(Void.TYPE);
                    } else {
                        value.add(res);
                    }

                    if ((pointer + 1) != ast.length)  {
                        throw new LispException("Expected end of expression at " + ast[pointer]);
                    }
                    
                    operation = null;

                } else if (token.getClass() == Boolean.class) {
                    value.add(token);
                } else if (token.getClass() == Integer.class) {
                    value.add(token);
                } else if (token.getClass() == Closure.class) {

                    Closure closure = (Closure) token;
                    Environment envClosure = new Environment(closure.getEnv());
                    Object[] vars = closure.getParams();
                    for (Object var : vars) {
                        envClosure.set((String) var, evaluate((Object)ast[++pointer], env));
                    }
                    
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
                List<Object> closureTokens = new ArrayList<>(valueArray.length);
                for (Object obj : valueArray) {
                    closureTokens.add(obj);
                }

                return evaluate(closureTokens.toArray(), env);

            } else {
                //throw new LispException("Illegal tokens " + valueArray[0]);
                return valueArray;
            }
        }
    }

    /**
     * 
     * Make sure the AST is a function call or a call to a special form.
     * 
     * @param tree
     */
    private static void validateTreeForEvaluation(Object[] tree, Environment env) {
        Object token = tree[0];
        if (token.getClass().isArray()) {// call to another list
            return;
        } else {
            if (token.getClass() == String.class) {
                if (SpecialTokens.ALL_TOKENS.contains(token)) {
                    return;
                } else {
                    LookupOperation operation = new LookupOperation();
                    Object result = operation.operate(token, env);
                    if (!(result instanceof Operation)) {
                        throw new LispException("List is not a function call:" + result + " from " + tree);
                    } else {
                        return;
                    }

                }

            } else if (token  instanceof Boolean) {
                throw new LispException("List is not a function call: " + tree);
            } else if (token  instanceof Integer) {
                throw new LispException("List is not a function call: " + tree);
            } else if (token instanceof Closure) {
                return;
            }
        }
    }

}

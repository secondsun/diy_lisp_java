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
package net.saga.diy.lisp.evaluator.operation;

import net.saga.diy.lisp.Evaluator;
import net.saga.diy.lisp.evaluator.operation.Operation;
import net.saga.diy.lisp.types.Environment;
import net.saga.diy.lisp.types.LispException;

public class MathOperation implements Operation<Operation> {

    private final Operand op;

    public MathOperation(Object token) {
        op = Operand.fromSymbol((String) token);
    }

    @Override
    public Operation operate(final Object token, Environment env) {

        final int value = verifyAndEvaluate(token, env);
        
        switch (op) {
            case ADD:
                return ((nextToken, env2) -> (value + verifyAndEvaluate(nextToken, env2)));

            case SUB:
                return ((nextToken, env2) -> {
                    return value - verifyAndEvaluate(nextToken, env2);
                });
            case DIV:
                return ((nextToken, env2) -> {
                    return value / verifyAndEvaluate(nextToken, env2);
                });
            case MOD:
                return ((nextToken, env2) -> {
                    return value % verifyAndEvaluate(nextToken, env2);
                });
            case GT:
                return ((nextToken, env2) -> {
                    return value > verifyAndEvaluate(nextToken, env2);
                });
            case LT:
                return ((nextToken, env2) -> {
                    return value < verifyAndEvaluate(nextToken, env2);
                });
            case MULT:
                return ((nextToken, env2) -> {
                    return value * verifyAndEvaluate(nextToken, env2);
                });
            default:
                throw new AssertionError(op.name());
        }
    };

    private Integer verifyAndEvaluate(Object token, Environment env) {

        if (token instanceof Integer) {
            return (Integer) token;
        }

        if (token instanceof String) {
            return (Integer) env.lookup((String) token);
        }

        if (token.getClass().isArray()) {
            Object result = Evaluator.evaluate((Object[]) token, env);
            if (result instanceof Integer) {
                return (Integer) result;
            }
        }

        throw new LispException("Math operations expect a Integer");

    }

}
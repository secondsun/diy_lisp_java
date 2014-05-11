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
 */
package net.saga.diy.lisp.parser.operation;

import net.saga.diy.lisp.parser.AST;
import net.saga.diy.lisp.parser.Evaluator;
import net.saga.diy.lisp.parser.types.Environment;

/**
 *
 * @author summers
 */
public class Eq2Operation implements Operation<Boolean> {

    private final AST.Token firstToken;

    public Eq2Operation(AST.Token firstArgument) {
        this.firstToken = firstArgument;
    }

    @Override
    public Boolean operate(AST.Token secondToken, Environment env) {
        AtomOperation isAtom = new AtomOperation();

        Object firstValue;
        Object secondValue;
        if (firstToken.tree == null) {
            firstValue = (Evaluator.evaluate(new AST(firstToken), env));
        } else {
            firstValue = (Evaluator.evaluate(firstToken.tree, env));
        }
        if (secondToken.tree == null) {
            secondValue = (Evaluator.evaluate(new AST(secondToken), env));
        } else {
            secondValue = (Evaluator.evaluate(secondToken.tree, env));
        }
        
        if (firstValue instanceof AST || firstValue.getClass().isArray()) {
            return false;
        }
        
        if (secondValue instanceof AST || secondValue.getClass().isArray()) {
            return false;
        }
        
        
        
        return firstValue.equals(secondValue);

    }

}

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

package net.saga.diy.lisp.parser.operation;

import net.saga.diy.lisp.parser.AST;
import net.saga.diy.lisp.parser.SpecialTokens;
import net.saga.diy.lisp.parser.types.Environment;

/**
 * 
 * @author summers
 */
public class AtomOperation implements Operation<Boolean> {

    public AtomOperation() {
    }

    /*
     * I made a mistake in how I parse "'" and it creates a new AST. It makes
     * some operations easier, but atoms now have to make sure a list isn't being
     * quoted.
     */
    @Override
    public Boolean operate(AST.Token token, Environment env) {
        if (token.tree == null) {
            return true;
        }
        return SpecialTokens.QUOTE.equals(token.tree.tokens.get(0)) &&
                token.tree.tokens.size() == 2 &&
                token.tree.tokens.get(1).tree == null;
    }

}

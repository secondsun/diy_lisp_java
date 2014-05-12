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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.saga.diy.lisp.parser.AST;
import net.saga.diy.lisp.parser.AST.Token;
import static net.saga.diy.lisp.parser.AST.Token.create;
import static net.saga.diy.lisp.parser.Evaluator.evaluate;
import static net.saga.diy.lisp.parser.Evaluator.evaluate;
import static net.saga.diy.lisp.parser.Evaluator.evaluate;
import net.saga.diy.lisp.parser.types.Environment;
import net.saga.diy.lisp.parser.types.LispException;

/**
 *
 * @author summers
 */
public class TailOperation implements Operation<AST>{

    @Override
    public AST operate(AST.Token listToken, Environment env) {
        if (listToken.tree == null) {
            throw new LispException(listToken + " is not a list");
        } else if (listToken.tree.tokens.isEmpty() ) {
            throw new LispException(listToken + " is empty");
        }
        
        Object result = evaluate(listToken.tree, env);
        
        if (result instanceof AST) {
            AST ast = (AST) result;
            if (ast.tokens.isEmpty() ) {
                throw new LispException(listToken + " is empty");
            }
            return new AST(ast.tokens.subList(1, ast.tokens.size()).toArray(new Token[ast.tokens.size() - 1]));
        } else if (result.getClass().isArray()) {
            Object[] resultArray = (Object[]) result;
//            Token[] tokenArray = new Token[resultArray.length - 1];
            List<Token> tokenList = new ArrayList<>(resultArray.length - 1);
            Arrays.stream(resultArray, 1, resultArray.length).forEach((t) -> tokenList.add(create(t.getClass(), t)));
            return new AST(tokenList.toArray(new Token[resultArray.length - 1]));
            
        }
        throw new LispException(listToken + " is not a list");
    }
    
}

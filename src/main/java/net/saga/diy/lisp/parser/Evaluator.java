package net.saga.diy.lisp.parser;

import net.saga.diy.lisp.parser.operation.EqOperation;
import net.saga.diy.lisp.parser.operation.AtomOperation;
import net.saga.diy.lisp.parser.operation.QuoteOperation;
import net.saga.diy.lisp.parser.operation.Operation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import static net.saga.diy.lisp.parser.SpecialTokens.QUOTE;
import net.saga.diy.lisp.parser.operation.math.MathOperation;
import net.saga.diy.lisp.parser.types.Environment;
import net.saga.diy.lisp.parser.types.LispException;

public class Evaluator {
    
    public static Object evaluate(AST ast, Environment env){
        ArrayList value = new ArrayList();
        Operation operation= null;
        Iterator<AST.Token> tokensItem = ast.tokens.iterator();
        while(tokensItem.hasNext()) {
            AST.Token token = tokensItem.next();
            if (token.tree != null) {
                Object res = evaluate(token.tree, env);
                if (res.getClass().isArray()) {
                    value.addAll(Arrays.asList((Object[])res));
                } else {
                    value.add(res);
                }
            } else {
                if (token.type == String.class) {
                    if (QUOTE.equals(token)) {
                        operation = new QuoteOperation();
                    
                    } else if (SpecialTokens.ATOM.equals(token)) {
                        operation = new AtomOperation();
                    
                    } else if (SpecialTokens.EQ.equals(token)) {
                        operation = new EqOperation();
                    
                    } else if (SpecialTokens.MATHS.contains(token)) {
                        operation = new MathOperation(token);
                    } else {
                            throw new LispException("Unexpected token " + token);
                    }
                    
                    Object res = operation.operate(tokensItem.next());
                    
                    while (res instanceof Operation) {
                        res = ((Operation)res).operate(tokensItem.next());
                    }
                    
                    if (res.getClass().isArray()) {
                        value.addAll(Arrays.asList((Object[])res));
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
        } if (value.isEmpty()) {
            return Void.class;
        } else {
            return value.toArray();
        }
    }



    
    
    
    
}

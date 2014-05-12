package net.saga.diy.lisp.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static net.saga.diy.lisp.parser.Evaluator.evaluate;
import static net.saga.diy.lisp.parser.Parser.parse;
import net.saga.diy.lisp.parser.types.Environment;

public class Interpreter {

    public static void interpret(String source) {
            interpret(source, null);
    }
    
    public static Object interpret(String source, Environment env) {
        if (env == null) {
            env = new Environment();
        }
        
        return evaluate(parse(source), env);
        
    }
    
    
    
    public static List<Object> interpretFile(File sourceFile, Environment env) {
        if (env == null) {
            env = new Environment();
        }
        
        try {
            Reader reader = new FileReader(sourceFile);
            List results = new ArrayList();
            for (Object exp : Parser.parseMultiple(reader)) {
                results.add(evaluate(exp, env));
            }
            return results;
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
    
}

package net.saga.diy.lisp.parser;

import java.util.HashSet;
import java.util.Set;
import static net.saga.diy.lisp.parser.AST.Token.create;

public final class SpecialTokens {
    private SpecialTokens(){}
    
    public static final AST.Token QUOTE = create(String.class, "quote");
    public static final AST.Token ATOM = create(String.class, "atom");
    public static final AST.Token EQ = create(String.class, "eq");
    
    public static final Set<AST.Token> MATHS = new HashSet<>(6);
    static {
        MATHS.add(create(String.class, "+"));
        MATHS.add(create(String.class, "-"));
        MATHS.add(create(String.class, "*"));
        MATHS.add(create(String.class, "/"));
        MATHS.add(create(String.class, "mod"));
        MATHS.add(create(String.class, "<"));
        MATHS.add(create(String.class, ">"));
    }
    
}

package net.saga.diy.lisp.parser;

import static net.saga.diy.lisp.parser.AST.Token.create;

public final class SpecialTokens {
    private SpecialTokens(){}
    
    public static AST.Token QUOTE = create(String.class, "quote");
    public static AST.Token ATOM = create(String.class, "atom");
    public static AST.Token EQ = create(String.class, "eq");
    
}

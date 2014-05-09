/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.saga.diy.lisp.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author summers
 */
public class AST implements Iterable<AST.Token> {

    public List<Token> tokens = new ArrayList<>();

    public AST() {}
    
    public AST(Token... tokens) {
        this.tokens.addAll(Arrays.asList(tokens));
    }
    
    @Override
    public Iterator<Token> iterator() {

        Iterator<Token> tokenIterator = new Iterator<Token>() {

            final List<Token> currentList = new ArrayList<>(tokens);
            Iterator<Token> tokenIterator;
            int index = 0;

            @Override
            public boolean hasNext() {
                return currentList.size() > index;
            }

            @Override
            public Token next() {
                if (!hasNext()) {
                    throw new ArrayIndexOutOfBoundsException();
                }

                Token token = currentList.get(index);
                if (token.tree == null) {
                    index++;
                    return token;
                } else {

                    if (tokenIterator == null) {
                        tokenIterator = token.tree.iterator();
                    }
                    Token toReturn = tokenIterator.next();
                    if (!tokenIterator.hasNext()) {
                        tokenIterator = null;
                        index++;
                    }
                    return toReturn;

                }

            }
        };
        return tokenIterator;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.tokens);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AST other = (AST) obj;
        if (!Objects.equals(this.tokens, other.tokens)) {
            return false;
        }
        return true;

    }

    @Override
    public String toString() {
        return toString(0);
    }
    
    private String toString(final int indent) {
        
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < indent; i++) {
            sb.append("\t");    
        }
        sb.append("AST{tokens=");
        
        tokens.forEach((token)-> {
            if (token.tree == null) {
                sb.append("Token[").append(token.type.getSimpleName()).append(":").append(token.value).append("],");
            } else {
                sb.append("AST{tokens=\n");
                sb.append(token.tree.toString(1 + indent));
                sb.append("}");
            }
        });
        
        
        sb.append("}");
        return sb.toString();
    }

    
    

    public static class Token {

        public Class<?> type;
        public Object value;
        public AST tree;

        public Token() {
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 59 * hash + Objects.hashCode(this.type);
            hash = 59 * hash + Objects.hashCode(this.value);
            hash = 59 * hash + Objects.hashCode(this.tree);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Token other = (Token) obj;
            if (!Objects.equals(this.type, other.type)) {
                return false;
            }
            if (!Objects.equals(this.value, other.value)) {
                return false;
            }
            if (!Objects.equals(this.tree, other.tree)) {
                return false;
            }
            return true;
        }

        public static Token create(AST ast) {
            Token token = new Token();
            token.tree = ast;
            return token;
        }
        
        public static Token create(Class<?> type, Object value) {
            Token token = new Token();
            token.tree = null;
            token.type = type;
            token.value = value;
            return token;
        }

        @Override
        public String toString() {
            return "Token{" + "type=" + type + ", value=" + value + ", tree=" + tree + '}';
        }
        
    }

}

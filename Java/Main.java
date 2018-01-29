package src;


import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String args[]) throws IOException {

        ANTLRFileStream inputStream = new ANTLRFileStream(args[0]);
        HelloLexer lexer = new HelloLexer(inputStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        HelloParser parser = new HelloParser(tokenStream);
        // begin parsing at ap() rule //
        ParseTree tree = parser.ap();

        List<Context> res=new MyVisitor().visit(tree);
        for(int i=0;i<res.size();i++) {
            System.out.println(res.get(i).node.getTextContent());
        }
    }
}

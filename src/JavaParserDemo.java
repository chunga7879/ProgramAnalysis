import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JavaParserDemo {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("src/ArithmeticExceptionExample.java");
        CompilationUnit compilationUnit = StaticJavaParser.parse(path);
        compilationUnit.accept(new AstVisitor(), null);
    }
}

package utils;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import com.github.javaparser.resolution.SymbolResolver;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.util.*;
import java.util.stream.Collectors;

public final class JavadocUtil {
    public static final String RUNTIME_EXCEPTION_QN = "java.lang.RuntimeException";

    /**
     * Get exceptions that are in @throws in Javadoc
     * @param dec MethodDeclaration with Javadocs
     * @return Set of ResolvedType for Exceptions
     */
    public static Set<ResolvedType> getThrows(MethodDeclaration dec) {
        Javadoc javadoc = dec.getJavadoc().orElse(null);
        if (javadoc == null) return Collections.emptySet();
        SymbolResolver symbolResolver = StaticJavaParser.getParserConfiguration().getSymbolResolver().orElseThrow();
        return javadoc.getBlockTags().stream()
                .filter(x -> x.getType() == JavadocBlockTag.Type.THROWS && x.getName().isPresent())
                .map(x -> {
                    NameExpr nameExpr = new NameExpr(x.getName().get());
                    nameExpr.setParentNode(dec);
                    try {
                        return symbolResolver.calculateType(nameExpr);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * Get runtime exceptions that are in @throws in Javadoc
     */
    public static List<ResolvedType> getRuntimeThrows(MethodDeclaration dec) {
        ResolvedReferenceTypeDeclaration runtimeExceptionType = new ReflectionTypeSolver().solveType(RUNTIME_EXCEPTION_QN);
        return JavadocUtil.getThrows(dec).stream()
                .filter(t -> runtimeExceptionType.isAssignableBy(t))
                .toList();
    }
}

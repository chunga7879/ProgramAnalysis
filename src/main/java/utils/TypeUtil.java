package utils;

import com.github.javaparser.resolution.types.ResolvedType;

import java.util.Objects;

public class TypeUtil {
    public static final String STRING_QN = "java.lang.String";
    public static final String BOXED_INTEGER_QN = "java.lang.Integer";
    public static final String BOXED_BOOLEAN_QN = "java.lang.Boolean";
    public static final String BOXED_CHARACTER_QN = "java.lang.Character";

    public static boolean isStringType(ResolvedType type) {
        return type != null && type.isReferenceType() && Objects.equals(type.asReferenceType().getQualifiedName(), STRING_QN);
    }
}

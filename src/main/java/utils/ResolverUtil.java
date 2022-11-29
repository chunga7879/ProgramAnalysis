package utils;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.resolution.Resolvable;
import com.github.javaparser.resolution.types.ResolvedType;

public final class ResolverUtil {
    public static <T> T resolveOrNull(Resolvable<T> resolvable) {
        try {
            return resolvable.resolve();
        } catch (Exception e) {
            return null;
        }
    }

    public static ResolvedType calculateResolvedTypeOrNull(Expression expr) {
        try {
            return expr.calculateResolvedType();
        } catch (Exception e) {
            return null;
        }
    }
}

package utils;

import com.github.javaparser.resolution.Resolvable;

public final class ResolverUtil {
    public static <T> T resolveOrNull(Resolvable<T> resolvable) {
        try {
            return resolvable.resolve();
        } catch (Exception e) {
            return null;
        }
    }
}

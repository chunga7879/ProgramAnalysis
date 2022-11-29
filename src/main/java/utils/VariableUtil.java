package utils;

import analysis.model.AnalysisError;
import analysis.model.ExpressionAnalysisState;
import analysis.model.VariablesState;
import analysis.values.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserParameterDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserVariableDeclaration;

import java.util.Objects;
import java.util.function.Function;

/**
 * Util for Variables State in our analysis
 */
public final class VariableUtil {
    /**
     * Set a value of an Expression to a value
     */
    public static void setVariableFromExpression(Expression variableExpression, PossibleValues value, VariablesState variablesState) {
        if (variableExpression instanceof NameExpr nameVariableExpression) {
            ResolvedValueDeclaration dec = ResolverUtil.resolveOrNull(nameVariableExpression);
            setVariableFromDeclaration(dec, value, variablesState);
        } else if (variableExpression instanceof FieldAccessExpr fieldAccessExpression) {
            Expression scopeExpr = fieldAccessExpression.getScope();
            if (scopeExpr instanceof NameExpr scopeNameExpr) {
                ResolvedValueDeclaration scopeDec = ResolverUtil.resolveOrNull(scopeNameExpr);
                if (scopeDec != null && scopeDec.getType().isArray() && fieldAccessExpression.getNameAsString().equalsIgnoreCase("length")) {
                    updateArrayLength(scopeDec, value, variablesState);
                }
            }
        } else if (variableExpression instanceof UnaryExpr unaryExpr) {
            if (unaryExpr.getOperator() == UnaryExpr.Operator.PREFIX_INCREMENT
                    || unaryExpr.getOperator() == UnaryExpr.Operator.PREFIX_INCREMENT) {
                setVariableFromExpression(unaryExpr.getExpression(), value, variablesState);
            }
        } else if (variableExpression instanceof AssignExpr assignExpr) {
            setVariableFromExpression(assignExpr.getTarget(), value, variablesState);
        } else if (variableExpression instanceof EnclosedExpr enclosedExpr) {
            setVariableFromExpression(enclosedExpr.getInner(), value, variablesState);
        }
        // Might want to move to visitor
    }

    /**
     * @see VariableUtil#setVariableFromExpression(Expression, PossibleValues, VariablesState)
     */
    public static void setVariableFromExpression(
            Expression variableExpression,
            PossibleValues value1, VariablesState variablesState1,
            PossibleValues value2, VariablesState variablesState2
    ) {
        if (variableExpression instanceof NameExpr nameVariableExpression) {
            ResolvedValueDeclaration dec = ResolverUtil.resolveOrNull(nameVariableExpression);
            if (dec == null) return;
            setVariableFromDeclaration(dec, value1, variablesState1);
            setVariableFromDeclaration(dec, value2, variablesState2);
        } else if (variableExpression instanceof FieldAccessExpr fieldAccessExpression) {
            Expression scopeExpr = fieldAccessExpression.getScope();
            if (scopeExpr instanceof NameExpr scopeNameExpr) {
                ResolvedValueDeclaration scopeDec = ResolverUtil.resolveOrNull(scopeNameExpr);
                if (scopeDec != null && scopeDec.getType().isArray() && fieldAccessExpression.getNameAsString().equalsIgnoreCase("length")) {
                    updateArrayLength(scopeDec, value1, variablesState1);
                    updateArrayLength(scopeDec, value2, variablesState2);
                }
            }
        } else if (variableExpression instanceof UnaryExpr unaryExpr) {
            if (unaryExpr.getOperator() == UnaryExpr.Operator.PREFIX_INCREMENT
                    || unaryExpr.getOperator() == UnaryExpr.Operator.PREFIX_INCREMENT) {
                setVariableFromExpression(unaryExpr.getExpression(), value1, variablesState1, value2, variablesState2);
            }
        } else if (variableExpression instanceof AssignExpr assignExpr) {
            setVariableFromExpression(assignExpr.getTarget(), value1, variablesState1, value2, variablesState2);
        } else if (variableExpression instanceof EnclosedExpr enclosedExpr) {
            setVariableFromExpression(enclosedExpr.getInner(), value1, variablesState1, value2, variablesState2);
        }
    }

    private static void setVariableFromDeclaration(
            ResolvedValueDeclaration dec,
            PossibleValues value, VariablesState variablesState
    ) {
        if (dec instanceof JavaParserVariableDeclaration jpVarDec) {
            variablesState.setVariable(jpVarDec.getVariableDeclarator(), value);
        }
        if (dec instanceof JavaParserParameterDeclaration jpParamDec) {
            variablesState.setVariable(jpParamDec.getWrappedNode(), value);
        }
    }

    public static PossibleValues implicitTypeCasting(
            ResolvedType type,
            Expression expr,
            PossibleValues values,
            ExpressionAnalysisState arg
    ) {
        if (type.isPrimitive()) {
            if (values instanceof BoxedPrimitive boxed) {
                if (boxed.canBeNull()) {
                    arg.addError(new AnalysisError(NullPointerException.class, expr, false));
                    setVariableFromExpression(expr, boxed.withNotNullable(), arg.getVariablesState());
                }
                return boxed.unbox();
            } else if (Objects.equals(values, NullValue.VALUE)) {
                arg.addError(new AnalysisError(NullPointerException.class, expr, true));
                arg.getVariablesState().setDomainEmpty();
            }
        } else if (type.isReferenceType() && values instanceof PrimitiveValue) {
            switch (type.asReferenceType().getQualifiedName()) {
                case "java.lang.Integer", "java.lang.Boolean", "java.lang.Character" -> {
                    return BoxedPrimitive.create(values, false);
                }
            }
        }
        return values;
    }

    /**
     * Update the length of an array
     */
    public static void updateArrayLength(ResolvedValueDeclaration dec, PossibleValues length, VariablesState state) {
        if (dec == null) return;
        Function<PossibleValues, PossibleValues> updateFunc = x -> {
            if (length.isEmpty()) return new EmptyValue();
            if (x instanceof ArrayValue a) return a.withLength(length);
            if (x instanceof AnyValue) return ArrayValue.create(length, true);
            return x;
        };
        if (dec instanceof JavaParserVariableDeclaration scopeVarDec) {
            state.updateVariable(scopeVarDec.getVariableDeclarator(), updateFunc);
        }
        else if (dec instanceof JavaParserParameterDeclaration scopeParamDec) {
            state.updateVariable(scopeParamDec.getWrappedNode(), updateFunc);
        }
    }
}

package utils;

import analysis.model.VariablesState;
import analysis.values.AnyValue;
import analysis.values.ArrayValue;
import analysis.values.EmptyValue;
import analysis.values.PossibleValues;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserParameterDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserVariableDeclaration;

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
            ResolvedValueDeclaration dec = nameVariableExpression.resolve();
            setVariableFromDeclaration(dec, value, variablesState);
        } else if (variableExpression instanceof FieldAccessExpr fieldAccessExpression) {
            Expression scopeExpr = fieldAccessExpression.getScope();
            if (scopeExpr instanceof NameExpr scopeNameExpr) {
                ResolvedValueDeclaration scopeDec = scopeNameExpr.resolve();
                if (scopeDec.getType().isArray() && fieldAccessExpression.getNameAsString().equalsIgnoreCase("length")) {
                    updateArrayLength(scopeDec, value, variablesState);
                }
            }
        }
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
            ResolvedValueDeclaration dec = nameVariableExpression.resolve();
            setVariableFromDeclaration(dec, value1, variablesState1);
            setVariableFromDeclaration(dec, value2, variablesState2);
        } else if (variableExpression instanceof FieldAccessExpr fieldAccessExpression) {
            Expression scopeExpr = fieldAccessExpression.getScope();
            if (scopeExpr instanceof NameExpr scopeNameExpr) {
                ResolvedValueDeclaration scopeDec = scopeNameExpr.resolve();
                if (scopeDec.getType().isArray() && fieldAccessExpression.getNameAsString().equalsIgnoreCase("length")) {
                    updateArrayLength(scopeDec, value1, variablesState1);
                    updateArrayLength(scopeDec, value2, variablesState2);
                }
            }
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

    /**
     * Update the length of an array
     */
    public static void updateArrayLength(ResolvedValueDeclaration dec, PossibleValues length, VariablesState state) {
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

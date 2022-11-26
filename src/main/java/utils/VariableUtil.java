package utils;

import analysis.model.VariablesState;
import analysis.values.PossibleValues;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserParameterDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserVariableDeclaration;

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
}

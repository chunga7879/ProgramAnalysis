package analysis.visitor;

import analysis.model.VariablesState;
import analysis.values.AnyValue;
import analysis.values.IntegerRange;
import analysis.values.PossibleValues;
import analysis.values.StringValue;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.modules.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserParameterDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserVariableDeclaration;

public class ExpressionVisitor implements GenericVisitor<PossibleValues, VariablesState> {

    @Override
    public PossibleValues visit(VariableDeclarationExpr n, VariablesState arg) {
        for (VariableDeclarator declarator : n.getVariables()) {
            declarator.accept(this, arg);
        }
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(VariableDeclarator n, VariablesState arg) {
        if (n.getInitializer().isPresent()) {
            PossibleValues value = n.getInitializer().get().accept(this, arg);
            arg.setVariable(n, value);
            return value;
        }
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(AssignExpr n, VariablesState arg) {
        Expression target = n.getTarget();
        PossibleValues value = n.getValue().accept(this, arg);
        if (target instanceof NameExpr nameTarget) {
            ResolvedValueDeclaration dec = nameTarget.resolve();
            if (dec instanceof JavaParserVariableDeclaration jpVarDec) {
                arg.setVariable(jpVarDec.getVariableDeclarator(), value);
            }
            if (dec instanceof JavaParserParameterDeclaration jpParamDec) {
                arg.setVariable(jpParamDec.getWrappedNode(), value);
            }
        }
        return value;
    }

    @Override
    public PossibleValues visit(ArrayAccessExpr n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ArrayCreationExpr n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ArrayInitializerExpr n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(BinaryExpr n, VariablesState arg) {
        PossibleValues leftValue = n.getLeft().accept(this, arg);
        PossibleValues rightValue = n.getRight().accept(this, arg);
        return switch (n.getOperator()) {
            case PLUS -> leftValue.add(rightValue);
            case MINUS -> leftValue.subtract(rightValue);
            default -> new AnyValue();
        };
    }

    @Override
    public PossibleValues visit(CastExpr n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ClassExpr n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ConditionalExpr n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(EnclosedExpr n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(FieldAccessExpr n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(InstanceOfExpr n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(StringLiteralExpr n, VariablesState arg) {
        return new StringValue();
    }

    @Override
    public PossibleValues visit(IntegerLiteralExpr n, VariablesState arg) {
        return new IntegerRange(n.asNumber().intValue(), n.asNumber().intValue());
    }

    @Override
    public PossibleValues visit(LongLiteralExpr n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(CharLiteralExpr n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(DoubleLiteralExpr n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(BooleanLiteralExpr n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(NullLiteralExpr n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(MethodCallExpr n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(NameExpr n, VariablesState arg) {
        ResolvedValueDeclaration dec = n.resolve();
        if (dec instanceof JavaParserVariableDeclaration jpVarDec) {
            return arg.getVariable(jpVarDec.getVariableDeclarator());
        }
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ObjectCreationExpr n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ThisExpr n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(SuperExpr n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(UnaryExpr n, VariablesState arg) {
        return new AnyValue();
    }

    // region ----Not required----
    // Move any we're not using here
    @Override
    public PossibleValues visit(CompilationUnit n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(MethodDeclaration n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(BlockStmt n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ExpressionStmt n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(SwitchStmt n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(SwitchEntry n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(BreakStmt n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ReturnStmt n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(IfStmt n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(WhileStmt n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ContinueStmt n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(DoStmt n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ForEachStmt n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ForStmt n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ThrowStmt n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(TryStmt n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(CatchClause n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(Name n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(SimpleName n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(LabeledStmt n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(EmptyStmt n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(NodeList n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(PackageDeclaration n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(TypeParameter n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(LineComment n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(BlockComment n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ClassOrInterfaceDeclaration n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(RecordDeclaration n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(CompactConstructorDeclaration n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(EnumDeclaration n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(EnumConstantDeclaration n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(AnnotationDeclaration n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(AnnotationMemberDeclaration n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(FieldDeclaration n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ConstructorDeclaration n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(Parameter n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(InitializerDeclaration n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(JavadocComment n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ClassOrInterfaceType n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(PrimitiveType n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ArrayType n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ArrayCreationLevel n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(IntersectionType n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(MarkerAnnotationExpr n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(SingleMemberAnnotationExpr n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(NormalAnnotationExpr n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(MemberValuePair n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ExplicitConstructorInvocationStmt n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(LocalClassDeclarationStmt n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(LocalRecordDeclarationStmt n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(AssertStmt n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(UnionType n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(VoidType n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(WildcardType n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(UnknownType n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(SynchronizedStmt n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(LambdaExpr n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(MethodReferenceExpr n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(TypeExpr n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ImportDeclaration n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ModuleDeclaration n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ModuleRequiresDirective n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ModuleExportsDirective n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ModuleProvidesDirective n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ModuleUsesDirective n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ModuleOpensDirective n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(UnparsableStmt n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ReceiverParameter n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(VarType n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(Modifier n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(SwitchExpr n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(YieldStmt n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(TextBlockLiteralExpr n, VariablesState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(PatternExpr n, VariablesState arg) {
        return new AnyValue();
    }
    // endregion ----Not required----
}

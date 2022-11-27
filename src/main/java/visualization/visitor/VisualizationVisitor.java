package visualization.visitor;

import analysis.model.*;
import analysis.values.AnyValue;
import analysis.values.visitor.AddApproximateVisitor;
import analysis.values.visitor.SubtractApproximateVisitor;
import analysis.visitor.AnalysisVisitor;
import analysis.visitor.ExpressionVisitor;
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
import logger.AnalysisLogger;
import visualization.DiagramNode;
import visualization.Error;
import visualization.model.VisualizationState;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class VisualizationVisitor implements GenericVisitor<EndState, VisualizationState> {

    public String targetMethod;

    public VisualizationVisitor(String targetMethod) {
        this.targetMethod = targetMethod;
    }

    @Override
    public EndState visit(CompilationUnit n, VisualizationState arg) {
        List<MethodDeclaration> declarations = n.findAll(
                MethodDeclaration.class,
                methodDeclaration -> Objects.equals(methodDeclaration.getNameAsString(), targetMethod)
        );
        if (declarations.isEmpty()) throw new RuntimeException("Method not found");
        MethodDeclaration dec = declarations.get(0);
        arg.diagram.addStartDiagramNode();
        dec.accept(this, arg);
        arg.diagram.addEndDiagramNode();
        return null;
    }

    @Override
    public EndState visit(MethodDeclaration n, VisualizationState arg) {
        String diagramStatement = n.getNameAsString() + "(";

        for (Parameter p : n.getParameters()) {
            diagramStatement = diagramStatement + p + ",";
        }

        diagramStatement = diagramStatement.substring(0, diagramStatement.length() - 1) + ")";
        DiagramNode methodCall = new DiagramNode(diagramStatement, Error.NONE, "");
        arg.diagram.addNode(methodCall);

        Optional<BlockStmt> body = n.getBody();
        return body.map(blockStmt -> blockStmt.accept(this, arg)).orElse(null);
    }

    @Override
    public EndState visit(BlockStmt n, VisualizationState arg) {
        for (Statement s : n.getStatements()) {
            s.accept(this, arg);
        }
        return null;
    }

    @Override
    public EndState visit(ExpressionStmt n, VisualizationState arg) {
        String expression = n.getExpression().toString();

        DiagramNode diagramNode;
        StringBuilder errorDescription = new StringBuilder();
        if (!arg.getErrorMap().get(n).isEmpty()) {
            for (AnalysisError er : arg.getErrorMap().get(n)) {
                errorDescription.append(er.getMessage()).append("\n");
            }
            diagramNode = new DiagramNode(expression, Error.POTENTIAL, errorDescription.toString());
        } else {
            diagramNode = new DiagramNode(expression, Error.NONE, errorDescription.toString());
        }
        arg.diagram.addNode(diagramNode);

        return null;
    }

    @Override
    public EndState visit(SwitchStmt n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(SwitchEntry n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(BreakStmt n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(ReturnStmt n, VisualizationState arg) {
        DiagramNode expression;
        StringBuilder errorDescription = new StringBuilder();
        if (!arg.getErrorMap().get(n).isEmpty()) {
            for (AnalysisError er : arg.getErrorMap().get(n)) {
                errorDescription.append(er.getMessage()).append("\n");
            }
            // TODO: differentiate potential and definite error
            expression = new DiagramNode("return" + n.getExpression(), Error.POTENTIAL, errorDescription.toString());
        } else {
            expression = new DiagramNode("return " + n.getExpression(), Error.NONE, errorDescription.toString());
        }
        arg.diagram.addNode(expression);
        return null;
    }

    @Override
    public EndState visit(IfStmt n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(WhileStmt n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(ContinueStmt n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(DoStmt n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(ForEachStmt n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(ForStmt n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(ThrowStmt n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(TryStmt n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(CatchClause n, VisualizationState arg) {
        return null;
    }


    // region ----Not required----
    // Move any we're not using here
    @Override
    public EndState visit(Name n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(SimpleName n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(ArrayAccessExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(ArrayCreationExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(ArrayInitializerExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(AssignExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(BinaryExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(CastExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(ClassExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(ConditionalExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(EnclosedExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(FieldAccessExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(InstanceOfExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(StringLiteralExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(IntegerLiteralExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(LongLiteralExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(CharLiteralExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(DoubleLiteralExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(BooleanLiteralExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(NullLiteralExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(MethodCallExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(NameExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(ObjectCreationExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(ThisExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(SuperExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(UnaryExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(VariableDeclarationExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(LabeledStmt n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(EmptyStmt n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(NodeList n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(PackageDeclaration n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(TypeParameter n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(LineComment n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(BlockComment n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(ClassOrInterfaceDeclaration n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(RecordDeclaration n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(CompactConstructorDeclaration n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(EnumDeclaration n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(EnumConstantDeclaration n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(AnnotationDeclaration n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(AnnotationMemberDeclaration n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(FieldDeclaration n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(VariableDeclarator n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(ConstructorDeclaration n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(Parameter n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(InitializerDeclaration n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(JavadocComment n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(ClassOrInterfaceType n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(PrimitiveType n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(ArrayType n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(ArrayCreationLevel n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(IntersectionType n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(MarkerAnnotationExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(SingleMemberAnnotationExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(NormalAnnotationExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(MemberValuePair n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(ExplicitConstructorInvocationStmt n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(LocalClassDeclarationStmt n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(LocalRecordDeclarationStmt n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(AssertStmt n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(UnionType n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(VoidType n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(WildcardType n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(UnknownType n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(SynchronizedStmt n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(LambdaExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(MethodReferenceExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(TypeExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(ImportDeclaration n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(ModuleDeclaration n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(ModuleRequiresDirective n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(ModuleExportsDirective n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(ModuleProvidesDirective n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(ModuleUsesDirective n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(ModuleOpensDirective n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(UnparsableStmt n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(ReceiverParameter n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(VarType n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(Modifier n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(SwitchExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(YieldStmt n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(TextBlockLiteralExpr n, VisualizationState arg) {
        return null;
    }

    @Override
    public EndState visit(PatternExpr n, VisualizationState arg) {
        return null;
    }
    // endregion ----Not required----
}

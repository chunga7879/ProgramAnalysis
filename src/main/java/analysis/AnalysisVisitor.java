package analysis;

import analysis.models.VariablesState;
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

import java.util.List;
import java.util.Objects;

public class AnalysisVisitor implements GenericVisitor<Void, VariablesState> {
    private String targetMethod;

    public AnalysisVisitor(String targetMethod) {
        this.targetMethod = targetMethod;
    }

    @Override
    public Void visit(CompilationUnit n, VariablesState arg) {
        List<MethodDeclaration> declarations = n.findAll(
                MethodDeclaration.class,
                methodDeclaration -> Objects.equals(methodDeclaration.getNameAsString(), targetMethod)
        );
        if (declarations.isEmpty()) throw new RuntimeException("Method not found");
        MethodDeclaration dec = declarations.get(0);
        dec.accept(this, arg);
        return null;
    }

    @Override
    public Void visit(PackageDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(TypeParameter n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(LineComment n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(BlockComment n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(ClassOrInterfaceDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(RecordDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(CompactConstructorDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(EnumDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(EnumConstantDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(AnnotationDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(AnnotationMemberDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(FieldDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(VariableDeclarator n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(ConstructorDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(MethodDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(Parameter n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(InitializerDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(JavadocComment n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(ClassOrInterfaceType n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(PrimitiveType n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(ArrayType n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(ArrayCreationLevel n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(IntersectionType n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(UnionType n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(VoidType n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(WildcardType n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(UnknownType n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(ArrayAccessExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(ArrayCreationExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(ArrayInitializerExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(AssignExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(BinaryExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(CastExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(ClassExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(ConditionalExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(EnclosedExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(FieldAccessExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(InstanceOfExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(StringLiteralExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(IntegerLiteralExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(LongLiteralExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(CharLiteralExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(DoubleLiteralExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(BooleanLiteralExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(NullLiteralExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(MethodCallExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(NameExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(ObjectCreationExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(ThisExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(SuperExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(UnaryExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(VariableDeclarationExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(MarkerAnnotationExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(SingleMemberAnnotationExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(NormalAnnotationExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(MemberValuePair n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(ExplicitConstructorInvocationStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(LocalClassDeclarationStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(LocalRecordDeclarationStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(AssertStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(BlockStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(LabeledStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(EmptyStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(ExpressionStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(SwitchStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(SwitchEntry n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(BreakStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(ReturnStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(IfStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(WhileStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(ContinueStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(DoStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(ForEachStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(ForStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(ThrowStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(SynchronizedStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(TryStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(CatchClause n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(LambdaExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(MethodReferenceExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(TypeExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(NodeList n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(Name n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(SimpleName n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(ImportDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(ModuleDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(ModuleRequiresDirective n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(ModuleExportsDirective n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(ModuleProvidesDirective n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(ModuleUsesDirective n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(ModuleOpensDirective n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(UnparsableStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(ReceiverParameter n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(VarType n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(Modifier n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(SwitchExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(YieldStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(TextBlockLiteralExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public Void visit(PatternExpr n, VariablesState arg) {
        return null;
    }
}

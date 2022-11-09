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

public class AstVisitor implements GenericVisitor<Object, Object> {
    @Override
    public Object visit(CompilationUnit n, Object arg) {
        for (BinaryExpr be: n.findAll(BinaryExpr.class)) {
            be.accept(this, null);
        }
        return null;
    }

    @Override
    public Object visit(PackageDeclaration n, Object arg) {
        return null;
    }

    @Override
    public Object visit(TypeParameter n, Object arg) {
        return null;
    }

    @Override
    public Object visit(LineComment n, Object arg) {
        return null;
    }

    @Override
    public Object visit(BlockComment n, Object arg) {
        return null;
    }

    @Override
    public Object visit(ClassOrInterfaceDeclaration n, Object arg) {
        return null;
    }

    @Override
    public Object visit(RecordDeclaration n, Object arg) {
        return null;
    }

    @Override
    public Object visit(CompactConstructorDeclaration n, Object arg) {
        return null;
    }

    @Override
    public Object visit(EnumDeclaration n, Object arg) {
        return null;
    }

    @Override
    public Object visit(EnumConstantDeclaration n, Object arg) {
        return null;
    }

    @Override
    public Object visit(AnnotationDeclaration n, Object arg) {
        return null;
    }

    @Override
    public Object visit(AnnotationMemberDeclaration n, Object arg) {
        return null;
    }

    @Override
    public Object visit(FieldDeclaration n, Object arg) {
        return null;
    }

    @Override
    public Object visit(VariableDeclarator n, Object arg) {
        return null;
    }

    @Override
    public Object visit(ConstructorDeclaration n, Object arg) {
        return null;
    }

    @Override
    public Object visit(MethodDeclaration n, Object arg) {
        return null;
    }

    @Override
    public Object visit(Parameter n, Object arg) {
        return null;
    }

    @Override
    public Object visit(InitializerDeclaration n, Object arg) {
        return null;
    }

    @Override
    public Object visit(JavadocComment n, Object arg) {
        return null;
    }

    @Override
    public Object visit(ClassOrInterfaceType n, Object arg) {
        return null;
    }

    @Override
    public Object visit(PrimitiveType n, Object arg) {
        return null;
    }

    @Override
    public Object visit(ArrayType n, Object arg) {
        return null;
    }

    @Override
    public Object visit(ArrayCreationLevel n, Object arg) {
        return null;
    }

    @Override
    public Object visit(IntersectionType n, Object arg) {
        return null;
    }

    @Override
    public Object visit(UnionType n, Object arg) {
        return null;
    }

    @Override
    public Object visit(VoidType n, Object arg) {
        return null;
    }

    @Override
    public Object visit(WildcardType n, Object arg) {
        return null;
    }

    @Override
    public Object visit(UnknownType n, Object arg) {
        return null;
    }

    @Override
    public Object visit(ArrayAccessExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(ArrayCreationExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(ArrayInitializerExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(AssignExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(BinaryExpr n, Object arg) {
        if (n.getOperator() == BinaryExpr.Operator.DIVIDE) {
            System.out.println(n + " can throw ArithmeticException");
        }
        return null;
    }

    @Override
    public Object visit(CastExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(ClassExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(ConditionalExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(EnclosedExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(FieldAccessExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(InstanceOfExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(StringLiteralExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(IntegerLiteralExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(LongLiteralExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(CharLiteralExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(DoubleLiteralExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(BooleanLiteralExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(NullLiteralExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(MethodCallExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(NameExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(ObjectCreationExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(ThisExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(SuperExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(UnaryExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(VariableDeclarationExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(MarkerAnnotationExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(SingleMemberAnnotationExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(NormalAnnotationExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(MemberValuePair n, Object arg) {
        return null;
    }

    @Override
    public Object visit(ExplicitConstructorInvocationStmt n, Object arg) {
        return null;
    }

    @Override
    public Object visit(LocalClassDeclarationStmt n, Object arg) {
        return null;
    }

    @Override
    public Object visit(LocalRecordDeclarationStmt n, Object arg) {
        return null;
    }

    @Override
    public Object visit(AssertStmt n, Object arg) {
        return null;
    }

    @Override
    public Object visit(BlockStmt n, Object arg) {
        return null;
    }

    @Override
    public Object visit(LabeledStmt n, Object arg) {
        return null;
    }

    @Override
    public Object visit(EmptyStmt n, Object arg) {
        return null;
    }

    @Override
    public Object visit(ExpressionStmt n, Object arg) {
        return null;
    }

    @Override
    public Object visit(SwitchStmt n, Object arg) {
        return null;
    }

    @Override
    public Object visit(SwitchEntry n, Object arg) {
        return null;
    }

    @Override
    public Object visit(BreakStmt n, Object arg) {
        return null;
    }

    @Override
    public Object visit(ReturnStmt n, Object arg) {
        return null;
    }

    @Override
    public Object visit(IfStmt n, Object arg) {
        return null;
    }

    @Override
    public Object visit(WhileStmt n, Object arg) {
        return null;
    }

    @Override
    public Object visit(ContinueStmt n, Object arg) {
        return null;
    }

    @Override
    public Object visit(DoStmt n, Object arg) {
        return null;
    }

    @Override
    public Object visit(ForEachStmt n, Object arg) {
        return null;
    }

    @Override
    public Object visit(ForStmt n, Object arg) {
        return null;
    }

    @Override
    public Object visit(ThrowStmt n, Object arg) {
        return null;
    }

    @Override
    public Object visit(SynchronizedStmt n, Object arg) {
        return null;
    }

    @Override
    public Object visit(TryStmt n, Object arg) {
        return null;
    }

    @Override
    public Object visit(CatchClause n, Object arg) {
        return null;
    }

    @Override
    public Object visit(LambdaExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(MethodReferenceExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(TypeExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(NodeList n, Object arg) {
        return null;
    }

    @Override
    public Object visit(Name n, Object arg) {
        return null;
    }

    @Override
    public Object visit(SimpleName n, Object arg) {
        return null;
    }

    @Override
    public Object visit(ImportDeclaration n, Object arg) {
        return null;
    }

    @Override
    public Object visit(ModuleDeclaration n, Object arg) {
        return null;
    }

    @Override
    public Object visit(ModuleRequiresDirective n, Object arg) {
        return null;
    }

    @Override
    public Object visit(ModuleExportsDirective n, Object arg) {
        return null;
    }

    @Override
    public Object visit(ModuleProvidesDirective n, Object arg) {
        return null;
    }

    @Override
    public Object visit(ModuleUsesDirective n, Object arg) {
        return null;
    }

    @Override
    public Object visit(ModuleOpensDirective n, Object arg) {
        return null;
    }

    @Override
    public Object visit(UnparsableStmt n, Object arg) {
        return null;
    }

    @Override
    public Object visit(ReceiverParameter n, Object arg) {
        return null;
    }

    @Override
    public Object visit(VarType n, Object arg) {
        return null;
    }

    @Override
    public Object visit(Modifier n, Object arg) {
        return null;
    }

    @Override
    public Object visit(SwitchExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(YieldStmt n, Object arg) {
        return null;
    }

    @Override
    public Object visit(TextBlockLiteralExpr n, Object arg) {
        return null;
    }

    @Override
    public Object visit(PatternExpr n, Object arg) {
        return null;
    }
}

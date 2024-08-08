package ntou.cse.soselab.migratetocamelcase;

import org.openrewrite.*;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.TreeVisitingPrinter;
import org.openrewrite.java.tree.J;
import com.google.common.base.CaseFormat;

import java.util.ArrayList;
import java.util.List;

public class MigrateToCamelCase extends Recipe {

    @Override
    public String getDisplayName() {
        return "Migrate to Camel Case";
    }

    @Override
    public String getDescription() {
        return "String to CamelCase migration.";
    }


    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<>() {
          @Override
          public J.CompilationUnit visitCompilationUnit(J.CompilationUnit compilationUnit, ExecutionContext executionContext) {
              // Use TreeVisitingPrinter to print LST
              String outputLst = TreeVisitingPrinter.printTree(getCursor());
              System.out.println(outputLst);
              return super.visitCompilationUnit(compilationUnit, executionContext);
          }
        };
    }

    // JavaIsoVisitor 可以產生與 input 相同的樹，可以對 source code 進行更改
    // ExecutionContext 可在 Visitor 運行中傳遞與共享訊息
    public class MigrateToCamelCaseVisitor extends JavaIsoVisitor<ExecutionContext> {

        @Override
        public J.VariableDeclarations visitVariableDeclarations(J.VariableDeclarations varDecl, ExecutionContext executionContext) {
            J.VariableDeclarations v = super.visitVariableDeclarations(varDecl, executionContext);
            List<J.VariableDeclarations.NamedVariable> updateVariables = new ArrayList<>();

            for (J.VariableDeclarations.NamedVariable variable : varDecl.getVariables()) {
                J.Identifier identifierName = variable.getName(); // 取得 J.Identifier

                String newName = identifierName.getSimpleName();  // 取得 J.Identifier 的名字

                if (!isCamelCase(newName)) {
                    newName = toCamelCase(newName);
                }

                J.Identifier updateIdeName = identifierName.withSimpleName(newName)
                                                           .withPrefix(identifierName.getPrefix())
                                                           .withMarkers(identifierName.getMarkers());

                J.VariableDeclarations.NamedVariable updateVariable = variable.withName(updateIdeName)
                                                                              .withPrefix(variable.getPrefix())
                                                                              .withMarkers(variable.getMarkers())
                                                                              .withType(variable.getType());

                updateVariables.add(updateVariable);
            }

            J.VariableDeclarations updateVariableDeclarations = v.withVariables(updateVariables);

            return updateVariableDeclarations;
        }

        private boolean isCamelCase(String name) {
            String camelCasePattern = "([a-z]+[A-Z]+\\w+)+";
            return name.matches(camelCasePattern);
        }

        private boolean isUpperCamel(String name) {
            String upperCamelPattern = "([A-Z][a-z0-9]+)+";
            return name.matches(upperCamelPattern);
        }

        private String toCamelCase(String name) {
            String converted;

            if (name.contains("-")) {
                converted = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, name);
            } else if (name.contains("_")) {
                converted = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name);
            } else if (isUpperCamel(name)) {
                converted = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, name);
            } else {
                converted = name;
            }

            return converted;
        }

    }
}

package ntou.cse.soselab.migratetocamelcase;

import org.openrewrite.*;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.TreeVisitingPrinter;
import org.openrewrite.java.tree.J;
import com.google.common.base.CaseFormat;

import java.util.*;

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
            public J.VariableDeclarations.NamedVariable visitVariable (J.VariableDeclarations.NamedVariable namedVariable, ExecutionContext executionContext) {
                J.VariableDeclarations.NamedVariable n = super.visitVariable(namedVariable, executionContext);
                String name = n.getName().getSimpleName();
                String newName = "";

                if (!isCamelCase(name)) {
                    newName = toCamelCase(name);
                }

                if (getCursor().getMessage("change", 1) != 0) {
                    System.out.println("pass");
                    J.VariableDeclarations.NamedVariable updatedVariable =
                            n.withName(namedVariable.getName().withSimpleName(newName))
                                    .withVariableType(n.getVariableType().withName(newName));

                    return updatedVariable;
                }
                return super.visitVariable(namedVariable, executionContext);
            }

            @Override
            public J.Literal visitLiteral(J.Literal literal, ExecutionContext executionContext) {
                Object value = literal.getValue();
                if (value instanceof Integer) {
                    Cursor method = getCursor().dropParentUntil(J.VariableDeclarations.NamedVariable.class::isInstance);
                    method.putMessage("change", 1);
                }
                return super.visitLiteral(literal, executionContext);
            }
        };
    }

    // JavaIsoVisitor 可以產生與 input 相同的樹，可以對 source code 進行更改
    // ExecutionContext 可在 Visitor 運行中傳遞與共享訊息
    public boolean isCamelCase(String name) {
        String camelCasePattern = "([a-z]+[A-Z]+\\w+)+";
        return name.matches(camelCasePattern);
    }

    public boolean isUpperCamel(String name) {
        String upperCamelPattern = "([A-Z][a-z0-9]+)+";
        return name.matches(upperCamelPattern);
    }

    public String toCamelCase(String name) {
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

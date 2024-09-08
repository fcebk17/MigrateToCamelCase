package ntou.cse.soselab.migratetocamelcase;

import org.openrewrite.*;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.RenameVariable;
import org.openrewrite.java.TreeVisitingPrinter;
import org.openrewrite.java.tree.J;
import com.google.common.base.CaseFormat;
import org.openrewrite.Cursor;
import org.openrewrite.java.tree.JavaSourceFile;
import org.openrewrite.java.tree.JavaType;

import java.util.*;

import static java.util.Collections.emptyMap;

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
        return new JavaIsoVisitor<ExecutionContext>() {

            @Nullable
            private Cursor sourceFileCursor;

            private Cursor getSourceFileCursor() {
                if (sourceFileCursor == null) {
                    sourceFileCursor = getCursor().getPathAsCursors(c -> c.getValue() instanceof JavaSourceFile).next();
                }
                return sourceFileCursor;
            }
            @Override
            public J.VariableDeclarations visitVariableDeclarations (J.VariableDeclarations variable, ExecutionContext executionContext) {
                J.VariableDeclarations v = super.visitVariableDeclarations(variable, executionContext);

                List<J.VariableDeclarations.NamedVariable> namedVariables = v.getVariables();
                for (J.VariableDeclarations.NamedVariable var : namedVariables) {
                    String name = var.getSimpleName();

                    if (!isCamelCase(name)) {
                        renameVariable(var, toCamelCase(name));
                    }

                    else {
                        hasNameKey(computeKey(name, var));
                    }
                }
                return v;
            }

            @Override
            public @Nullable J postVisit(J tree, ExecutionContext ctx) { // postVisit 會在所有子節點都被訪問後被調用
                if (tree instanceof JavaSourceFile) {
                    JavaSourceFile cu = (JavaSourceFile) tree;
                    Map<J.VariableDeclarations.NamedVariable, String> renameVariablesMap = getCursor().getMessage("RENAME_VARIABLE_KEY", emptyMap());
                    Set<String> hasNameSet = getCursor().computeMessageIfAbsent("HAS_NAME_KEY", k -> new HashSet<>());

                    for (Map.Entry<J.VariableDeclarations.NamedVariable, String> entry : renameVariablesMap.entrySet()) {
                        J.VariableDeclarations.NamedVariable variable = entry.getKey();
                        String newName = entry.getValue();
                        if (shouldRename(hasNameSet, variable, newName)) {
                            cu = (JavaSourceFile) new RenameVariable<>(variable, newName).visitNonNull(cu, ctx); // 遍歷所有節點，將變數名稱改為新名稱
                            // RenameVariable 是一個 OpenRewrite Visitor，用於重命名變數
                            hasNameSet.add(computeKey(newName, variable)); // 添加變數型態與名稱於 set 中
                        }
                    }
                    return cu;
                }
                return super.postVisit(tree, ctx);
            }

            private void renameVariable(J.VariableDeclarations.NamedVariable variable, String newName) { // 重命名變數
                getSourceFileCursor()
                        .computeMessageIfAbsent("RENAME_VARIABLE_KEY", k -> new LinkedHashMap<>())
                        .put(variable, newName);
            }

            private void hasNameKey(String variableName) { // 檢查是否有重複的名稱
                getSourceFileCursor()
                        .computeMessageIfAbsent("HAS_NAME_KEY", k -> new HashSet<>())
                        .add(variableName);
            }

            private boolean shouldRename(Set<String> hasNameSet, J.VariableDeclarations.NamedVariable variable, String toName) { // 檢查是否有重複的名稱
                if (toName.isEmpty() || !Character.isAlphabetic(toName.charAt(0))) {
                    return false;
                }
                return isAvailableIdentifier(toName, variable, hasNameSet);
            }

            private boolean isAvailableIdentifier(String identifier, J context, Set<String> hasNameSet) { // 檢查是否有重複的名稱
                if (hasNameSet.contains(identifier)) {
                    return false;
                }
                JavaType.Variable fieldType = getFieldType(context);
                if (fieldType != null && fieldType.getOwner() != null) {
                    if (hasNameSet.contains(fieldType.getOwner() + " " + identifier)) {
                        return false;
                    }
                    if (fieldType.getOwner() instanceof JavaType.Method) {
                        // Add all enclosing classes
                        JavaType.FullyQualified declaringType = ((JavaType.Method) fieldType.getOwner()).getDeclaringType();
                        while (declaringType != null) {
                            if (hasNameSet.contains(declaringType + " " + identifier)) {
                                return false;
                            }
                            declaringType = declaringType.getOwningClass();
                        }
                    }
                }
                return true;
            }

            private String computeKey(String identifier, J context) { //我要思考一下，感覺是回傳變數型態 + 名稱
                JavaType.Variable fieldType = getFieldType(context);
                if (fieldType != null && fieldType.getOwner() != null) { // int a = 1; 這裡的 fieldType 是 int
                    return fieldType.getOwner() + " " + identifier;
                }
                return identifier;
            }

            private @Nullable JavaType.Variable getFieldType(J tree) { // 取得變數類型
                if (tree instanceof J.Identifier) {
                    return ((J.Identifier) tree).getFieldType();
                }
                if (tree instanceof J.VariableDeclarations.NamedVariable) {
                    return ((J.VariableDeclarations.NamedVariable) tree).getVariableType();
                }
                return null;
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

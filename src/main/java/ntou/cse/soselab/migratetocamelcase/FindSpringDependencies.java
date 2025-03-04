package ntou.cse.soselab.migratetocamelcase;

import org.openrewrite.Recipe;
import org.openrewrite.*;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.tree.J;

import java.util.HashMap;
import java.util.Map;

public class FindSpringDependencies extends Recipe {
    @Override
    public String getDisplayName() {
        return "Find Spring Controllers, Services, and Repositories";
    }

    @Override
    public String getDescription() {
        return "Finds Spring @Controller dependencies and maps them to @Service and @Repository components.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<ExecutionContext>() {
            private final Map<String, String> serviceToRepositoryMap = new HashMap<>();
            private final Map<String, String> controllerToServiceMap = new HashMap<>();

            @Override
            public J.ClassDeclaration visitClassDeclaration(J.ClassDeclaration classDecl, ExecutionContext ctx) {
                // 取得類別名稱
                String className = classDecl.getSimpleName();

                // 檢查是否是 @RestController 或 @Controller
                if (hasAnnotation(classDecl, "org.springframework.web.bind.annotation.RestController") ||
                        hasAnnotation(classDecl, "org.springframework.stereotype.Controller")) {

                    // 遍歷 class 成員變數，找到 @Autowired Service
                    classDecl.getBody().getStatements().stream()
                            .filter(stmt -> stmt instanceof J.VariableDeclarations)
                            .map(stmt -> (J.VariableDeclarations) stmt)
                            .filter(field -> hasAnnotation(field, "org.springframework.beans.factory.annotation.Autowired"))
                            .forEach(field -> {
                                if (!field.getVariables().isEmpty() && field.getVariables().get(0).getType() != null) {
                                    controllerToServiceMap.put(className, field.getVariables().get(0).getType().toString());
                                }
                            });
                }

                // 檢查是否是 @Service
                if (hasAnnotation(classDecl, "org.springframework.stereotype.Service")) {
                    // 遍歷 class 成員變數，找到 @Autowired Repository
                    classDecl.getBody().getStatements().stream()
                            .filter(stmt -> stmt instanceof J.VariableDeclarations)
                            .map(stmt -> (J.VariableDeclarations) stmt)
                            .filter(field -> hasAnnotation(field, "org.springframework.beans.factory.annotation.Autowired"))
                            .forEach(field -> {
                                if (!field.getVariables().isEmpty() && field.getVariables().get(0).getType() != null) {
                                    serviceToRepositoryMap.put(className, field.getVariables().get(0).getType().toString());
                                }
                            });
                }

                return super.visitClassDeclaration(classDecl, ctx);
            }

            @Override
            public J.CompilationUnit visitCompilationUnit(J.CompilationUnit cu, ExecutionContext ctx) {
                J.CompilationUnit unit = super.visitCompilationUnit(cu, ctx);

                // 只在最後一個 Class 被掃描完時輸出結果
                if (!controllerToServiceMap.isEmpty()) {
                    System.out.println("\nSpring Dependency Mapping:");
                    for (Map.Entry<String, String> entry : controllerToServiceMap.entrySet()) {
                        String controller = entry.getKey();
                        String service = entry.getValue();
                        String repository = serviceToRepositoryMap.get(service);

                        System.out.println("Controller: " + controller);
                        System.out.println("  → Service: " + service);
                        System.out.println("    → Repository: " + (repository != null ? repository : "Not Found"));
                        System.out.println("-----------------------------------");
                    }
                }
                return unit;
            }

            private boolean hasAnnotation(J.ClassDeclaration classDecl, String annotation) {
                return classDecl.getLeadingAnnotations().stream()
                        .anyMatch(ann -> ann.getType().toString().equals(annotation));
            }

            private boolean hasAnnotation(J.VariableDeclarations field, String annotation) {
                return field.getLeadingAnnotations().stream()
                        .anyMatch(ann -> ann.getType().toString().equals(annotation));
            }
        };
    }
}

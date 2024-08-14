package ntou.cse.soselab.migratetocamelcase;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.openrewrite.java.Assertions.java;

class MigrateToCamelCaseTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new MigrateToCamelCase());  // 設置要使用的 Recipe
    }

    @Test
    void forTest() {
        rewriteRun(
                java(
                        """
                                class A {
                                    int test_case() {
                                        int hello_world = 21;
                                        return hello_world;
                                    } 
                                }
                                """,
                        """
                                class A {
                                    int test_case() {
                                        int helloWorld = 21;
                                        return helloWorld;
                                    } 
                                }
                                """
                )
        );
    }


}
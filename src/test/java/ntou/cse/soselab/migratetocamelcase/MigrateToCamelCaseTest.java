package ntou.cse.soselab.migratetocamelcase;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.*;

import static org.openrewrite.java.Assertions.java;

class MigrateToCamelCaseTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new MigrateToCamelCase());  // 設置要使用的 Recipe
        // 可以使用 parser
    }
    @Test
    void testCamelCase() {
        rewriteRun(
                java( //groovy
                        """
                                class A {
                                    public void test() {
                                        String hello_World = "hello";
                                        System.out.println("1");
                                    }
                                }
                                """,
                        """
                                class A {
                                    public void test() {
                                        String helloWorld = "hello";
                                        System.out.println("1");
                                    }
                                }
                                """
                )
        );
    }



}

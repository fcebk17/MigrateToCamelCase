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
    void testMethod() {
        rewriteRun(
                java(
                        """
                                class A {
                                    int test_case() {
                                        int helloWorld = 21;
                                        return 1;
                                    }
                                }
                                """,
                        """
                                class A {
                                    int testCase() {
                                        int helloWorld = 21;
                                        return 1;
                                    }
                                }
                                """
                )
        );
    }

//    @Test
//    void testMethod2() {
//        rewriteRun(
//                java(
//                        """
//                                class A {
//                                    String THE_WEATHER() {
//                                        return "sunny";
//                                    }
//                                }
//                                """,
//                        """
//                                class A {
//                                    String theWeather() {
//                                        return "sunny";
//                                    }
//                                }
//                                """
//                )
//        );
//    }
//    @Test
//    void testMethod3() {
//        rewriteRun(
//                java(
//                        """
//                                class B {
//                                    Boolean chatGPT() {
//                                        return "sunny";
//                                    }
//                                }
//                                """,
//                        """
//                                class B {
//                                    Boolean chatGpt() {
//                                        return "sunny";
//                                    }
//                                }
//                                """
//                )
//        );
//    }


}
package ToolSpoon;

import org.junit.runner.notification.Failure;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtPath;
import spoon.reflect.path.CtPathBuilder;
import test.TestRunner;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
public class Collector {

    private Factory factory;

    public Collector(Factory factory) {
        this.factory = factory;
    }


    public void run(CtClass testClass, CtMethod<?> clone) throws IOException {
        final String fullQualifiedName = testClass.getQualifiedName();
        final String testMethodName = clone.getSimpleName();
        String path = testClass.getPosition().getFile().getPath().replace("\\","/");
        try {
            List<Failure> test = TestRunner.runTest(fullQualifiedName, testMethodName, new String[]{path});
            if (test.size() == 0) {
                System.out.println("Unit Test of " + testMethodName + ": SUCCESS");
            } else {
                test.stream().forEach(failure -> System.out.println(failure.toString()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}

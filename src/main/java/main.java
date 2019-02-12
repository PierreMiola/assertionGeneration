import ToolSpoon.Analyzer;
import ToolSpoon.AssertionAdder;
import ToolSpoon.Collector;
import ToolSpoon.Util;
import spoon.Launcher;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtConstructorCallImpl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static ToolSpoon.Util.interfaceTerminal;

public class main {
    public static void main(String[] args) throws IOException {
        String filePath;
        String file;
        String assertMethod;
        int numberAssert;
        if(args.length < 4) {
            args = interfaceTerminal();
        }
        filePath = args[0].replace("\\", "/");
        file = args[1];
        assertMethod = args[2].replace("()", "");
        numberAssert = Integer.valueOf(args[3]);
      
        Launcher spoon = new Launcher();
        spoon.addInputResource(filePath);
        spoon.buildModel();
        Factory factory = spoon.getFactory();
        factory.getEnvironment().setAutoImports(true);
        Analyzer analyzer = new Analyzer();
        Collector collector = new Collector(factory);
        AssertionAdder assertionAdder= new AssertionAdder(factory);

        CtClass<?> ctClassTest = factory.Class().get(file);
        if(ctClassTest == null){
            System.out.println("File don't exist");
            return ;
        }
        CtMethod ctMethodTest = ctClassTest.getMethod(assertMethod);
        if(ctMethodTest == null){
            System.out.println("Method don't exist");
            return;
        }
        Map<CtMethod, List<CtLocalVariable>> localVariables = analyzer.analyze(ctClassTest);
        for(CtMethod ctMethod : localVariables.keySet()){
            CtLocalVariable test = localVariables.get(ctMethod).get(0);
            CtClass<?> ctClass = factory.Class().get(test.getType().toString());
            CtConstructorCallImpl Contructor = test.getElements(new TypeFilter<>(CtConstructorCallImpl.class)).get(0);
            Util.saveArguments(Contructor,ctClass);
            System.out.println(ctMethodTest);
            collector.run(ctClassTest,ctMethodTest);
            System.out.println();
            CtMethod newMethod = assertionAdder.addAssertion(ctMethodTest, localVariables.get(ctMethod),numberAssert);
            System.out.println(newMethod);
            ctClassTest.removeMethod(ctMethodTest);
            ctClassTest.addMethod(newMethod);
            collector.run(ctClassTest,newMethod);
        }

    }

}

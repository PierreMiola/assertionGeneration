package classTest;

import static org.junit.Assert.*;

import org.junit.Test;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import ToolSpoon.Analyzer;
import spoon.Launcher;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;

public class AnalyzerTest {

	@Test
	public void testAnalize1() {
		Analyzer a = new Analyzer();
		Path path = FileSystems.getDefault().getPath(".").toAbsolutePath();
		String filePath = path.toString();
        String file = "classTest.PersonTest";
        String assertMethod = "testAge";
        int numberAssert = 2;
        Launcher spoon = new Launcher();
        spoon.addInputResource(filePath);
        spoon.buildModel();
        Factory factory = spoon.getFactory();
		CtClass<?> c = factory.Class().get(file);
		CtMethod m = c.getMethod(assertMethod);
		List<CtLocalVariable> l = a.analyze(m);
		assertEquals(1, l.size());
	}
	
	@Test
	public void testAnalize2() {
		Analyzer a = new Analyzer();
		Path path = FileSystems.getDefault().getPath(".").toAbsolutePath();
		String filePath = path.toString();String file = "classTest.PersonTest";
        String assertMethod = "testAge";
        int numberAssert = 2;
        Launcher spoon = new Launcher();
        spoon.addInputResource(filePath);
        spoon.buildModel();
        Factory factory = spoon.getFactory();
		CtClass<?> c = factory.Class().get(file);
		Map<CtMethod, List<CtLocalVariable>> l2 = a.analyze(c);
		assertEquals(1, l2.size());
	}

}

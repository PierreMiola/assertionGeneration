package ToolSpoon;

import org.testng.annotations.Test;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class Analyzer {

	public List<CtLocalVariable> analyze(CtMethod testMethod) {
		TypeFilter filterLocalVar =
				new TypeFilter(CtLocalVariable.class) {
					public boolean matches(CtLocalVariable localVariable) {
						return !localVariable.getType().isPrimitive();
					}
				};
		return testMethod.getElements(filterLocalVar);
	}


	public Map<CtMethod, List<CtLocalVariable>> analyze(CtType<?> ctClass) {
		return ctClass.getMethods().stream()
				.collect(Collectors.toMap(
						ctMethod -> ctMethod,
						this::analyze)
				);
	}
}

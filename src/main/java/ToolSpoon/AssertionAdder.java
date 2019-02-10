package ToolSpoon;

import org.junit.Assert;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;

import java.util.List;
import java.util.stream.Collectors;

import static ToolSpoon.Util.*;


public class AssertionAdder {

	private Factory factory;

	public AssertionAdder(Factory factory) {
		this.factory = factory;
	}

	@SuppressWarnings("unchecked")
	public CtMethod addAssertion(CtMethod<?> testMethod, List<CtLocalVariable> ctLocalVariables,int numberAssertions) {
		ctLocalVariables.forEach(ctLocalVariable -> this.addAssertion(testMethod, ctLocalVariable,numberAssertions));
		return testMethod;
	}

	@SuppressWarnings("unchecked")
	void addAssertion(CtMethod testMethod, CtLocalVariable localVariable, int numberAssertions) {
		List<CtMethod> getters =
				getGetters(localVariable);
		getters = filterGetters(getters, testMethod).stream().limit(numberAssertions).collect(Collectors.toList());
		getters.forEach(getter -> {
			CtInvocation invocationToGetter =
					invok(getter, localVariable);
			CtInvocation invocationToAssert = createAssert("assertEquals",
					factory.createLiteral(Logger.remember.get(getter)), // expected
					invocationToGetter); // actual
			testMethod.getBody().insertEnd(invocationToAssert);
		});
	}

	public static CtInvocation createAssert(String name, CtExpression... parameters) {
		final Factory factory = parameters[0].getFactory();
		CtTypeAccess accessToAssert =
				factory.createTypeAccess(factory.createCtTypeReference(Assert.class));
		CtExecutableReference assertEquals = factory.Type().get(Assert.class)
				.getMethodsByName(name).get(0).getReference();
		return factory.createInvocation(
				accessToAssert,
				assertEquals,
				parameters[0],
				parameters[1]
		);
	}

}

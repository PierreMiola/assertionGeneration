package ToolSpoon;

import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.*;
import spoon.support.reflect.reference.CtFieldReferenceImpl;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 26/06/17
 */
public class Util {

	public static void saveArguments(CtConstructorCallImpl contructorClassTest, CtClass classParent){
		List<CtExpression> argumentsConstructorTest = contructorClassTest.getArguments();
		List<CtParameter> argumentsConstructeurParent = null;
		Set<CtConstructor> listConstructorsClassParent = classParent.getConstructors();
		List<CtTypeReference> argumentstype = argumentsConstructorTest.stream().map(ctExpression -> ctExpression.getType()).collect(Collectors.toList());
		Map<List<CtParameter> ,CtConstructor> mapConstructorsParent = listConstructorsClassParent.stream()
				.collect(
						Collectors.toMap(
								ctConstructor -> ctConstructor.getParameters(),
								ctConstructor -> ctConstructor));
		CtConstructor ctConstructorClassParent = null;
		for(List<CtParameter> ctParameterList : mapConstructorsParent.keySet()){
			List<CtTypeReference> argumentstypeConstructorParent = ctParameterList.stream().map(parameter -> parameter.getType()).collect(Collectors.toList());
			if(argumentstypeConstructorParent.equals(argumentstype)) {
				ctConstructorClassParent = mapConstructorsParent.get(ctParameterList);
				argumentsConstructeurParent = ctConstructorClassParent.getParameters();
			}

		}
		Map<String,String> mapParamField = new HashMap<>();

		List<CtAssignment> listElement = ctConstructorClassParent.getElements(new TypeFilter<>(CtAssignment.class));
		for(CtAssignment ctAssignment : listElement){
			CtFieldReferenceImpl fieldReference = ctAssignment.getElements(new TypeFilter<>(CtFieldReferenceImpl.class)).get(0);
			CtVariableReadImpl paramReference= ctAssignment.getElements(new TypeFilter<>(CtVariableReadImpl.class)).get(0);
			mapParamField.put(fieldReference.toString(),paramReference.toString());
		}
		Set<CtMethod> methodsClassParent = classParent.getMethods();

		Map<String,CtMethod> map = new HashMap<>();
		methodsClassParent.stream().forEach(method ->
						filterReturn(method,map,mapParamField));

		for(int i=0; i < argumentsConstructorTest.size() ; i++){
			Logger.setRemember(map.get(argumentsConstructeurParent.get(i).getSimpleName()),argumentsConstructorTest.get(i));
		}
	}

	public static void filterReturn(CtMethod ctMethod, Map<String,CtMethod> map,Map<String,String> mapParamField){
		List<CtReturnImpl> temp = ctMethod.getElements(new TypeFilter<>(CtReturnImpl.class));
		if(temp.size() > 0) {
			List<CtFieldReadImpl> fields = temp.get(0).getElements(new TypeFilter<>(CtFieldReadImpl.class));
			if(fields.size()>0){
				map.put(mapParamField.get(fields.get(0).getVariable().getSimpleName()),ctMethod);
			}

		}
	}

	public static String getKey(CtMethod method) {
		return method.getParent(CtClass.class).getSimpleName() + "#" + method.getSimpleName();
	}

	public static CtInvocation invok(CtMethod method, CtLocalVariable localVariable) {
		final CtExecutableReference reference = method.getReference();
		final CtVariableAccess variableRead = method.getFactory().createVariableRead(localVariable.getReference(), false);
		return method.getFactory().createInvocation(variableRead, reference);
	}

	public static List<CtMethod> getGetters(CtLocalVariable localVariable) {
		return ((Set<CtMethod<?>>) localVariable.getType().getDeclaration().getMethods()).stream()
				.filter(method -> method.getParameters().isEmpty() &&
						method.getType() != localVariable.getFactory().Type().VOID_PRIMITIVE &&
						(method.getSimpleName().startsWith("get") ||
								method.getSimpleName().startsWith("is"))
				).collect(Collectors.toList());
	}
	public static List<CtMethod> filterGetters(List<CtMethod> getters,CtMethod testMethod) {
		List<CtInvocation> ctInvocationList = testMethod.getElements(new TypeFilter(CtInvocation.class));
		List<CtInvocation> gettersAssert = ctInvocationList.stream().filter(ctInvocation -> ctInvocation.getArguments().size() == 0).collect(Collectors.toList());
		List<Method> methodList = gettersAssert.stream().map(ctInvocation -> ctInvocation.getExecutable().getActualMethod()).collect(Collectors.toList());
		return getters.stream().filter(ctMethod -> !methodList.stream().anyMatch(method -> method.getName().equals(ctMethod.getSimpleName()))).collect(Collectors.toList());
	}
}

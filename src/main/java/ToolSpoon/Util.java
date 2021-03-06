package ToolSpoon;

import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.*;
import spoon.support.reflect.reference.CtFieldReferenceImpl;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class Util {
	public static String[] interfaceTerminal(){
		String filePath ="";
		String fileTest="";
		String methodTest="";
		int numberAssertions=0;
		Scanner scanner = new Scanner(System.in);
		boolean exists = false;
		String[] args = new String[4];
		while(filePath.equals("") && exists == false){
			System.out.println("Enter the path to maven project");
			filePath= scanner.next();
			File tmpDir = new File(filePath);
			exists = tmpDir.exists();
			if(!exists){
				System.out.println(filePath + " n'existe pas");
			}
		}
		while(fileTest.equals("")){
			System.out.println("Enter the name of the test class with the package. exemple: SomePackage.testclass");
			fileTest = scanner.next();
		}
		while(methodTest.equals("")){
			System.out.println("Enter the name of the method to be tested ex: testAge");
			methodTest= scanner.next();
		}
		while(numberAssertions == 0){
			System.out.println("Enter the number of assertion");
			numberAssertions= scanner.nextInt();
		}
		args[0]=filePath;
		args[1]=fileTest;
		args[2]=methodTest;
		args[3]=String.valueOf(numberAssertions);
		return args;
	}
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
			if(!(temp.get(0).getReturnedExpression() instanceof CtBinaryOperatorImpl)) {
				List<CtFieldReadImpl> fields = temp.get(0).getElements(new TypeFilter<>(CtFieldReadImpl.class));
				if (fields.size() > 0) {
					map.put(mapParamField.get(fields.get(0).getVariable().getSimpleName()), ctMethod);
				}
			}
		}
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

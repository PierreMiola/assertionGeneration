package ToolSpoon;

import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.support.reflect.code.CtLiteralImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 21/06/17
 */
public class Logger {

	public static Map<String, Object> observations = new HashMap<String, Object>();
	public static Map<CtMethod, CtExpression> remember = new HashMap<CtMethod, CtExpression>();
	public static void observe(String name, Object object) {
		observations.put(name, object);
	}
	public static void setRemember(CtMethod method, CtExpression parameter){
		remember.put(method,parameter);
	}
}

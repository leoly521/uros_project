package com.bamboocloud.uros.server;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.bamboocloud.uros.common.UrosResultMode;

public class UrosMethods {

	protected ConcurrentHashMap<String, ConcurrentHashMap<Integer, UrosMethod>> remoteMethods = new ConcurrentHashMap<String, ConcurrentHashMap<Integer, UrosMethod>>();
	protected ConcurrentHashMap<String, String> methodNames = new ConcurrentHashMap<String, String>();

	public UrosMethods() {
	}

	public UrosMethod get(String aliasName, int paramCount) {
		ConcurrentHashMap<Integer, UrosMethod> methods = this.remoteMethods
				.get(aliasName);
		if (methods == null) {
			return null;
		}
		return methods.get(paramCount);
	}

	public Collection<String> getAllNames() {
		return this.methodNames.values();
	}

	public int getCount() {
		return this.remoteMethods.size();
	}

	protected int getCount(Type[] paramTypes) {
		return paramTypes.length;
	}

	void addMethod(String aliasName, UrosMethod method) {
		ConcurrentHashMap<Integer, UrosMethod> methods;
		String name = aliasName.toLowerCase();
		if (this.remoteMethods.containsKey(name)) {
			methods = this.remoteMethods.get(name);
		} else {
			methods = new ConcurrentHashMap<Integer, UrosMethod>();
			this.methodNames.put(name, aliasName);
		}
		if (aliasName.equals("*")
				&& (!((method.paramTypes.length == 2)
						&& method.paramTypes[0].equals(String.class) && method.paramTypes[1]
							.equals(Object[].class)))) {
			return;
		}
		int i = getCount(method.paramTypes);
		methods.put(i, method);
		this.remoteMethods.put(name, methods);
	}

	public void addMethod(Method method, Object obj, String aliasName) {
		this.addMethod(aliasName, new UrosMethod(method, obj));
	}

	public void addMethod(Method method, Object obj, String aliasName,
			UrosResultMode mode) {
		this.addMethod(aliasName, new UrosMethod(method, obj, mode));
	}

	public void addMethod(Method method, Object obj, String aliasName,
			boolean simple) {
		this.addMethod(aliasName, new UrosMethod(method, obj, simple));
	}

	public void addMethod(Method method, Object obj, String aliasName,
			UrosResultMode mode, boolean simple) {
		this.addMethod(aliasName, new UrosMethod(method, obj, mode, simple));
	}

	public void addMethod(String methodName, Object obj, Class<?>[] paramTypes,
			String aliasName) throws NoSuchMethodException {
		this.addMethod(aliasName, new UrosMethod(methodName, obj, paramTypes));
	}

	public void addMethod(String methodName, Object obj, Class<?>[] paramTypes,
			String aliasName, UrosResultMode mode) throws NoSuchMethodException {
		this.addMethod(aliasName, new UrosMethod(methodName, obj, paramTypes, mode));
	}

	public void addMethod(String methodName, Object obj, Class<?>[] paramTypes,
			String aliasName, boolean simple) throws NoSuchMethodException {
		this.addMethod(aliasName,
				new UrosMethod(methodName, obj, paramTypes, simple));
	}

	public void addMethod(String methodName, Object obj, Class<?>[] paramTypes,
			String aliasName, UrosResultMode mode, boolean simple)
			throws NoSuchMethodException {
		this.addMethod(aliasName, new UrosMethod(methodName, obj, paramTypes, mode,
				simple));
	}

	public void addMethod(String methodName, Class<?> type,
			Class<?>[] paramTypes, String aliasName)
			throws NoSuchMethodException {
		this.addMethod(aliasName, new UrosMethod(methodName, type, paramTypes));
	}

	public void addMethod(String methodName, Class<?> type,
			Class<?>[] paramTypes, String aliasName, UrosResultMode mode)
			throws NoSuchMethodException {
		this.addMethod(aliasName, new UrosMethod(methodName, type, paramTypes, mode));
	}

	public void addMethod(String methodName, Class<?> type,
			Class<?>[] paramTypes, String aliasName, boolean simple)
			throws NoSuchMethodException {
		this.addMethod(aliasName, new UrosMethod(methodName, type, paramTypes,
				simple));
	}

	public void addMethod(String methodName, Class<?> type,
			Class<?>[] paramTypes, String aliasName, UrosResultMode mode,
			boolean simple) throws NoSuchMethodException {
		this.addMethod(aliasName, new UrosMethod(methodName, type, paramTypes, mode,
				simple));
	}

	public void addMethod(String methodName, Object obj, Class<?>[] paramTypes)
			throws NoSuchMethodException {
		this.addMethod(methodName, new UrosMethod(methodName, obj, paramTypes));
	}

	public void addMethod(String methodName, Object obj, Class<?>[] paramTypes,
			UrosResultMode mode) throws NoSuchMethodException {
		this.addMethod(methodName, new UrosMethod(methodName, obj, paramTypes, mode));
	}

	public void addMethod(String methodName, Object obj, Class<?>[] paramTypes,
			boolean simple) throws NoSuchMethodException {
		this.addMethod(methodName, new UrosMethod(methodName, obj, paramTypes,
				simple));
	}

	public void addMethod(String methodName, Object obj, Class<?>[] paramTypes,
			UrosResultMode mode, boolean simple) throws NoSuchMethodException {
		this.addMethod(methodName, new UrosMethod(methodName, obj, paramTypes, mode,
				simple));
	}

	public void addMethod(String methodName, Class<?> type,
			Class<?>[] paramTypes) throws NoSuchMethodException {
		this.addMethod(methodName, new UrosMethod(methodName, type, paramTypes));
	}

	public void addMethod(String methodName, Class<?> type,
			Class<?>[] paramTypes, UrosResultMode mode)
			throws NoSuchMethodException {
		this.addMethod(methodName,
				new UrosMethod(methodName, type, paramTypes, mode));
	}

	public void addMethod(String methodName, Class<?> type,
			Class<?>[] paramTypes, boolean simple) throws NoSuchMethodException {
		this.addMethod(methodName, new UrosMethod(methodName, type, paramTypes,
				simple));
	}

	public void addMethod(String methodName, Class<?> type,
			Class<?>[] paramTypes, UrosResultMode mode, boolean simple)
			throws NoSuchMethodException {
		this.addMethod(methodName, new UrosMethod(methodName, type, paramTypes,
				mode, simple));
	}

	private void addMethod(String methodName, Object obj, Class<?> type,
			String aliasName, UrosResultMode mode, boolean simple) {
		Method[] methods = type.getMethods();
		for (Method method : methods) {
			if (methodName.equals(method.getName())
					&& ((obj == null) == Modifier.isStatic(method
							.getModifiers()))) {
				this.addMethod(aliasName, new UrosMethod(method, obj, mode, simple));
			}
		}
	}

	private void addMethod(String methodName, Object obj, Class<?> type,
			String aliasName) {
		this.addMethod(methodName, obj, type, aliasName, UrosResultMode.Normal,
				false);
	}

	public void addMethod(String methodName, Object obj, String aliasName) {
		this.addMethod(methodName, obj, obj.getClass(), aliasName);
	}

	public void addMethod(String methodName, Object obj, String aliasName,
			UrosResultMode mode) {
		this.addMethod(methodName, obj, obj.getClass(), aliasName, mode, false);
	}

	public void addMethod(String methodName, Object obj, String aliasName,
			boolean simple) {
		this.addMethod(methodName, obj, obj.getClass(), aliasName,
				UrosResultMode.Normal, simple);
	}

	public void addMethod(String methodName, Object obj, String aliasName,
			UrosResultMode mode, boolean simple) {
		this.addMethod(methodName, obj, obj.getClass(), aliasName, mode, simple);
	}

	public void addMethod(String methodName, Class<?> type, String aliasName) {
		this.addMethod(methodName, null, type, aliasName);
	}

	public void addMethod(String methodName, Class<?> type, String aliasName,
			UrosResultMode mode) {
		this.addMethod(methodName, null, type, aliasName, mode, false);
	}

	public void addMethod(String methodName, Class<?> type, String aliasName,
			boolean simple) {
		this.addMethod(methodName, null, type, aliasName, UrosResultMode.Normal,
				simple);
	}

	public void addMethod(String methodName, Class<?> type, String aliasName,
			UrosResultMode mode, boolean simple) {
		this.addMethod(methodName, null, type, aliasName, mode, simple);
	}

	public void addMethod(String methodName, Object obj) {
		this.addMethod(methodName, obj, methodName);
	}

	public void addMethod(String methodName, Object obj, UrosResultMode mode) {
		this.addMethod(methodName, obj, methodName, mode, false);
	}

	public void addMethod(String methodName, Object obj, boolean simple) {
		this.addMethod(methodName, obj, methodName, UrosResultMode.Normal, simple);
	}

	public void addMethod(String methodName, Object obj, UrosResultMode mode,
			boolean simple) {
		this.addMethod(methodName, obj, methodName, mode, simple);
	}

	public void addMethod(String methodName, Class<?> type) {
		this.addMethod(methodName, type, methodName);
	}

	public void addMethod(String methodName, Class<?> type, UrosResultMode mode) {
		this.addMethod(methodName, type, methodName, mode, false);
	}

	public void addMethod(String methodName, Class<?> type, boolean simple) {
		this.addMethod(methodName, type, methodName, UrosResultMode.Normal, simple);
	}

	public void addMethod(String methodName, Class<?> type,
			UrosResultMode mode, boolean simple) {
		this.addMethod(methodName, type, methodName, mode, simple);
	}

	private void addMethods(String[] methodNames, Object obj, Class<?> type,
			String[] aliasNames, UrosResultMode mode, boolean simple) {
		Method[] methods = type.getMethods();
		for (int i = 0; i < methodNames.length; ++i) {
			String methodName = methodNames[i];
			String aliasName = aliasNames[i];
			for (Method method : methods) {
				if (methodName.equals(method.getName())
						&& ((obj == null) == Modifier.isStatic(method
								.getModifiers()))) {
					this.addMethod(aliasName, new UrosMethod(method, obj, mode,
							simple));
				}
			}
		}
	}

	private void addMethods(String[] methodNames, Object obj, Class<?> type,
			String[] aliasNames) {
		addMethods(methodNames, obj, type, aliasNames, UrosResultMode.Normal,
				false);
	}

	private void addMethods(String[] methodNames, Object obj, Class<?> type,
			String aliasPrefix, UrosResultMode mode, boolean simple) {
		String[] aliasNames = new String[methodNames.length];
		for (int i = 0; i < methodNames.length; ++i) {
			aliasNames[i] = aliasPrefix + "_" + methodNames[i];
		}
		addMethods(methodNames, obj, type, aliasNames, mode, simple);
	}

	private void addMethods(String[] methodNames, Object obj, Class<?> type,
			String aliasPrefix) {
		addMethods(methodNames, obj, type, aliasPrefix, UrosResultMode.Normal,
				false);
	}

	private void addMethods(String[] methodNames, Object obj, Class<?> type,
			UrosResultMode mode, boolean simple) {
		addMethods(methodNames, obj, type, methodNames, mode, simple);
	}

	private void addMethods(String[] methodNames, Object obj, Class<?> type) {
		addMethods(methodNames, obj, type, methodNames, UrosResultMode.Normal,
				false);
	}

	public void addMethods(String[] methodNames, Object obj, String[] aliasNames) {
		addMethods(methodNames, obj, obj.getClass(), aliasNames);
	}

	public void addMethods(String[] methodNames, Object obj,
			String[] aliasNames, UrosResultMode mode) {
		addMethods(methodNames, obj, obj.getClass(), aliasNames, mode, false);
	}

	public void addMethods(String[] methodNames, Object obj,
			String[] aliasNames, boolean simple) {
		addMethods(methodNames, obj, obj.getClass(), aliasNames,
				UrosResultMode.Normal, simple);
	}

	public void addMethods(String[] methodNames, Object obj,
			String[] aliasNames, UrosResultMode mode, boolean simple) {
		addMethods(methodNames, obj, obj.getClass(), aliasNames, mode, simple);
	}

	public void addMethods(String[] methodNames, Object obj, String aliasPrefix) {
		addMethods(methodNames, obj, obj.getClass(), aliasPrefix);
	}

	public void addMethods(String[] methodNames, Object obj,
			String aliasPrefix, UrosResultMode mode) {
		addMethods(methodNames, obj, obj.getClass(), aliasPrefix, mode, false);
	}

	public void addMethods(String[] methodNames, Object obj,
			String aliasPrefix, boolean simple) {
		addMethods(methodNames, obj, obj.getClass(), aliasPrefix,
				UrosResultMode.Normal, simple);
	}

	public void addMethods(String[] methodNames, Object obj,
			String aliasPrefix, UrosResultMode mode, boolean simple) {
		addMethods(methodNames, obj, obj.getClass(), aliasPrefix, mode, simple);
	}

	public void addMethods(String[] methodNames, Object obj) {
		addMethods(methodNames, obj, obj.getClass());
	}

	public void addMethods(String[] methodNames, Object obj, UrosResultMode mode) {
		addMethods(methodNames, obj, obj.getClass(), mode, false);
	}

	public void addMethods(String[] methodNames, Object obj, boolean simple) {
		addMethods(methodNames, obj, obj.getClass(), UrosResultMode.Normal,
				simple);
	}

	public void addMethods(String[] methodNames, Object obj,
			UrosResultMode mode, boolean simple) {
		addMethods(methodNames, obj, obj.getClass(), mode, simple);
	}

	public void addMethods(String[] methodNames, Class<?> type,
			String[] aliasNames) {
		addMethods(methodNames, null, type, aliasNames);
	}

	public void addMethods(String[] methodNames, Class<?> type,
			String[] aliasNames, UrosResultMode mode) {
		addMethods(methodNames, null, type, aliasNames, mode, false);
	}

	public void addMethods(String[] methodNames, Class<?> type,
			String[] aliasNames, boolean simple) {
		addMethods(methodNames, null, type, aliasNames, UrosResultMode.Normal,
				simple);
	}

	public void addMethods(String[] methodNames, Class<?> type,
			String[] aliasNames, UrosResultMode mode, boolean simple) {
		addMethods(methodNames, null, type, aliasNames, mode, simple);
	}

	public void addMethods(String[] methodNames, Class<?> type,
			String aliasPrefix) {
		addMethods(methodNames, null, type, aliasPrefix);
	}

	public void addMethods(String[] methodNames, Class<?> type,
			String aliasPrefix, UrosResultMode mode) {
		addMethods(methodNames, null, type, aliasPrefix, mode, false);
	}

	public void addMethods(String[] methodNames, Class<?> type,
			String aliasPrefix, boolean simple) {
		addMethods(methodNames, null, type, aliasPrefix, UrosResultMode.Normal,
				simple);
	}

	public void addMethods(String[] methodNames, Class<?> type,
			String aliasPrefix, UrosResultMode mode, boolean simple) {
		addMethods(methodNames, null, type, aliasPrefix, mode, simple);
	}

	public void addMethods(String[] methodNames, Class<?> type) {
		addMethods(methodNames, null, type);
	}

	public void addMethods(String[] methodNames, Class<?> type,
			UrosResultMode mode) {
		addMethods(methodNames, null, type, mode, false);
	}

	public void addMethods(String[] methodNames, Class<?> type, boolean simple) {
		addMethods(methodNames, null, type, UrosResultMode.Normal, simple);
	}

	public void addMethods(String[] methodNames, Class<?> type,
			UrosResultMode mode, boolean simple) {
		addMethods(methodNames, null, type, mode, simple);
	}

	public void addInstanceMethods(Object obj, Class<?> type,
			String aliasPrefix, UrosResultMode mode, boolean simple) {
		if (obj != null) {
			Method[] methods = type.getDeclaredMethods();
			for (Method method : methods) {
				int mod = method.getModifiers();
				if (Modifier.isPublic(mod) && !Modifier.isStatic(mod)) {
					this.addMethod(method, obj,
							aliasPrefix + "_" + method.getName(), mode, simple);
				}
			}
		}
	}

	public void addInstanceMethods(Object obj, Class<?> type,
			String aliasPrefix, boolean simple) {
		addInstanceMethods(obj, type, aliasPrefix, UrosResultMode.Normal,
				simple);
	}

	public void addInstanceMethods(Object obj, Class<?> type,
			String aliasPrefix, UrosResultMode mode) {
		addInstanceMethods(obj, type, aliasPrefix, mode, false);
	}

	public void addInstanceMethods(Object obj, Class<?> type, String aliasPrefix) {
		addInstanceMethods(obj, type, aliasPrefix, UrosResultMode.Normal, false);
	}

	public void addInstanceMethods(Object obj, Class<?> type,
			UrosResultMode mode, boolean simple) {
		if (obj != null) {
			Method[] methods = type.getDeclaredMethods();
			for (Method method : methods) {
				int mod = method.getModifiers();
				if (Modifier.isPublic(mod) && !Modifier.isStatic(mod)) {
					this.addMethod(method, obj, method.getName(), mode, simple);
				}
			}
		}
	}

	public void addInstanceMethods(Object obj, Class<?> type, boolean simple) {
		addInstanceMethods(obj, type, UrosResultMode.Normal, simple);
	}

	public void addInstanceMethods(Object obj, Class<?> type,
			UrosResultMode mode) {
		addInstanceMethods(obj, type, mode, false);
	}

	public void addInstanceMethods(Object obj, Class<?> type) {
		addInstanceMethods(obj, type, UrosResultMode.Normal, false);
	}

	public void addInstanceMethods(Object obj, String aliasPrefix) {
		addInstanceMethods(obj, obj.getClass(), aliasPrefix);
	}

	public void addInstanceMethods(Object obj, String aliasPrefix,
			UrosResultMode mode) {
		addInstanceMethods(obj, obj.getClass(), aliasPrefix, mode);
	}

	public void addInstanceMethods(Object obj, String aliasPrefix,
			boolean simple) {
		addInstanceMethods(obj, obj.getClass(), aliasPrefix, simple);
	}

	public void addInstanceMethods(Object obj, String aliasPrefix,
			UrosResultMode mode, boolean simple) {
		addInstanceMethods(obj, obj.getClass(), aliasPrefix, mode, simple);
	}

	public void addInstanceMethods(Object obj) {
		addInstanceMethods(obj, obj.getClass());
	}

	public void addInstanceMethods(Object obj, UrosResultMode mode) {
		addInstanceMethods(obj, obj.getClass(), mode);
	}

	public void addInstanceMethods(Object obj, boolean simple) {
		addInstanceMethods(obj, obj.getClass(), simple);
	}

	public void addInstanceMethods(Object obj, UrosResultMode mode,
			boolean simple) {
		addInstanceMethods(obj, obj.getClass(), mode, simple);
	}

	public void addStaticMethods(Class<?> type, String aliasPrefix,
			UrosResultMode mode, boolean simple) {
		Method[] methods = type.getDeclaredMethods();
		for (Method method : methods) {
			int mod = method.getModifiers();
			if (Modifier.isPublic(mod) && Modifier.isStatic(mod)) {
				this.addMethod(method, null, aliasPrefix + "_" + method.getName(),
						mode, simple);
			}
		}
	}

	public void addStaticMethods(Class<?> type, String aliasPrefix,
			boolean simple) {
		addStaticMethods(type, aliasPrefix, UrosResultMode.Normal, simple);
	}

	public void addStaticMethods(Class<?> type, String aliasPrefix,
			UrosResultMode mode) {
		addStaticMethods(type, aliasPrefix, mode, false);
	}

	public void addStaticMethods(Class<?> type, String aliasPrefix) {
		addStaticMethods(type, aliasPrefix, UrosResultMode.Normal, false);
	}

	public void addStaticMethods(Class<?> type, UrosResultMode mode,
			boolean simple) {
		Method[] methods = type.getDeclaredMethods();
		for (Method method : methods) {
			int mod = method.getModifiers();
			if (Modifier.isPublic(mod) && Modifier.isStatic(mod)) {
				this.addMethod(method, null, method.getName(), mode, simple);
			}
		}
	}

	public void addStaticMethods(Class<?> type, boolean simple) {
		addStaticMethods(type, UrosResultMode.Normal, simple);
	}

	public void addStaticMethods(Class<?> type, UrosResultMode mode) {
		addStaticMethods(type, mode, false);
	}

	public void addStaticMethods(Class<?> type) {
		addStaticMethods(type, UrosResultMode.Normal, false);
	}

	public void addMissingMethod(String methodName, Object obj)
			throws NoSuchMethodException {
		this.addMethod(methodName, obj, new Class<?>[] { String.class,
				Object[].class }, "*");
	}

	public void addMissingMethod(String methodName, Object obj,
			UrosResultMode mode) throws NoSuchMethodException {
		this.addMethod(methodName, obj, new Class<?>[] { String.class,
				Object[].class }, "*", mode);
	}

	public void addMissingMethod(String methodName, Object obj, boolean simple)
			throws NoSuchMethodException {
		this.addMethod(methodName, obj, new Class<?>[] { String.class,
				Object[].class }, "*", simple);
	}

	public void addMissingMethod(String methodName, Object obj,
			UrosResultMode mode, boolean simple) throws NoSuchMethodException {
		this.addMethod(methodName, obj, new Class<?>[] { String.class,
				Object[].class }, "*", mode, simple);
	}

	public void addMissingMethod(String methodName, Class<?> type)
			throws NoSuchMethodException {
		this.addMethod(methodName, type, new Class<?>[] { String.class,
				Object[].class }, "*");
	}

	public void addMissingMethod(String methodName, Class<?> type,
			UrosResultMode mode) throws NoSuchMethodException {
		this.addMethod(methodName, type, new Class<?>[] { String.class,
				Object[].class }, "*", mode);
	}

	public void addMissingMethod(String methodName, Class<?> type,
			boolean simple) throws NoSuchMethodException {
		this.addMethod(methodName, type, new Class<?>[] { String.class,
				Object[].class }, "*", simple);
	}

	public void addMissingMethod(String methodName, Class<?> type,
			UrosResultMode mode, boolean simple) throws NoSuchMethodException {
		this.addMethod(methodName, type, new Class<?>[] { String.class,
				Object[].class }, "*", mode, simple);
	}

}

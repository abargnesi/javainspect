/*
 * JavaInspect - Utility to visualize java software
 * Copyright (C) 2013-2014, Svjatoslav Agejenko, svjatoslav@svjatoslav.eu
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 3 of the GNU Lesser General Public License
 * or later as published by the Free Software Foundation.
 */

package eu.svjatoslav.inspector.java.structure;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MethodDescriptor implements GraphElement,
		Comparable<MethodDescriptor> {

	/**
	 * This class corresponds to single method within a java class.
	 */

	public String name;
	public ClassDescriptor returnType;
	private final ClassDescriptor parentClass;

	List<ClassDescriptor> argumentTypes = new ArrayList<ClassDescriptor>();

	public MethodDescriptor(final Method method, final ClassDescriptor parent,
			final ClassGraph dump) {

		parentClass = parent;

		name = method.getName();

		if (!method.getDeclaringClass().getName()
				.equals(parent.fullyQualifiedName))
			// do not index inherited methods
			return;

		parent.methods.add(this);

		returnType = dump.addClass(method.getReturnType());
		returnType.registerReference();

		final Type genericType = method.getGenericReturnType();
		if (genericType instanceof ParameterizedType) {
			final ParameterizedType pt = (ParameterizedType) genericType;
			for (final Type t : pt.getActualTypeArguments())
				if (t instanceof Class) {
					final Class cl = (Class) t;
					final ClassDescriptor classDescriptor = dump.addClass(cl);
					classDescriptor.registerReference();
					argumentTypes.add(classDescriptor);
				}

		}
	}

	@Override
	public int compareTo(final MethodDescriptor o) {

		final int nameComparisonResult = name.compareTo(o.name);
		if (nameComparisonResult != 0)
			return nameComparisonResult;

		return toString().compareTo(o.toString());
	}

	@Override
	public String getDot() {

		if (!isVisible())
			return "";

		final StringBuffer result = new StringBuffer();

		// describe associated types
		for (final ClassDescriptor classDescriptor : argumentTypes)
			if (classDescriptor.isVisible())
				if (classDescriptor.areReferencesShown())
					result.append("    " + getGraphId() + " -> "
							+ classDescriptor.getGraphId() + "[label=\"" + name
							+ "\", color=\"" + classDescriptor.getColor()
							+ "\", style=\"dotted, bold\"];\n");

		if (!returnType.isVisible())
			return result.toString();

		// main type
		if (returnType.areReferencesShown())
			result.append("    " + getGraphId() + " -> "
					+ returnType.getGraphId() + "[label=\"" + name + "\","
					+ " color=\"" + returnType.getColor()
					+ "\", style=\"dotted, bold\"];\n");

		return result.toString();
	}

	@Override
	public String getEmbeddedDot() {
		if (!isVisible())
			return "";

		final StringBuffer result = new StringBuffer();

		result.append("        // " + name + "\n");

		result.append("        <TR><td ALIGN=\"right\">"
				+ "<FONT POINT-SIZE=\"8.0\">" + returnType.getClassName(true)
				+ "</FONT>" + "</td><TD PORT=\"" + getMethodLabel()
				+ "\" ALIGN=\"left\"><FONT COLOR =\"red\" POINT-SIZE=\"11.0\">"
				+ getMethodLabel() + "</FONT></TD></TR>\n");

		return result.toString();
	}

	@Override
	public String getGraphId() {
		return parentClass.getGraphId() + ":" + name;
	}

	public String getMethodLabel() {
		return name;
	}

	public int getOutsideVisibleReferencesCount() {
		int result = 0;

		if (returnType.isVisible())
			result++;

		for (final ClassDescriptor classDescriptor : argumentTypes)
			if (classDescriptor.isVisible())
				result++;

		return result;
	}

	@Override
	public boolean isVisible() {

		// hide common object methods
		if (Utils.isCommonObjectMethod(name))
			return false;

		// hide common Enumeration methods
		if (parentClass.isEnum && Utils.isEnumMethod(name))
			return false;

		// hide get/set methods for the field of the same name
		if (name.startsWith("get") || name.startsWith("set"))
			if (parentClass.hasFieldIgnoreCase(name.substring(3)))
				return false;

		// hide is methods for the boolean field of the same name
		if (name.startsWith("is")) {
			final FieldDescriptor field = parentClass.getFieldIgnoreCase(name
					.substring(2));
			if (field != null)
				if ("boolean".equals(field.getType().fullyQualifiedName))
					return false;
		}

		return true;

	}

}

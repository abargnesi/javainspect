/*
 * JavaInspect - Utility to visualize java software
 * Copyright (C) 2013, Svjatoslav Agejenko, svjatoslav@svjatoslav.eu
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
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
	private final ClassDescriptor parent;

	List<ClassDescriptor> typeArguments = new ArrayList<ClassDescriptor>();

	public MethodDescriptor(final Method method, final ClassDescriptor parent,
			final ClassGraph dump) {

		this.parent = parent;

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
					typeArguments.add(classDescriptor);
				}

		}
	}

	@Override
	public String getDot() {

		if (!isVisible())
			return "";

		final StringBuffer result = new StringBuffer();

		// describe associated types
		for (final ClassDescriptor classDescriptor : typeArguments)
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
		return parent.getGraphId() + ":" + name;
	}

	public String getMethodLabel() {
		return name;
	}

	public int getOutsideVisibleReferencesCount() {
		int result = 0;

		if (returnType.isVisible())
			result++;

		for (final ClassDescriptor classDescriptor : typeArguments)
			if (classDescriptor.isVisible())
				result++;

		return result;
	}

	@Override
	public boolean isVisible() {

		if (Utils.isSystemMethod(name))
			return false;

		if (parent.isEnum && Utils.isEnumMethod(name))
			return false;

		if (!(name.startsWith("get") || name.startsWith("set")))
			return true;

		final String upprCaseName = name.substring(3).toUpperCase();

		for (String parentField : parent.nameToFieldMap.keySet()) {
			parentField = parentField.toUpperCase();

			if (upprCaseName.equals(parentField))
				return false;

		}

		return true;
	}

	@Override
	public int compareTo(MethodDescriptor o) {

		int nameComparisonResult = name.compareTo(o.name);
		if (nameComparisonResult != 0)
			return nameComparisonResult;

		return toString().compareTo(o.toString());
	}

}

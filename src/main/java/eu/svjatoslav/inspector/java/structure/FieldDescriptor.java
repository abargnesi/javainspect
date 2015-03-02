/*
 * JavaInspect - Utility to visualize java software
 * Copyright (C) 2013-2015, Svjatoslav Agejenko, svjatoslav@svjatoslav.eu
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 3 of the GNU Lesser General Public License
 * or later as published by the Free Software Foundation.
 */

package eu.svjatoslav.inspector.java.structure;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FieldDescriptor implements GraphElement {

	/**
	 * This class corresponds to single field within a java class.
	 */

	public String name;
	private ClassDescriptor type;
	private ClassDescriptor parentClass;
	List<ClassDescriptor> typeArguments = new ArrayList<ClassDescriptor>();

	public FieldDescriptor(final Field field, final ClassDescriptor parent,
			final ClassGraph classGraph) {

		parentClass = parent;

		if (!field.getDeclaringClass().getName()
				.equals(parent.classFullyQualifiedName))
			// if field is inherited, do not index it
			return;

		// if (field.getType().isArray())
		// System.out.println("field name: " + field.getName());

		parent.nameToFieldMap.put(field.getName(), this);

		name = field.getName();
		type = classGraph.addClass(field.getType());
		type.registerReference();

		final Type genericType = field.getGenericType();
		if (genericType instanceof ParameterizedType) {
			final ParameterizedType pt = (ParameterizedType) genericType;
			for (final Type t : pt.getActualTypeArguments())
				if (t instanceof Class) {
					final Class cl = (Class) t;
					final ClassDescriptor genericTypeDescriptor = classGraph
							.addClass(cl);
					genericTypeDescriptor.registerReference();
					typeArguments.add(genericTypeDescriptor);
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
							+ "\", style=\"bold\"];\n");

		if (!type.isVisible())
			return result.toString();

		// main type
		boolean showLink = type.areReferencesShown();

		if (type == parentClass)
			showLink = false;

		if (parentClass.isEnum)
			showLink = false;

		if (showLink)
			result.append("    " + getGraphId() + " -> " + type.getGraphId()
					+ "[label=\"" + name + "\"," + " color=\""
					+ type.getColor() + "\", style=\"bold\"];\n");

		return result.toString();
	}

	@Override
	public String getEmbeddedDot() {

		if (!isVisible())
			return "";

		final StringBuffer result = new StringBuffer();

		result.append("        // " + name + "\n");
		if (parentClass.isEnum && (type == parentClass)) {
			result.append("        <TR><TD colspan=\"2\" PORT=\"" + name);
			result.append("\" ALIGN=\"left\"><FONT POINT-SIZE=\"11.0\">");
			result.append(name + "</FONT></TD></TR>\n");
		} else {
			result.append("        <TR><td ALIGN=\"right\">");
			result.append("<FONT POINT-SIZE=\"8.0\">");
			result.append(type.getClassName(true) + "</FONT>");
			result.append("</td><TD PORT=\"" + name);
			result.append("\" ALIGN=\"left\"><FONT POINT-SIZE=\"11.0\">");
			result.append(name + "</FONT></TD></TR>\n");
		}
		return result.toString();
	}

	@Override
	public String getGraphId() {
		return parentClass.getGraphId() + ":" + name;
	}

	public int getOutsideVisibleReferencesCount() {

		if (!isVisible())
			return 0;

		if (type != null)
			if (type.isVisible())
				return 1;

		return 0;
	}

	public ClassDescriptor getType() {
		return type;
	}

	@Override
	public boolean isVisible() {
		if (name.contains("$"))
			return false;

		if (name.equals("serialVersionUID"))
			return false;

		return true;
	}

}
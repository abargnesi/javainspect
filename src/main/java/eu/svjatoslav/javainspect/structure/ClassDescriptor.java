/*
 * JavaInspect - Utility to visualize java software
 * Copyright (C) 2013, Svjatoslav Agejenko, svjatoslav@svjatoslav.eu
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
 */

package eu.svjatoslav.javainspect.structure;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Describes single class instance
 */
public class ClassDescriptor implements GraphElement {

	private static final int MAX_REFERECNES_COUNT = 10;

	public final String fullyQualifiedName;

	Map<String, FieldDescriptor> nameToFieldMap = new HashMap<String, FieldDescriptor>();

	public List<MethodDescriptor> methods = new ArrayList<MethodDescriptor>();

	/**
	 * Incoming arrows will have this color.
	 */
	private String distinctiveReferenceColor;

	private String interfaceColor;

	private String superClassColor;

	boolean isEnum;

	boolean isInterface;

	boolean isArray;

	private final ClassGraph dump;

	List<ClassDescriptor> interfaces = new ArrayList<ClassDescriptor>();

	ClassDescriptor superClass;

	/**
	 * Amount of field and method references pointing to this class.
	 */
	private int referenceCount = 0;

	public ClassDescriptor(final Class<? extends Object> clazz, final ClassGraph dump) {
		this.dump = dump;

		fullyQualifiedName = clazz.getName();
		dump.nameToClassMap.put(fullyQualifiedName, this);

		isArray = clazz.isArray();

		if (isArray) {
			final Class<?> componentType = clazz.getComponentType();
			dump.addClass(componentType);
		}

		// System.out.println("class: " + fullyQualifiedName);

		isEnum = clazz.isEnum();

		isInterface = clazz.isInterface();

		if (!isVisible())
			return;

		indexFields(clazz.getDeclaredFields());
		indexFields(clazz.getFields());

		indexMethods(clazz);

		for (final Class interfaceClass : clazz.getInterfaces())
			interfaces.add(dump.addClass(interfaceClass));

		superClass = dump.addClass(clazz.getSuperclass());
	}

	public boolean areReferencesShown() {
		return referenceCount <= MAX_REFERECNES_COUNT;
	}

	public void enlistFieldReferences(final StringBuffer result) {
		if (nameToFieldMap.isEmpty())
			return;

		result.append("\n");
		result.append("    // field references to other classes\n");
		for (final Map.Entry<String, FieldDescriptor> entry : nameToFieldMap
				.entrySet())
			result.append(entry.getValue().getDot());
	}

	public void enlistFields(final StringBuffer result) {
		if (nameToFieldMap.isEmpty())
			return;

		result.append("\n");
		result.append("    // fields:\n");

		// enlist fields
		for (final Map.Entry<String, FieldDescriptor> entry : nameToFieldMap
				.entrySet())
			result.append(entry.getValue().getEmbeddedDot());
	}

	public void enlistImplementedInterfaces(final StringBuffer result) {
		if (interfaces.isEmpty())
			return;

		result.append("\n");
		result.append("    // interfaces implemented by class: "
				+ fullyQualifiedName + "\n");

		for (final ClassDescriptor interfaceDescriptor : interfaces) {
			if (!interfaceDescriptor.isVisible())
				continue;

			result.append("    " + interfaceDescriptor.getGraphId() + " -> "
					+ getGraphId() + "[style=\"dotted, tapered\", color=\""
					+ interfaceDescriptor.getInterfaceColor()
					+ "\", penwidth=20, dir=\"forward\"];\n");
		}
	}

	public void enlistMethodReferences(final StringBuffer result) {
		if (methods.isEmpty())
			return;

		result.append("\n");
		result.append("    // method references to other classes\n");
		for (final MethodDescriptor methodDescriptor : methods)
			result.append(methodDescriptor.getDot());
	}

	public void enlistMethods(final StringBuffer result) {
		if (methods.isEmpty())
			return;

		result.append("\n");
		result.append("    // methods:\n");

		// enlist methods
		for (final MethodDescriptor methodDescriptor : methods)
			result.append(methodDescriptor.getEmbeddedDot());
	}

	public void enlistSuperClass(final StringBuffer result) {
		if (superClass == null)
			return;

		if (!superClass.isVisible())
			return;

		result.append("\n");
		result.append("    // super class for: " + fullyQualifiedName + "\n");

		result.append("    " + superClass.getGraphId() + " -> " + getGraphId()
				+ "[style=\"tapered\", color=\""
				+ superClass.getSuperClassColor()
				+ "\", penwidth=10, dir=\"forward\"];\n");
	}

	public void generateDotHeader(final StringBuffer result) {
		result.append("\n");
		result.append("// Class: " + fullyQualifiedName + "\n");

		result.append("    " + getGraphId() + "[label=<<TABLE "
				+ getBackgroundColor() + " BORDER=\"" + getBorderWidth()
				+ "\" CELLBORDER=\"1\" CELLSPACING=\"0\">\n");

		result.append("\n");
		result.append("    // class descriptor header\n");
		result.append("    <TR><TD colspan=\"2\" PORT=\"f0\">"
				+ "<FONT POINT-SIZE=\"8.0\" >" + getPackageName()
				+ "</FONT><br/>" + "<FONT POINT-SIZE=\"25.0\"><B>"
				+ getClassName(false) + "</B></FONT>" + "</TD></TR>\n");
	}

	public List<FieldDescriptor> getAllFields() {
		final List<FieldDescriptor> result = new ArrayList<FieldDescriptor>();

		for (final Map.Entry<String, FieldDescriptor> entry : nameToFieldMap
				.entrySet())
			result.add(entry.getValue());

		return result;
	}

	public String getBackgroundColor() {
		String bgColor = "";

		if (isEnum)
			bgColor = "bgcolor=\"navajowhite2\"";

		if (isInterface)
			bgColor = "bgcolor=\"darkslategray1\"";

		return bgColor;
	}

	public String getBorderWidth() {

		if (!areReferencesShown())
			return "4";
		return "1";
	}

	public String getClassName(final boolean differentiateArray) {

		final int i = fullyQualifiedName.lastIndexOf('.');

		String result = fullyQualifiedName.substring(i + 1);

		if (isArray)
			result = result.substring(0, result.length() - 1);

		if (differentiateArray)
			if (isArray)
				result += " []";

		return result;
	}

	public String getColor() {
		if (getDistinctiveColor() == null)
			setDistinctiveColor(Utils.getNextDarkColor());

		return getDistinctiveColor();
	}

	public String getDistinctiveColor() {
		return distinctiveReferenceColor;
	}

	@Override
	public String getDot() {
		if (!isVisible())
			return "";

		if (isArray)
			return "";

		final StringBuffer result = new StringBuffer();

		generateDotHeader(result);

		enlistFields(result);

		enlistMethods(result);

		result.append("    </TABLE>>, shape=\"none\"];\n");

		enlistFieldReferences(result);

		enlistMethodReferences(result);

		enlistImplementedInterfaces(result);

		enlistSuperClass(result);

		return result.toString();
	}

	@Override
	public String getEmbeddedDot() {
		return null;
	}

	@Override
	public String getGraphId() {
		final String result = "class_"
				+ fullyQualifiedName.replace('.', '_').replace(";", "")
						.replace("[L", "");
		return result;
	}

	public String getInterfaceColor() {
		if (interfaceColor == null)
			interfaceColor = Utils.getNextLightColor();

		return interfaceColor;
	}

	public String getPackageName() {

		final int i = fullyQualifiedName.lastIndexOf('.');

		if (i == -1)
			return "";

		return fullyQualifiedName.substring(0, i).replace("[L", "");
	}

	// public String getReadableName() {
	//
	// // do not print full class name for well known system classes
	// final String packageName = getPackageName();
	//
	// if (packageName.equals("java.util"))
	// return getClassName();
	//
	// if (packageName.equals("java.lang"))
	// return getClassName();
	//
	// return fullyQualifiedName;
	// }

	public String getSuperClassColor() {
		if (superClassColor == null)
			superClassColor = Utils.getNextLightColor();

		return superClassColor;
	}

	public void indexFields(final Field[] fields) {
		for (final Field field : fields) {
			if (nameToFieldMap.containsKey(field.getName()))
				continue;

			final FieldDescriptor fieldDescriptor = new FieldDescriptor(field,
					this, dump);

		}
	}

	private void indexMethods(final Class<? extends Object> clazz) {
		final Method[] methods = clazz.getMethods();

		for (final Method method : methods)
			new MethodDescriptor(method, this, dump);

	}

	@Override
	public boolean isVisible() {

		if (Utils.isSystemDataType(fullyQualifiedName))
			return false;

		if (Utils.isSystemPackage(fullyQualifiedName))
			return false;

		return true;
	}

	public void registerReference() {
		referenceCount++;
	}

	public void setDistinctiveColor(final String distinctiveColor) {
		distinctiveReferenceColor = distinctiveColor;
	}
}

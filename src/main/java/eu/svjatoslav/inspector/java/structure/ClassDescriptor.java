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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Describes single class instance
 */
public class ClassDescriptor implements GraphElement {

	private static final int MAX_REFERECNES_COUNT = 10;

	public final String classFullyQualifiedName;

	Map<String, FieldDescriptor> nameToFieldMap = new TreeMap<String, FieldDescriptor>();

	public SortedSet<MethodDescriptor> methods = new TreeSet<MethodDescriptor>();

	/**
	 * Incoming arrows will have this color.
	 */
	private String distinctiveReferenceColor;

	private String interfaceColor;

	private String superClassColor;

	boolean isEnum;

	boolean isInterface;

	boolean isArray;

	private boolean isShown = true;

	private final ClassGraph classGraph;

	List<ClassDescriptor> interfaces = new ArrayList<ClassDescriptor>();

	ClassDescriptor superClass;

	/**
	 * Amount of field and method references pointing to this class.
	 */
	private int referencesCount = 0;

	// for interface, counts amount of found implementations
	private int implementationsCount = 0;

	// counts amount of times this class is extended
	private int extensionsCount = 0;

	private ClassDescriptor arrayComponent;

	public ClassDescriptor(final Class<? extends Object> clazz,
			final ClassGraph classGraph) {
		this.classGraph = classGraph;

		classFullyQualifiedName = clazz.getName();

		classGraph.registerClass(classFullyQualifiedName, this);

		isArray = clazz.isArray();

		if (isArray) {
			final Class<?> componentType = clazz.getComponentType();
			arrayComponent = classGraph.addClass(componentType);
		}

		// System.out.println("class: " + fullyQualifiedName);

		isEnum = clazz.isEnum();

		isInterface = clazz.isInterface();

		if (!isVisible())
			return;

		indexFields(clazz.getDeclaredFields());
		indexFields(clazz.getFields());

		indexMethods(clazz);

		for (final Class interfaceClass : clazz.getInterfaces()) {
			final ClassDescriptor classDescriptor = classGraph
					.addClass(interfaceClass);
			classDescriptor.registerImplementation();
			interfaces.add(classDescriptor);
		}

		superClass = classGraph.addClass(clazz.getSuperclass());
		if (superClass != null)
			superClass.registerExtension();

	}

	public boolean areReferencesShown() {
		return referencesCount <= MAX_REFERECNES_COUNT;
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
				+ classFullyQualifiedName + "\n");

		for (final ClassDescriptor interfaceDescriptor : interfaces) {
			if (!interfaceDescriptor.isVisible())
				continue;

			if (!interfaceDescriptor.areReferencesShown())
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

		if (!superClass.areReferencesShown())
			return;

		result.append("\n");
		result.append("    // super class for: " + classFullyQualifiedName
				+ "\n");

		result.append("    " + superClass.getGraphId() + " -> " + getGraphId()
				+ "[style=\"tapered\", color=\""
				+ superClass.getSuperClassColor()
				+ "\", penwidth=10, dir=\"forward\"];\n");
	}

	public void generateDotHeader(final StringBuffer result) {
		result.append("\n");
		result.append("// Class: " + classFullyQualifiedName + "\n");

		result.append("    " + getGraphId() + "[label=<<TABLE "
				+ getBackgroundColor() + " BORDER=\"" + getBorderWidth()
				+ "\" CELLBORDER=\"1\" CELLSPACING=\"0\">\n");

		result.append("\n");
		result.append("    // class descriptor header\n");
		result.append("    <TR><TD colspan=\"2\" PORT=\"f0\">"
				+ "<FONT POINT-SIZE=\"8.0\" >" + getPackageName()
				+ "</FONT><br/>");

		final String parentClassesName = getParentClassesName();
		if (parentClassesName.length() > 0)
			result.append("<FONT POINT-SIZE=\"12.0\"><B>" + parentClassesName
					+ "</B></FONT><br/>\n");

		result.append("<FONT POINT-SIZE=\"25.0\"><B>" + getClassName(false)
				+ "</B></FONT>" + "</TD></TR>\n");
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
		// this is needed for nested classes
		final String actualClassName = classFullyQualifiedName
				.replace('$', '.');

		String result;
		if (isArray) {
			// for arrays use array component instead of array class name
			result = arrayComponent.classFullyQualifiedName;
			if (result.contains(".")) {
				final int i = result.lastIndexOf('.');
				result = result.substring(i + 1);
			}
		} else {
			final int i = actualClassName.lastIndexOf('.');
			result = actualClassName.substring(i + 1);
		}

		if (differentiateArray)
			if (isArray)
				result += " []";

		// this is needed for nested classes
		// result = result.replace('$', '.');
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

	/**
	 * Returns field with given name (case is ignored). Or <code>null</code> if
	 * field is not found.
	 */
	public FieldDescriptor getFieldIgnoreCase(final String fieldToSearch) {

		for (final String fieldName : nameToFieldMap.keySet())
			if (fieldToSearch.equalsIgnoreCase(fieldName))
				return nameToFieldMap.get(fieldName);

		return null;
	}

	@Override
	public String getGraphId() {
		final String result = "class_"
				+ classFullyQualifiedName.replace('.', '_').replace(";", "")
				.replace("[L", "").replace('$', '_');
		return result;
	}

	public String getInterfaceColor() {
		if (interfaceColor == null)
			interfaceColor = Utils.getNextLightColor();

		return interfaceColor;
	}

	private int getOutgoingReferencesCount() {
		int result = 0;

		// count method references
		for (final MethodDescriptor methodDescriptor : methods)
			result += methodDescriptor.getOutsideVisibleReferencesCount();

		// count field references
		for (final FieldDescriptor fieldDescriptor : nameToFieldMap.values())
			result += fieldDescriptor.getOutsideVisibleReferencesCount();

		// count implemented interfaces
		for (final ClassDescriptor classDescriptor : interfaces)
			if (classDescriptor.isVisible())
				result++;

		// count superclass
		if (superClass != null)
			if (superClass.isVisible())
				result++;

		return result;
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

	public String getPackageName() {

		final int i = classFullyQualifiedName.lastIndexOf('.');

		if (i == -1)
			return "";

		return classFullyQualifiedName.substring(0, i).replace("[L", "");
	}

	public String getParentClassesName() {
		int i = classFullyQualifiedName.lastIndexOf('.');
		final String fullClassName = classFullyQualifiedName.substring(i + 1);

		i = fullClassName.lastIndexOf('$');
		if (i == -1)
			return "";
		final String parentClassesName = fullClassName.substring(0, i);
		return parentClassesName.replace('$', '.');
	}

	public String getSuperClassColor() {
		if (superClassColor == null)
			superClassColor = Utils.getNextLightColor();

		return superClassColor;
	}

	/**
	 * Checks if class has field with given name (case is ignored). Returns
	 * <code>true</code> if such field is found.
	 */
	public boolean hasFieldIgnoreCase(final String fieldToSearch) {

		for (final String fieldName : nameToFieldMap.keySet())
			if (fieldToSearch.equalsIgnoreCase(fieldName))
				return true;

		return false;
	}

	public void hide() {
		isShown = false;
	}

	public void hideClassIfNoReferences() {
		if (!isVisible())
			return;

		final int totalReferencesCount = getOutgoingReferencesCount()
				+ referencesCount + extensionsCount + implementationsCount;

		if (totalReferencesCount == 0) {
			hide();
			return;
		}

		return;
	}

	public void indexFields(final Field[] fields) {
		for (final Field field : fields) {
			if (nameToFieldMap.containsKey(field.getName()))
				continue;

			final FieldDescriptor fieldDescriptor = new FieldDescriptor(field,
					this, classGraph);

		}
	}

	private void indexMethods(final Class<? extends Object> clazz) {
		final Method[] methods = clazz.getMethods();

		for (final Method method : methods)
			new MethodDescriptor(method, this, classGraph);

	}

	@Override
	public boolean isVisible() {

		if (Utils.isSystemDataType(classFullyQualifiedName))
			return false;

		if (Utils.isSystemPackage(classFullyQualifiedName))
			return false;

		if (!classGraph.getFilter().isClassShown(classFullyQualifiedName))
			return false;

		if (isArray)
			if (arrayComponent != null)
				if (Utils
						.isSystemDataType(arrayComponent.classFullyQualifiedName))
					// Do not show references to primitive data types in arrays.
					// That is: there is no point to show reference to byte when
					// we have class with byte array field.
					return false;

		return isShown;
	}

	/**
	 * Register event when another class is extending this one.
	 */
	public void registerExtension() {
		extensionsCount++;
	}

	public void registerImplementation() {
		implementationsCount++;
	}

	public void registerReference() {
		referencesCount++;
	}

	public void setDistinctiveColor(final String distinctiveColor) {
		distinctiveReferenceColor = distinctiveColor;
	}
}

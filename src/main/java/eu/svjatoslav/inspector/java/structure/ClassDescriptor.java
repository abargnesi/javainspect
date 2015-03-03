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

	private String fullyQualifiedName;

	private final Map<String, FieldDescriptor> nameToFieldMap = new TreeMap<String, FieldDescriptor>();

	private final SortedSet<MethodDescriptor> methods = new TreeSet<MethodDescriptor>();

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

	public ClassDescriptor(final ClassGraph classGraph) {
		this.classGraph = classGraph;
	}

	protected void analyzeClass(final Class<? extends Object> clazz) {

		fullyQualifiedName = clazz.getName();

		isArray = clazz.isArray();

		if (isArray) {
			final Class<?> componentType = clazz.getComponentType();
			arrayComponent = getClassGraph().getOrCreateClassDescriptor(
					componentType);
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
			final ClassDescriptor interfaceClassDescriptor = getClassGraph()
					.getOrCreateClassDescriptor(interfaceClass);
			interfaceClassDescriptor.registerImplementation();
			interfaces.add(interfaceClassDescriptor);
		}

		superClass = getClassGraph().getOrCreateClassDescriptor(
				clazz.getSuperclass());
		if (superClass != null)
			superClass.registerExtension();

	};

	protected boolean areReferencesShown() {
		return referencesCount <= MAX_REFERECNES_COUNT;
	}

	private void enlistFieldReferences(final StringBuffer result) {
		if (nameToFieldMap.isEmpty())
			return;

		result.append("\n");
		result.append("    // field references to other classes\n");
		for (final Map.Entry<String, FieldDescriptor> entry : nameToFieldMap
				.entrySet())
			result.append(entry.getValue().getDot());
	}

	private void enlistFields(final StringBuffer result) {
		if (nameToFieldMap.isEmpty())
			return;

		result.append("\n");
		result.append("    // fields:\n");

		// enlist fields
		for (final Map.Entry<String, FieldDescriptor> entry : nameToFieldMap
				.entrySet())
			result.append(entry.getValue().getEmbeddedDot());
	}

	private void enlistImplementedInterfaces(final StringBuffer result) {
		if (interfaces.isEmpty())
			return;

		result.append("\n");
		result.append("    // interfaces implemented by class: "
				+ fullyQualifiedName + "\n");

		for (final ClassDescriptor interfaceDescriptor : interfaces) {
			if (!interfaceDescriptor.isVisible())
				continue;

			if (!interfaceDescriptor.areReferencesShown())
				continue;

			result.append("    " + interfaceDescriptor.getGraphId() + " -> "
					+ getGraphId() + "[style=\"dotted\", color=\""
					+ interfaceDescriptor.getInterfaceColor()
					+ "\", penwidth=10, dir=\"forward\"];\n");
		}
	}

	private void enlistMethodReferences(final StringBuffer result) {
		if (methods.isEmpty())
			return;

		result.append("\n");
		result.append("    // method references to other classes\n");
		for (final MethodDescriptor methodDescriptor : methods)
			result.append(methodDescriptor.getDot());
	}

	private void enlistMethods(final StringBuffer result) {
		if (methods.isEmpty())
			return;

		result.append("\n");
		result.append("    // methods:\n");

		// enlist methods
		for (final MethodDescriptor methodDescriptor : methods)
			result.append(methodDescriptor.getEmbeddedDot());
	}

	private void enlistSuperClass(final StringBuffer result) {
		if (superClass == null)
			return;

		if (!superClass.isVisible())
			return;

		if (!superClass.areReferencesShown())
			return;

		result.append("\n");
		result.append("    // super class for: " + fullyQualifiedName + "\n");

		result.append("    " + superClass.getGraphId() + " -> " + getGraphId()
				+ "[ color=\"" + superClass.getSuperClassColor()
				+ "\", penwidth=10, dir=\"forward\"];\n");
	}

	private void generateDotHeader(final StringBuffer result) {
		result.append("\n");
		result.append("// Class: " + fullyQualifiedName + "\n");

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

	private String getBackgroundColor() {
		String bgColor = "";

		if (isEnum)
			bgColor = "bgcolor=\"navajowhite2\"";

		if (isInterface)
			bgColor = "bgcolor=\"darkslategray1\"";

		return bgColor;
	}

	private String getBorderWidth() {

		if (!areReferencesShown())
			return "4";
		return "1";
	}

	protected ClassGraph getClassGraph() {
		return classGraph;
	}

	protected String getClassName(final boolean differentiateArray) {
		// this is needed for nested classes
		final String actualClassName = fullyQualifiedName.replace('$', '.');

		String result;
		if (isArray) {
			// for arrays use array component instead of array class name
			result = arrayComponent.fullyQualifiedName;
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

	protected String getColor() {
		if (distinctiveReferenceColor == null)
			distinctiveReferenceColor = Utils.getNextDarkColor();

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
	protected FieldDescriptor getFieldIgnoreCase(final String fieldToSearch) {

		for (final String fieldName : nameToFieldMap.keySet())
			if (fieldToSearch.equalsIgnoreCase(fieldName))
				return nameToFieldMap.get(fieldName);

		return null;
	}

	protected String getFullyQualifiedName() {
		return fullyQualifiedName;
	}

	@Override
	public String getGraphId() {
		final String result = "class_"
				+ fullyQualifiedName.replace('.', '_').replace(";", "")
				.replace("[L", "").replace('$', '_');
		return result;
	}

	private String getInterfaceColor() {
		if (interfaceColor == null)
			interfaceColor = Utils.getNextDarkColor();

		return interfaceColor;
	}

	private FieldDescriptor getOrCreateFieldDescriptor(final Field field) {

		final String fieldName = field.getName();

		if (nameToFieldMap.containsKey(fieldName))
			return nameToFieldMap.get(fieldName);

		final FieldDescriptor newFieldDescriptor = new FieldDescriptor(this);
		nameToFieldMap.put(fieldName, newFieldDescriptor);

		newFieldDescriptor.analyzeField(field);

		return newFieldDescriptor;
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

	private String getPackageName() {

		final int i = fullyQualifiedName.lastIndexOf('.');

		if (i == -1)
			return "";

		return fullyQualifiedName.substring(0, i).replace("[L", "");
	}

	private String getParentClassesName() {
		int i = fullyQualifiedName.lastIndexOf('.');
		final String fullClassName = fullyQualifiedName.substring(i + 1);

		i = fullClassName.lastIndexOf('$');
		if (i == -1)
			return "";
		final String parentClassesName = fullClassName.substring(0, i);
		return parentClassesName.replace('$', '.');
	}

	private String getSuperClassColor() {
		if (superClassColor == null)
			superClassColor = Utils.getNextLightColor();

		return superClassColor;
	}

	/**
	 * Checks if class has field with given name (case is ignored). Returns
	 * <code>true</code> if such field is found.
	 */
	protected boolean hasFieldIgnoreCase(final String fieldToSearch) {

		for (final String fieldName : nameToFieldMap.keySet())
			if (fieldToSearch.equalsIgnoreCase(fieldName))
				return true;

		return false;
	}

	private void hide() {
		isShown = false;
	}

	protected void hideClassIfNoReferences() {
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

	private void indexFields(final Field[] fields) {
		for (final Field field : fields)
			getOrCreateFieldDescriptor(field);
	}

	private void indexMethods(final Class<? extends Object> clazz) {
		for (final Method method : clazz.getMethods()) {
			final MethodDescriptor methodDescriptor = new MethodDescriptor(
					this, method.getName());

			methods.add(methodDescriptor);

			methodDescriptor.analyze(method);
		}

	}

	@Override
	public boolean isVisible() {

		if (Utils.isSystemDataType(fullyQualifiedName))
			return false;

		if (Utils.isSystemPackage(fullyQualifiedName))
			return false;

		if (!getClassGraph().getFilter().isClassShown(fullyQualifiedName))
			return false;

		if (isArray)
			if (arrayComponent != null)
				if (Utils.isSystemDataType(arrayComponent.fullyQualifiedName))
					// Do not show references to primitive data types in arrays.
					// That is: there is no point to show reference to byte when
					// we have class with byte array field.
					return false;

		return isShown;
	}

	/**
	 * Register event when another class is extending this one.
	 */
	protected void registerExtension() {
		extensionsCount++;
	}

	protected void registerImplementation() {
		implementationsCount++;
	}

	protected void registerReference() {
		referencesCount++;
	}

}

/*
 * JavaInspect - Utility to visualize java software
 * Copyright (C) 2013-2015, Svjatoslav Agejenko, svjatoslav@svjatoslav.eu
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

/**
 * This class corresponds to single method within a java class.
 */
public class MethodDescriptor implements GraphElement,
        Comparable<MethodDescriptor> {

    private final String methodName;
    private final ClassDescriptor parentClass;
    private final List<ClassDescriptor> argumentTypes = new ArrayList<ClassDescriptor>();
    private ClassDescriptor returnType;
    private boolean isInherited;

    public MethodDescriptor(final ClassDescriptor parent,
                            final String methodName) {
        parentClass = parent;
        this.methodName = methodName;
    }

    public void analyze(final Method method) {

        if (!method.getDeclaringClass().getName()
                .equals(parentClass.getFullyQualifiedName()))
            isInherited = true;

        returnType = parentClass.getClassGraph().getOrCreateClassDescriptor(
                method.getReturnType());
        returnType.registerReference();

        final Type genericType = method.getGenericReturnType();
        if (genericType instanceof ParameterizedType) {
            final ParameterizedType pt = (ParameterizedType) genericType;
            for (final Type t : pt.getActualTypeArguments())
                if (t instanceof Class) {
                    final Class cl = (Class) t;
                    final ClassDescriptor classDescriptor = parentClass
                            .getClassGraph().getOrCreateClassDescriptor(cl);
                    classDescriptor.registerReference();
                    argumentTypes.add(classDescriptor);
                }

        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MethodDescriptor)) return false;

        MethodDescriptor that = (MethodDescriptor) o;

        if (methodName != null ? !methodName.equals(that.methodName) : that.methodName != null) return false;
        if (parentClass != null ? !parentClass.equals(that.parentClass) : that.parentClass != null) return false;
        return argumentTypes != null ? argumentTypes.equals(that.argumentTypes) : that.argumentTypes == null;

    }


    @Override
    public int hashCode() {
        int result = methodName != null ? methodName.hashCode() : 0;
        result = 31 * result + (parentClass != null ? parentClass.hashCode() : 0);
        result = 31 * result + (argumentTypes != null ? argumentTypes.hashCode() : 0);
        return result;
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
                            + classDescriptor.getGraphId() + "[label=\""
                            + methodName + "\", color=\""
                            + classDescriptor.getColor()
                            + "\", style=\"dotted, bold\"];\n");

        if (!returnType.isVisible())
            return result.toString();

        // main type
        if (returnType.areReferencesShown())
            result.append("    " + getGraphId() + " -> "
                    + returnType.getGraphId() + "[label=\"" + methodName
                    + "\"," + " color=\"" + returnType.getColor()
                    + "\", style=\"dotted, bold\"];\n");

        return result.toString();
    }

    @Override
    public String getEmbeddedDot() {
        if (!isVisible())
            return "";

        final StringBuffer result = new StringBuffer();

        result.append("        // " + methodName + "\n");

        result.append("        <TR><td ALIGN=\"right\">"
                + "<FONT POINT-SIZE=\"8.0\">" + returnType.getClassName(true)
                + "</FONT>" + "</td><TD PORT=\"" + getMethodLabel()
                + "\" ALIGN=\"left\"><FONT COLOR =\"red\" POINT-SIZE=\"11.0\">"
                + getMethodLabel() + "</FONT></TD></TR>\n");

        return result.toString();
    }

    @Override
    public String getGraphId() {
        return parentClass.getGraphId() + ":" + methodName;
    }

    private String getMethodLabel() {
        return methodName;
    }

    protected int getOutsideVisibleReferencesCount() {
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

        // hide inherited methods
        if (isInherited)
            return false;

        // hide common object methods
        if (Utils.isCommonObjectMethod(methodName))
            return false;

        // hide common Enumeration methods
        if (parentClass.isEnum && Utils.isEnumMethod(methodName))
            return false;

        // hide get/set methods for the field of the same name
        if (methodName.startsWith("get") || methodName.startsWith("set"))
            if (parentClass.hasFieldIgnoreCase(methodName.substring(3)))
                return false;

        // hide is methods for the boolean field of the same name
        if (methodName.startsWith("is")) {
            final FieldDescriptor field = parentClass
                    .getFieldIgnoreCase(methodName.substring(2));
            if (field != null)
                if ("boolean".equals(field.getType().getFullyQualifiedName()))
                    return false;
        }

        return true;

    }

    @Override
    public int compareTo(MethodDescriptor that) {
        if (this == that) return 0;

        int comparisonResult = methodName.compareTo(that.methodName);
        if (comparisonResult != 0) return comparisonResult;

        return parentClass.compareTo(that.parentClass);
    }
}

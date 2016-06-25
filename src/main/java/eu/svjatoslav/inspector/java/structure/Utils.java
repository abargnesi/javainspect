/*
 * JavaInspect - Utility to visualize java software
 * Copyright (C) 2013-2015, Svjatoslav Agejenko, svjatoslav@svjatoslav.eu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 3 of the GNU Lesser General Public License
 * or later as published by the Free Software Foundation.
 */

package eu.svjatoslav.inspector.java.structure;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static final List<String> systemDataTypes = new ArrayList<String>();
    private static final List<String> commonObjectMethods = new ArrayList<String>();
    private static final List<String> systemPackages = new ArrayList<String>();
    private static final List<String> darkColors = new ArrayList<String>();
    private static final List<String> lightColors = new ArrayList<String>();
    private static final List<String> enumMethods = new ArrayList<String>();
    private static int lastChosenDarkColor = -1;
    private static int lastChosenLightColor = -1;

    static {
        initEnumMethods();
        initSystemDataTypes();
        initDarkColors();
        initLightColors();
        initCommonObjectMethods();
        initSystemPackages();
    }

    /**
     * retrieves colors from predefined palette
     *
     * @return next available dark color name
     */
    protected static String getNextDarkColor() {
        lastChosenDarkColor++;
        if (lastChosenDarkColor >= darkColors.size())
            lastChosenDarkColor = 0;

        return darkColors.get(lastChosenDarkColor);
    }

    /**
     * retrieves colors from predefined palette
     *
     * @return next available light color name
     */
    protected static String getNextLightColor() {
        lastChosenLightColor++;
        if (lastChosenLightColor >= lightColors.size())
            lastChosenLightColor = 0;

        return lightColors.get(lastChosenLightColor);
    }

    private static void initCommonObjectMethods() {
        commonObjectMethods.add("wait");
        commonObjectMethods.add("equals");
        commonObjectMethods.add("toString");
        commonObjectMethods.add("hashCode");
        commonObjectMethods.add("notify");
        commonObjectMethods.add("notifyAll");
        commonObjectMethods.add("getClass");
    }

    protected static void initDarkColors() {
        darkColors.add("antiquewhite4");
        darkColors.add("blueviolet");
        darkColors.add("brown4");
        darkColors.add("chartreuse4");
        darkColors.add("cyan4");
        darkColors.add("deeppink1");
        darkColors.add("deepskyblue3");
        darkColors.add("firebrick1");
        darkColors.add("goldenrod3");
        darkColors.add("gray0");
    }

    private static void initEnumMethods() {
        enumMethods.add("values");
        enumMethods.add("valueOf");
        enumMethods.add("name");
        enumMethods.add("compareTo");
        enumMethods.add("valueOf");
        enumMethods.add("getDeclaringClass");
        enumMethods.add("ordinal");
    }

    private static void initLightColors() {
        lightColors.add("olivedrab2");
        lightColors.add("peachpuff2");
        lightColors.add("seagreen1");
        lightColors.add("violet");
        lightColors.add("aqua");
        lightColors.add("orange");
    }

    private static void initSystemDataTypes() {
        systemDataTypes.add("void");
        systemDataTypes.add("int");
        systemDataTypes.add("long");
        systemDataTypes.add("float");
        systemDataTypes.add("double");
        systemDataTypes.add("boolean");
        systemDataTypes.add("char");
        systemDataTypes.add("short");
        systemDataTypes.add("byte");
    }

    private static void initSystemPackages() {
        systemPackages.add("java.");
        systemPackages.add("javax.");
        systemPackages.add("sun.");
    }

    protected static boolean isCommonObjectMethod(final String name) {
        return commonObjectMethods.contains(name);
    }

    protected static boolean isEnumMethod(final String name) {
        return enumMethods.contains(name);
    }

    protected static boolean isSystemDataType(final String name) {
        return systemDataTypes.contains(name);
    }

    protected static boolean isSystemPackage(final String name) {

        for (final String packagePrefix : systemPackages)
            if (name.startsWith(packagePrefix))
                return true;

        return false;
    }

}

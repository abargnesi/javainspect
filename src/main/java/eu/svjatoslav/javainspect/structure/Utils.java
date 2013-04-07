/*
 * JavaInspect - Utility to visualize java software
 * Copyright (C) 2013, Svjatoslav Agejenko, svjatoslav@svjatoslav.eu
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
 */

package eu.svjatoslav.javainspect.structure;

import java.util.ArrayList;
import java.util.List;

public class Utils {

	private static final List<String> systemDataTypes = new ArrayList<String>();

	private static final List<String> systemMethods = new ArrayList<String>();

	private static final List<String> systemPackages = new ArrayList<String>();

	private static final List<String> darkColors = new ArrayList<String>();

	private static final List<String> lightColors = new ArrayList<String>();

	private static final List<String> enumMethods = new ArrayList<String>();

	public static int lastChosenDarkColor = -1;

	public static int lastChosenLightColor = -1;

	static {
		initEnumMethods();
		initSystemDataTypes();
		initDarkColors();
		initLightColors();
		initSystemMethods();
		initSystemPackages();
	}

	/**
	 * retrieves colors from predefined palette
	 */
	public static String getNextDarkColor() {
		lastChosenDarkColor++;
		if (lastChosenDarkColor >= darkColors.size())
			lastChosenDarkColor = 0;

		return darkColors.get(lastChosenDarkColor);
	}

	/**
	 * retrieves colors from predefined palette
	 */
	public static String getNextLightColor() {
		lastChosenLightColor++;
		if (lastChosenLightColor >= lightColors.size())
			lastChosenLightColor = 0;

		return lightColors.get(lastChosenLightColor);
	}

	public static void initDarkColors() {
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

	public static void initEnumMethods() {
		enumMethods.add("values");
		enumMethods.add("valueOf");
		enumMethods.add("name");
		enumMethods.add("compareTo");
		enumMethods.add("valueOf");
		enumMethods.add("getDeclaringClass");
		enumMethods.add("ordinal");
	}

	public static void initLightColors() {
		lightColors.add("olivedrab2");
		lightColors.add("peachpuff2");
		lightColors.add("seagreen1");
		lightColors.add("yellow");
		lightColors.add("violet");
	}

	public static void initSystemDataTypes() {
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

	public static void initSystemMethods() {
		systemMethods.add("wait");
		systemMethods.add("equals");
		systemMethods.add("toString");
		systemMethods.add("hashCode");
		systemMethods.add("notify");
		systemMethods.add("notifyAll");
		systemMethods.add("getClass");
	}

	public static void initSystemPackages() {
		systemPackages.add("java.");
		systemPackages.add("javax.");
		systemPackages.add("sun.");
	}

	public static boolean isEnumMethod(final String name) {
		return enumMethods.contains(name);
	}

	public static boolean isSystemDataType(final String name) {
		return systemDataTypes.contains(name);
	}

	public static boolean isSystemMethod(final String name) {
		return systemMethods.contains(name);
	}

	public static boolean isSystemPackage(final String name) {

		for (final String packagePrefix : systemPackages)
			if (name.startsWith(packagePrefix))
				return true;

		return false;
	}

}

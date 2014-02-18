/*
 * JavaInspect - Utility to visualize java software
 * Copyright (C) 2013-2014, Svjatoslav Agejenko, svjatoslav@svjatoslav.eu
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 3 of the GNU Lesser General Public License
 * or later as published by the Free Software Foundation.
 */

package eu.svjatoslav.inspector.java.methods;

public class Modifiers {

	public enum Access {
		PUBLIC("public"), PROTECTED("protected"), DEFAULT(""), PRIVATE(
				"private");

		public final String name;

		Access(final String name) {
			this.name = name;
		};
	}

	Access access = Access.DEFAULT;

	boolean isStatic = false;;

	boolean isFinal = false;

	boolean isAbstract = false;

	public boolean parseModifier(final String string) {
		for (final Access access : Access.values())
			if (access.name.equals(string)) {
				this.access = access;
				return true;
			}

		if ("static".equals(string)) {
			isStatic = true;
			return true;
		}

		if ("final".equals(string)) {
			isFinal = true;
			return true;
		}

		if ("abstract".equals(string)) {
			isAbstract = true;
			return true;
		}

		return false;
	}

	public void reset() {
		isStatic = false;
		isFinal = false;
		access = Access.DEFAULT;
	}

	@Override
	public String toString() {
		final StringBuffer result = new StringBuffer();

		result.append(access.name);

		if (isStatic) {
			if (result.length() > 0)
				result.append(" ");
			result.append("static");
		}

		if (isFinal) {
			if (result.length() > 0)
				result.append(" ");
			result.append("final");
		}

		return result.toString();
	}
}

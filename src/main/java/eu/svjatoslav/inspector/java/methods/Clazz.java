/*
 * JavaInspect - Utility to visualize java software
 * Copyright (C) 2013-2015, Svjatoslav Agejenko, svjatoslav@svjatoslav.eu
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 3 of the GNU Lesser General Public License
 * or later as published by the Free Software Foundation.
 */

package eu.svjatoslav.inspector.java.methods;

import eu.svjatoslav.commons.string.tokenizer.InvalidSyntaxException;
import eu.svjatoslav.commons.string.tokenizer.Tokenizer;
import eu.svjatoslav.commons.string.tokenizer.TokenizerMatch;

import java.util.ArrayList;
import java.util.List;

public class Clazz {

    private final String packageName;
    private final String className;
    private final boolean isInterface;

    public ClassReference superClass;
    public List<ClassReference> implementedInterfaces = new ArrayList<ClassReference>();

    public Clazz(final String packageName, final String className,
                 final Tokenizer tokenizer, final boolean isInterface)
            throws InvalidSyntaxException {

        this.packageName = packageName;
        this.className = className;
        this.isInterface = isInterface;

        while (true) {
            final TokenizerMatch match = tokenizer.getNextToken();

            if ("extends".equals(match.token)) {
                superClass = new ClassReference(tokenizer);
                continue;
            }

            if ("implements".equals(match.token)) {
                while (true) {
                    implementedInterfaces.add(new ClassReference(tokenizer));

                    if (tokenizer.probeNextToken(","))
                        continue;

                    break;
                }
                continue;
            }

            if ("{".equals(match.token)) {
                parseClassBody(tokenizer);
                break;
            }

        }
    }

    public String getFullName() {
        return packageName + "." + className;
    }

    public void parseClassBody(final Tokenizer tokenizer) {
        tokenizer.skipUntilDataEnd();
    }

    @Override
    public String toString() {
        final EnumerationBuffer result = new EnumerationBuffer();

        result.append(packageName + " -> " + className + " ");

        if (isInterface)
            result.append("(interface)");
        else
            result.append("(class)");
        result.append("\n");

        if (superClass != null)
            result.append("    super: " + superClass.toString() + "\n");

        if (implementedInterfaces.size() > 0) {
            result.append("    implements: ");
            for (final ClassReference classReference : implementedInterfaces)
                result.appendEnumeration(classReference.toString());
            result.append("\n");
        }

        return result.toString();
    }

}

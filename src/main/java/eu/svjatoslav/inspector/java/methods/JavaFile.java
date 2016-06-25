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

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JavaFile {

    public static final String UTF_8 = "UTF-8";
    private final List<Import> imports = new ArrayList<Import>();
    private final File file;
    public List<Clazz> classes = new ArrayList<Clazz>();
    StringBuffer contents = new StringBuffer();
    private String packageName;

    public JavaFile(final File file) throws IOException, InvalidSyntaxException {
        this.file = file;
        parse();
    }

    public void parse() throws IOException, InvalidSyntaxException {
        System.out.println("java file: " + file);

        readFile();

        final Tokenizer tokenizer = new Tokenizer(contents.toString());

        // empty space
        tokenizer.addTerminator(" ", true);
        tokenizer.addTerminator("\t", true);
        tokenizer.addTerminator("\n", true);

        tokenizer.addTerminator(";", false);
        tokenizer.addTerminator("{", false);
        tokenizer.addTerminator("}", false);
        tokenizer.addTerminator("(", false);
        tokenizer.addTerminator(")", false);
        tokenizer.addTerminator("[", false);
        tokenizer.addTerminator("]", false);
        tokenizer.addTerminator("<", false);
        tokenizer.addTerminator(">", false);
        tokenizer.addTerminator(",", false);
        tokenizer.addTerminator("@", false);

        // comments
        tokenizer.addTerminator("//", "\n", true);
        tokenizer.addTerminator("/*", "*/", true);

        final Modifiers modifiers = new Modifiers();

        while (true) {
            final TokenizerMatch match = tokenizer.getNextToken();
            if (match == null)
                break;

            if (match.token.equals("package")) {
                parsePackage(tokenizer);
                continue;
            }

            if (match.token.equals("import")) {
                parseImport(tokenizer);
                continue;
            }

            final boolean wasModifier = modifiers.parseModifier(match.token);
            if (wasModifier)
                continue;

            if ("class".equals(match.token)) {
                parseClass(tokenizer);
                continue;
            }

            if ("interface".equals(match.token)) {
                parseInterface(tokenizer);
                continue;
            }

            if ("@".equals(match.token)) {
                new Annotation(tokenizer);
                continue;
            }

            System.out.println("    " + modifiers.toString() + " "
                    + match.token);
            modifiers.reset();
            skipUntilSemicolon(tokenizer);
        }

    }

    private void parseClass(final Tokenizer tokenizer)
            throws InvalidSyntaxException {

        final TokenizerMatch match = tokenizer.getNextToken();
        final Clazz clazz = new Clazz(packageName, match.token, tokenizer,
                false);
        // System.out.println(clazz.toString());
        classes.add(clazz);

    }

    private void parseImport(final Tokenizer tokenizer)
            throws InvalidSyntaxException {

        final Import imp = new Import();

        final TokenizerMatch match = tokenizer.getNextToken();

        if (match.token.equals("static")) {
            imp.isStatic = true;
            imp.path = tokenizer.getNextToken().token;
        } else
            imp.path = match.token;

        imports.add(imp);

        tokenizer.expectNextToken(";");
    }

    private void parseInterface(final Tokenizer tokenizer)
            throws InvalidSyntaxException {

        final TokenizerMatch match = tokenizer.getNextToken();
        final Clazz clazz = new Clazz(packageName, match.token, tokenizer, true);
        // System.out.println(clazz.toString());
        classes.add(clazz);
    }

    private void parsePackage(final Tokenizer tokenizer)
            throws InvalidSyntaxException {

        final TokenizerMatch match = tokenizer.getNextToken();

        packageName = match.token;

        tokenizer.expectNextToken(";");
    }

    private void readFile() throws IOException {
        InputStreamReader inputStream = new InputStreamReader(new FileInputStream(file), UTF_8);

        final BufferedReader bufferedReader = new BufferedReader(inputStream);

        while (true) {
            final String line = bufferedReader.readLine();

            if (line == null)
                break;

            contents.append(line);
            contents.append("\n");
        }

        bufferedReader.close();
        inputStream.close();
    }

    public void skipUntilSemicolon(final Tokenizer tokenizer) {
        while (true) {
            final TokenizerMatch token = tokenizer.getNextToken();

            if (token == null)
                return;

            if (token.token.equals(";"))
                return;
        }
    }

}

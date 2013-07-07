package eu.svjatoslav.inspector.java.methods;

import java.util.ArrayList;
import java.util.List;

import eu.svjatoslav.inspector.tokenizer.InvalidSyntaxException;
import eu.svjatoslav.inspector.tokenizer.Tokenizer;

public class ClassReference {

	public String name;

	List<ClassReference> typeParameters = new ArrayList<ClassReference>();

	public ClassReference(final Tokenizer tokenizer)
			throws InvalidSyntaxException {
		name = tokenizer.getToken().token;

		if (!tokenizer.isNextToken("<"))
			return;

		while (true) {
			final ClassReference parameterType = new ClassReference(tokenizer);
			typeParameters.add(parameterType);

			if (!tokenizer.isNextToken(","))
				break;
		}

		tokenizer.expectToken(">");
	}

	@Override
	public String toString() {
		final EnumerationBuffer result = new EnumerationBuffer();

		result.append(name);

		if (typeParameters.size() > 0) {
			result.append("<");
			for (final ClassReference classReference : typeParameters)
				result.appendEnumeration(classReference.toString());
			result.append(">");
		}

		return result.toString();
	}
}

package eu.svjatoslav.inspector.java.methods;

import java.util.ArrayList;
import java.util.List;

import eu.svjatoslav.commons.string.tokenizer.InvalidSyntaxException;
import eu.svjatoslav.commons.string.tokenizer.Tokenizer;

public class ClassReference {

	public String name;

	List<ClassReference> typeParameters = new ArrayList<ClassReference>();

	public ClassReference(final Tokenizer tokenizer)
			throws InvalidSyntaxException {
		name = tokenizer.getNextToken().token;

		if (!tokenizer.probeNextToken("<"))
			return;

		while (true) {
			final ClassReference parameterType = new ClassReference(tokenizer);
			typeParameters.add(parameterType);

			if (!tokenizer.probeNextToken(","))
				break;
		}

		tokenizer.expectNextToken(">");
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

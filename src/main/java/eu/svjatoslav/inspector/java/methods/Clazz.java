package eu.svjatoslav.inspector.java.methods;

import java.util.ArrayList;
import java.util.List;

import eu.svjatoslav.inspector.tokenizer.InvalidSyntaxException;
import eu.svjatoslav.inspector.tokenizer.Tokenizer;
import eu.svjatoslav.inspector.tokenizer.TokenizerMatch;

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

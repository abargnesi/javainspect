package eu.svjatoslav.inspector.java.methods;

public class EnumerationBuffer {

	private final String enumerationDelimiter;

	private final StringBuffer buffer = new StringBuffer();

	public int enumeratedEntitiesCount = 0;

	public EnumerationBuffer() {
		this(", ");
	}

	public EnumerationBuffer(final String enumerationDelimiter) {
		this.enumerationDelimiter = enumerationDelimiter;
	}

	public void append(final String value) {
		buffer.append(value);
	}

	public void appendEnumeration(final String value) {
		if (enumeratedEntitiesCount > 0)
			buffer.append(enumerationDelimiter);

		buffer.append(value);
		enumeratedEntitiesCount++;
	}

	public void resetEnumeration() {
		enumeratedEntitiesCount = 0;
	}

	@Override
	public String toString() {
		return buffer.toString();
	}

}

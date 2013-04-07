package eu.svjatoslav.javainspect.example.structure;

public class SampleClass extends SampleSuperClass {

	ObjectVisibleAsClassField sampleClassField;

	public ObjectReturnedByMethod sampleMethod() {
		return new ObjectReturnedByMethod();
	}

}

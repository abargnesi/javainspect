package eu.svjatoslav.javainspect.example.structure;

public class SampleSuperClass implements SampleInterface {

	@Override
	public SampleEnum getSomeValue() {
		return SampleEnum.ONE;
	}

}

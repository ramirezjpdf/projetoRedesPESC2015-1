package br.ufrj.cos.redes.delayLossSimulator;

public class ExponentialSampleGeneratorExample {
	public static void main(String[] args) {
		//E[X] = 1/lambda for exponential random variables
		double LAMBDA = 50;
		ExponentialSampleGenerator generator = new ExponentialSampleGenerator(LAMBDA);
		for (int i = 0; i < 500; i++) {
			System.out.println("Sample generated = " + generator.getSample());
		}
	}
}

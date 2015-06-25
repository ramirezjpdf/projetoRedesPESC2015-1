package br.ufrj.cos.redes.delayLossSimulator;

import java.util.Random;

public class ExponentialSampleGenerator {
	private double lambda;
	private Random generator;
	
	public ExponentialSampleGenerator(double lambda) {
		this.lambda = lambda;
		this.generator = new Random();
	}
	
	public double getSample() {
		return calculateExponentialSample(this.generator.nextDouble(), this.lambda);
	}
	
	private double calculateExponentialSample(double u, double lambda) {
		return (Math.log(u)) / (-lambda);
	}

	public double getLambda() {
		return lambda;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
		this.generator = new Random();
	}
}

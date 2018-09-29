package eu.eisti.p2k19.fintech.portfolio.optimizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.apache.commons.math3.stat.correlation.Covariance;

import eu.eisti.p2k19.fintech.portfolio.model.Profitability;
import eu.eisti.p2k19.fintech.portfolio.model.Quotation;

public class PortfolioOptimizer {
	
	private final Map<String, List<Quotation>> quotationsMap;
	private final Map<String, Double> expectedProfitability;
	final double expectedPortfolioProfitability;

	private RealVector solution;
	private RealMatrix covarianceMatrix;
	private boolean computed;
	
	public PortfolioOptimizer(Map<String, List<Quotation>> quotationsMap, Map<String, Double> expectedProfitability,
			double expectedPortfolioProfitability) {
		super();
		this.quotationsMap = quotationsMap;
		this.expectedProfitability = expectedProfitability;
		this.expectedPortfolioProfitability = expectedPortfolioProfitability;
		
	}
	
	public void printExpectedProfitabilities() {
		
		if(!computed) computeSolution();
		
		System.out.println("----------------");

		Iterator<String> symbolsIterator;
		symbolsIterator = quotationsMap.keySet().iterator();
		int k = 0;
		while(symbolsIterator.hasNext()) {
			String symbol = symbolsIterator.next();
			System.out.println(symbol + ": " + solution.getEntry(k));
			k++;
		}
		
		System.out.println("----------------");
	}

	private RealVector getIdentityVector(RealMatrix covarianceMatrix) {
		final int dimension = covarianceMatrix.getColumnDimension();
		RealVector identityVector = new ArrayRealVector(dimension);
		for(int k = 0; k < dimension; k++) {
			identityVector.setEntry(k, 1);
		}
		return identityVector;
	}

	private Map<String, List<Profitability>> computeProfitabilitiesMap(
			Map<String, List<Quotation>> quotationsMap) {
		Map<String, List<Profitability>> profitabilitiesMap = new HashMap<String, List<Profitability>>();
		Iterator<String> symbolsIterator = quotationsMap.keySet().iterator();

		while(symbolsIterator.hasNext()) {
			String symbol = symbolsIterator.next();
			profitabilitiesMap.put(symbol, computeProfitability(quotationsMap.get(symbol)));
		}
		return profitabilitiesMap;
	}

	private RealMatrix computeCovarianceMatrix(Map<String, List<Profitability>> profitabilitiesMap) {
		Iterator<String> symbolsIterator;
		int nbSymbols = profitabilitiesMap.size();
		String firstSymbol = (String) (profitabilitiesMap.keySet().toArray())[0];
		int nbVals = profitabilitiesMap.get(firstSymbol).size();
		
		double [][] profitabilityTable = new double[nbVals][nbSymbols];
		symbolsIterator = profitabilitiesMap.keySet().iterator();
		int i = 0;
		while(symbolsIterator.hasNext()) {
			String symbol = symbolsIterator.next();
			Iterator<Profitability> profitabilitiesIterator = profitabilitiesMap.get(symbol).iterator();
			int j = 0;
			while(profitabilitiesIterator.hasNext()) {
				profitabilityTable[j][i] = profitabilitiesIterator.next().getValue();
				j++;
			}
			i++;
		}
		
		Covariance covariance = new Covariance(profitabilityTable);
		RealMatrix covarianceMatrix = covariance.getCovarianceMatrix();
		return covarianceMatrix;
	}

	private RealVector getExpectedProfitabilityMatrix(Map<String, Double> expectedProfitability) {
		Iterator<String> symbolsIterator;

		RealVector expectedProfitabilityVector = new ArrayRealVector(expectedProfitability.size());
		symbolsIterator = expectedProfitability.keySet().iterator();
		int d = 0;
		while(symbolsIterator.hasNext()) {
			String symbol = symbolsIterator.next();
			expectedProfitabilityVector.setEntry(d, expectedProfitability.get(symbol));
			d++;
		}
		return expectedProfitabilityVector;
	}

	private RealVector getWeights(RealMatrix covarianceMatrix, RealVector expectedProfitabilityVector, double expectedPortfolioProfitability) {
		
		final int dimension = covarianceMatrix.getColumnDimension();
		
		RealMatrix lagrangeMatrix = MatrixUtils.createRealMatrix(dimension+2, dimension+2);
		RealVector b = new ArrayRealVector(dimension+2);

		for(int i = 0; i < dimension; i++)
		{
			b.setEntry(i, 0);
			for(int j = 0; j < dimension; j++)
			{
				lagrangeMatrix.setEntry(i, j, 2 * covarianceMatrix.getEntry(i, j));
			}
		}

		for(int d = 0; d < dimension; d++)
		{
			lagrangeMatrix.setEntry(dimension, d, expectedProfitabilityVector.getEntry(d));
			lagrangeMatrix.setEntry(dimension+1, d, 1);
			lagrangeMatrix.setEntry(d, dimension, expectedProfitabilityVector.getEntry(d));
			lagrangeMatrix.setEntry(d, dimension+1, 1);
		}
		
		b.setEntry(dimension, expectedPortfolioProfitability);
		b.setEntry(dimension + 1, 1);
		
		SingularValueDecomposition svd = new SingularValueDecomposition(lagrangeMatrix);
				
		RealVector solution = svd.getSolver().solve(b).getSubVector(0, dimension);
		
		return solution;
	}
	
	
	private List<Profitability> computeProfitability(List<Quotation> list) {
		List<Profitability> result = new ArrayList<Profitability>();
		
		int size = list.size();
		for(int i = 12; i < size; i++) {
			double profitability = (list.get(i).getValue() - list.get(i - 12).getValue()) / list.get(i - 12).getValue();
			result.add(new Profitability(list.get(i-12).getDate(), profitability));
		}
		
		return result;
	}	
	
	public Map<String, List<Quotation>> getQuotationsMap() {
		return quotationsMap;
	}
	
	public Map<String, Double> getExpectedProfitability() {
		return expectedProfitability;
	}
	
	public double getExpectedPortfolioProfitability() {
		return expectedPortfolioProfitability;
	}

	public RealVector computeSolution() {

		if(!computed) {
		
			RealVector expectedProfitabilityVector = getExpectedProfitabilityMatrix(expectedProfitability);
			Map<String, List<Profitability>> profitabilitiesMap = computeProfitabilitiesMap(quotationsMap);
			covarianceMatrix = computeCovarianceMatrix(profitabilitiesMap);
			solution = getWeights(covarianceMatrix, expectedProfitabilityVector, expectedPortfolioProfitability);
			
			computed = true;
		}
		
		return solution;
	}

	public double getVariance() {

		if(!computed) computeSolution();
		
		double variance = covarianceMatrix.preMultiply(solution).dotProduct(solution);		
		return variance;
	}

	public RealMatrix getCovarianceMatrix() {
		if(!computed) {
			computeSolution();
		}
		return covarianceMatrix;
	}
	
	

}

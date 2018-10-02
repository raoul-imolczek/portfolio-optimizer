package eu.eisti.p2k19.fintech.portfolio.main;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.linear.RealVector;

import eu.eisti.p2k19.fintech.portfolio.exceptions.IncorrectQuotationsFileException;
import eu.eisti.p2k19.fintech.portfolio.model.Quotation;
import eu.eisti.p2k19.fintech.portfolio.optimizer.PortfolioOptimizer;
import eu.eisti.p2k19.fintech.portfolio.util.FileReaderUtil;

public class TestApp {

	public static void main(String[] args) {
		
		try {
			
			Map<String, List<Quotation>> quotationsMap = new HashMap<String, List<Quotation>>();
			
			FileReaderUtil util = new FileReaderUtil();
			quotationsMap.put("AAPL", util.readValues(getReader("AAPL")));
			quotationsMap.put("ALV.DE", util.readValues(getReader("ALV.DE")));
			quotationsMap.put("BNP.PA", util.readValues(getReader("BNP.PA")));
			quotationsMap.put("GOOG", util.readValues(getReader("GOOG")));
			quotationsMap.put("KO", util.readValues(getReader("KO")));
			quotationsMap.put("MCD", util.readValues(getReader("MCD")));
			quotationsMap.put("MSFT", util.readValues(getReader("MSFT")));
			quotationsMap.put("NKE", util.readValues(getReader("NKE")));

			Map<String, Double> expectedProfitability = new HashMap<String, Double>();
			expectedProfitability.put("AAPL", 234.63 / 225.74);
			expectedProfitability.put("ALV.DE", 213.69 / 192.00);
			expectedProfitability.put("BNP.PA", 68.16 / 52.71);
			expectedProfitability.put("GOOG", 1354.00 / 1193.47);
			expectedProfitability.put("KO", 50.89 / 46.19);
			expectedProfitability.put("MCD", 183.62 / 167.29);
			expectedProfitability.put("MSFT", 123.55 / 114.37);
			expectedProfitability.put("NKE", 87.53 / 84.72);

			double expectedPortfolioProfitability = 1.5;

			PortfolioOptimizer optimizer = new PortfolioOptimizer(quotationsMap, expectedProfitability, expectedPortfolioProfitability);
			
			RealVector solution = optimizer.computeSolution();
			double variance = optimizer.getVariance();
			
			optimizer.printExpectedProfitabilities();
			
			System.out.println(optimizer.getCovarianceMatrix());
			
		} catch (IncorrectQuotationsFileException e) {
			e.printStackTrace();
		}
		
	}
	
	public static Reader getReader(String symbol) {
		String path = symbol + ".csv";
        Reader reader = new InputStreamReader(TestApp.class.getClassLoader().getResourceAsStream(path));
        return reader;
	}

	
}

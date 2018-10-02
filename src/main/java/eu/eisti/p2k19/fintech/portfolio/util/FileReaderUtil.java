package eu.eisti.p2k19.fintech.portfolio.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.opencsv.CSVReader;

import eu.eisti.p2k19.fintech.portfolio.exceptions.IncorrectProfitabilityFileException;
import eu.eisti.p2k19.fintech.portfolio.exceptions.IncorrectQuotationsFileException;
import eu.eisti.p2k19.fintech.portfolio.model.Profitability;
import eu.eisti.p2k19.fintech.portfolio.model.Quotation;

public class FileReaderUtil {

	public List<Quotation> readValues(Reader reader) throws IncorrectQuotationsFileException {
		List<Quotation> result = new ArrayList<Quotation>();
		
        try {

            CSVReader csvReader = new CSVReader(reader);
            
            Iterator<String[]> lines = csvReader.iterator();

            lines.next();
            
            while(lines.hasNext()) {
            	String[] line = lines.next();
            	
            	String date = line[0];
            	int year = Integer.parseInt(date.substring(0, 4));
            	int month = Integer.parseInt(date.substring(5, 7));
            	int day = Integer.parseInt(date.substring(8, 10));
            	
            	if (!"null".equals(line[1])) {
            		Quotation quotation = new Quotation(LocalDate.of(year, month, day), Double.parseDouble(line[1]));
            		result.add(quotation);
            	}
            }

        	csvReader.close();
        	
		} catch (IOException e) {
			throw new IncorrectQuotationsFileException();
		}
		
		return result;
	}
	
	public double readEstimatedProfitability(Reader reader, String symbol) throws IncorrectProfitabilityFileException {
        try {

            CSVReader csvReader = new CSVReader(reader);
            
            Iterator<String[]> lines = csvReader.iterator();

            lines.next();
            
            while(lines.hasNext()) {
            	String[] line = lines.next();
            	
            	String lineSymbol = line[0];
            	if(symbol.equals(lineSymbol)) {
                	csvReader.close();
            		return Double.parseDouble(line[1]);
            	}
            	
            }

        	csvReader.close();
			throw new IncorrectProfitabilityFileException();
        	
		} catch (IOException e) {
			throw new IncorrectProfitabilityFileException();
		}
	}

	public List<String> readSymbols() throws IncorrectProfitabilityFileException {
		
		List<String> result = new ArrayList<String> ();
		
		String path = "consensus.csv";
		
        try {

            Reader reader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(path));
            CSVReader csvReader = new CSVReader(reader);
            
            Iterator<String[]> lines = csvReader.iterator();

            lines.next();
            
            while(lines.hasNext()) {
            	String[] line = lines.next();
            	result.add(line[0]);
            }

        	csvReader.close();
        	
		} catch (IOException e) {
			throw new IncorrectProfitabilityFileException();
		}

        return result;
	}
		
}

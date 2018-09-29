package eu.eisti.p2k19.fintech.portfolio.model;

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

public class Profitability {
	
	private LocalDate date;
	private double value;
	
	public LocalDate getDate() {
		return date;
	}
	
	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	
	public Profitability(LocalDate date, double value) {
		super();
		this.date = date;
		this.value = value;
	}

}

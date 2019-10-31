package br.imd.aqueducte.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class CsvToNGSILDUtil {

	private CsvToNGSILDUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static List<HashMap<String, Object>> convertCsvToJson(InputStreamReader inputStreamReader, int limit) {
		List<HashMap<String, Object>> listOfObjects = new ArrayList<>();
		try {
			// Create object of filereader
			// class with csv file as parameter.
			BufferedReader rd = new BufferedReader(inputStreamReader);

			// create csvParser object with
			// custom separator semi-colon
			CSVParser parser = new CSVParserBuilder().build();

			// create csvReader object with
			// parameter filereader and parser
			CSVReader csvReader = new CSVReaderBuilder(rd).withCSVParser(parser).build();
			
			int index = 0;
			List<String[]> allData = new ArrayList<String[]>();
			for (Iterator iterator = csvReader.iterator(); iterator.hasNext();) {
				allData.add((String[]) iterator.next());
				
				if (index == limit) break;
				
				if (index > 0)
					index++;
			}
			
//			List<String[]> allData = csvReader.readAll();

			HashMap<String, Object> csvToJson = new HashMap<>();

			listOfObjects = generateJson(csvToJson, allData, limit);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfObjects;
	}

	private static boolean isNumeric(String strNum) {
		try {
			Double.parseDouble(strNum);
		} catch (NumberFormatException | NullPointerException nfe) {
			return false;
		}
		return true;
	}

	private static List<HashMap<String, Object>> generateJson(
			HashMap<String, Object> csvToJsonNSGILD,
			List<String[]> allData,
			int limit) {
		List<HashMap<String, Object>> listOfObjects = new ArrayList<>();
		List<String> rowPropertiesName = null;

		int index = 0;
		for (String[] row : allData) {

			if (index == 0) {
				rowPropertiesName = Arrays.asList(row).stream().filter(key -> key != null || !"".equals(key))
						.map(key -> {
							return key.replace(" ", "_").toLowerCase().trim();
						}).collect(Collectors.toList());
			} else {
				int indexData = 0;
				for (Object cell : row) {

					if (cell == "" || cell.toString().length() == 0)
						cell = null;

					if (isNumeric((String) cell) && !rowPropertiesName.get(indexData).equals(""))
						csvToJsonNSGILD.put(rowPropertiesName.get(indexData), Double.parseDouble((String) cell));
					else
						csvToJsonNSGILD.put(rowPropertiesName.get(indexData), cell);

					indexData++;
				}
				listOfObjects.add(csvToJsonNSGILD);
				csvToJsonNSGILD = new HashMap<>();
			}
			
			if (limit == index) break;

			index++;
		}
		return listOfObjects;
	}

}

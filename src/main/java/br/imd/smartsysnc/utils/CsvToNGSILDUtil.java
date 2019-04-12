package br.imd.smartsysnc.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class CsvToNGSILDUtil {

	public static List<Object> convertCsvToNSGILD(InputStreamReader inputStreamReader) {

		List<Object> listOfObjects = new ArrayList<>();
		try {
			// Create object of filereader
			// class with csv file as parameter.
			BufferedReader rd = new BufferedReader(inputStreamReader);

			// create csvParser object with
			// custom seperator semi-colon
			CSVParser parser = new CSVParserBuilder().withSeparator(';').build();

			// create csvReader object with
			// parameter filereader and parser
			CSVReader csvReader = new CSVReaderBuilder(rd).withCSVParser(parser).build();

			// Read all data at once
			List<String[]> allData = csvReader.readAll();

			HashMap<Object, Object> csvToJsonNSGILD = new HashMap<>();

			List<String> contextList = Arrays.asList(
					"https://forge.etsi.org/gitlab/NGSI-LD/NGSI-LD/raw/master/coreContext/ngsi-ld-core-context.jsonld",
					"https://github.com/JorgePereiraUFRN/SGEOL-LD/blob/master/ngsi-ld/education/school/School_Context.jsonld");

			HashMap<Object, Object> value = new HashMap<>();
			HashMap<Object, Map<Object, Object>> atributos = new HashMap<>();

			String[] rowPropertiesName = null;
			int index = 0;
			for (String[] row : allData) {
				csvToJsonNSGILD.put("@context", contextList);
				csvToJsonNSGILD.put("id", "urn:ngsi-ld:layer:wheater:id");
				csvToJsonNSGILD.put("type", "wheater");

				if (row.length == 26) {
					if (row[0].trim().equals("data"))
						rowPropertiesName = row;
					else {
						for (Object cell : row) {

							if (cell.equals("sim"))
								cell = true;
							else if (cell.equals("não"))
								cell = false;

							if (cell == "" || cell.toString().length() == 0)
								cell = null;
							value.put("type", "Property");
							value.put("value",
									cell != null && cell.toString().contains(",")
											? Double.parseDouble(cell.toString().replace(",", "."))
											: cell);

							atributos.put(rowPropertiesName[index].trim().replace(" ", ""), value);
							value = new HashMap<Object, Object>();
							index++;
						}
						index = 0;
						csvToJsonNSGILD.putAll(atributos);
						listOfObjects.add(csvToJsonNSGILD);
						csvToJsonNSGILD = new HashMap<Object, Object>();

						atributos = new HashMap<Object, Map<Object, Object>>();
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfObjects;
	}

}

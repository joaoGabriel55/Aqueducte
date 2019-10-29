package br.imd.aqueducte.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class CsvToNGSILDUtil {

	private CsvToNGSILDUtil() {
		throw new IllegalStateException("Utility class");
	}

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

			listOfObjects = generateJson(csvToJsonNSGILD, allData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfObjects;
	}

	private static List<Object> generateJson(HashMap<Object, Object> csvToJsonNSGILD, List<String[]> allData) {
		List<Object> listOfObjects = new ArrayList<>();
		List<String> contextList = initContextList();
		HashMap<Object, Object> value = new HashMap<>();
		HashMap<Object, Map<Object, Object>> atributos = new HashMap<>();
		List<String> rowPropertiesName = null;

		int index = 0;
		for (String[] row : allData) {
			UUID uuid = UUID.randomUUID();
			initParams(csvToJsonNSGILD, contextList, uuid.toString());

			if (row[0].trim().equals("Estacao")) {
				rowPropertiesName = Arrays.asList(row).stream().filter(key -> key != null || !"".equals(key))
						.map(key -> {
							String keyFinal = key.replace(" ", "").trim();
							return keyFinal.toLowerCase();
						}).collect(Collectors.toList());
			} else {
				for (Object cell : row) {

					if (cell == "" || cell.toString().length() == 0)
						cell = null;

					value.put("type", "Property");
					if (rowPropertiesName.get(index).equals("hora") && cell != null) {
						value.put("value", FormatterUtils.getHourFormat(cell.toString()));
					} else {
						value.put("value", cell);
					}

					if (!rowPropertiesName.get(index).equals(""))
						atributos.put(rowPropertiesName.get(index), value);

					value = new HashMap<>();
					index++;
				}
				index = 0;
				csvToJsonNSGILD.putAll(atributos);
				listOfObjects.add(csvToJsonNSGILD);
				csvToJsonNSGILD = new HashMap<>();

				atributos = new HashMap<>();
			}
		}
		return listOfObjects;
	}

	private static List<String> initContextList() {
		return Arrays.asList(
				"https://forge.etsi.org/gitlab/NGSI-LD/NGSI-LD/raw/master/coreContext/ngsi-ld-core-context.jsonld",
				"https://github.com/JorgePereiraUFRN/SGEOL-LD/blob/master/ngsi-ld/education/school/School_Context.jsonld");

	}

	private static void initParams(HashMap<Object, Object> csvToJsonNSGILD, List<String> contextList, String uuid) {
		csvToJsonNSGILD.put("@context", contextList);
		csvToJsonNSGILD.put("id", "urn:ngsi-ld:layer:wheater:" + uuid);
		csvToJsonNSGILD.put("municipio", "Natal");
		csvToJsonNSGILD.put("type", "wheater");
	}
}

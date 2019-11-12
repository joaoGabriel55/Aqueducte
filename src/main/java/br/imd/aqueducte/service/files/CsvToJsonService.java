package br.imd.aqueducte.service.files;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import br.imd.aqueducte.service.files.FileConversionService;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.stereotype.Service;

@Service
public class CsvToJsonService implements FileConversionService {

    private String readDataFromFile(FileItemIterator fileItemIterator, int limit, String delimiter) throws IOException, FileUploadException {
        int countLoop = 0;
        String contentFile = null;
        System.out.println("Limit: " + limit);
        FileItemStream item = fileItemIterator.next();
        InputStream stream = item.openStream();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
            String contentFileTemp = null;
            while ((contentFileTemp = br.readLine()) != null && countLoop <= limit) {
                if (contentFileTemp.contains("|"))
                    contentFile = contentFile + contentFileTemp.replace(delimiter, ",") + "\n";
                else
                    contentFile = contentFile + contentFileTemp + "\n";

                System.out.println(contentFile);
                ++countLoop;
            }
            stream.close();
            return contentFile;
        }

    }

    @Override
    public List<HashMap<String, Object>> convertToJson(FileItemIterator fileItemIterator, int limit, String delimiter) {
        List<HashMap<String, Object>> listOfObjects = new ArrayList<>();
        try {
            String contentFile = readDataFromFile(fileItemIterator, limit, delimiter);

            try (CSVReader reader = new CSVReader(new StringReader(contentFile))) {
                List<String[]> allData;
                allData = reader.readAll();
                HashMap<String, Object> csvToJson = new HashMap<>();

                listOfObjects = generateJson(csvToJson, allData, limit);
                System.gc();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return listOfObjects;
    }

    private boolean isNumeric(String strNum) {
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    private List<HashMap<String, Object>> generateJson(
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

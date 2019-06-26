package br.imd.smartsysnc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.mongodb.connection.Stream;

public class ListMapTest {

	public static void main(String[] args) {
		// Objs
		// ============================================================
		LinkedHashMap<Object, Object> hashMap = new LinkedHashMap<>();
		hashMap.put("id1", 123);
		hashMap.put("id2", 456);
		hashMap.put("id3", 789);

		LinkedHashMap<Object, Object> hashMap2 = new LinkedHashMap<>();
		hashMap2.put("id1", 159);
		hashMap2.put("id2", 951);
		hashMap2.put("id3", 456);

		// Obj to add
		// ============================================================
		LinkedHashMap<Object, Object> hashMap3 = new LinkedHashMap<>();
		hashMap3.put("id1", 123);
		hashMap3.put("id2", 456);
		hashMap3.put("id3", 789);

		LinkedHashMap<Object, Object> hashMap4 = new LinkedHashMap<>();
		hashMap4.put("id1", 159);
		hashMap4.put("id2", 145);
		hashMap4.put("id3", 456);
		// ============================================================

		List<Map<Object, Object>> obj = Arrays.asList(hashMap, hashMap2);

		List<Map<Object, Object>> objToAdd = Arrays.asList(hashMap3, hashMap4);

		// convert inside the map() method directly.
		List<Map<Object, Object>> result = new ArrayList<>();

		for (Map<Object, Object> mapToAdd : objToAdd) {

			for (Entry<Object, Object> elemToAdd : mapToAdd.entrySet()) {

				for (Map<Object, Object> map : obj) {

					int count = 0;
					for (Entry<Object, Object> elem : map.entrySet()) {
						
						if (!elemToAdd.equals(elem))
							count++;

					}
				
				}

			}

		}

		System.out.println(result);

	}

	/*
	 * result = obj.stream() .map(elem -> elem.entrySet().stream().filter(map ->
	 * map.getKey().equals("id1")) .collect(Collectors.toMap(p -> p.getKey(), p ->
	 * p.getValue()))) .collect(Collectors.toList());
	 */

}

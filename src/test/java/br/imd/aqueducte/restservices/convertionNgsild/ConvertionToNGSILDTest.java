package br.imd.aqueducte.restservices.convertionNgsild;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import br.imd.aqueducte.AqueducteApplicationTests;
import br.imd.aqueducte.utils.RequestsUtils;

public class ConvertionToNGSILDTest extends AqueducteApplicationTests {

	private static String PATH = "/br/imd/smartsysnc/restservices/convertionNgsild/";

	@Override
	@Before
	public void setUp() {
		super.setUp();
	}

	@Test
	public void convertToNSILDService() throws Exception {
		InputStreamReader is = new InputStreamReader(getClass().getResourceAsStream(PATH + "payload1000.json"),
				"utf-8");
		BufferedReader buf = new BufferedReader(is);

		String line = buf.readLine();
		StringBuilder sb = new StringBuilder();

		while (line != null) {
			sb.append(line).append("\n");
			line = buf.readLine();
		}

		String jsonFile = sb.toString();
		String uri = "/sync/withoutContextSetup/convertToNgsild/daslkdskadkla";
		String inputJson = super.mapToJson(jsonFile);

		HttpHeaders headers = new HttpHeaders();
		headers.add(RequestsUtils.USER_TOKEN, "41fd004050058b292db9c0cff118e724daff481c");

		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE)
				.headers(headers).content(inputJson)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(201, status);
		String content = mvcResult.getResponse().getContentAsString();
		assertEquals(content, "Product is created successfully");
	}

}

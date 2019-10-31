package br.imd.aqueducte;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public abstract class AqueducteApplicationTests {

//	protected MockMvc mvc;
//	@Autowired
//	WebApplicationContext webApplicationContext;
//
//	protected void setUp() {
//		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//	}
//
//	protected String mapToJson(Object obj) throws JsonProcessingException {
//		ObjectMapper objectMapper = new ObjectMapper();
//		return objectMapper.writeValueAsString(obj);
//	}
//
//	protected <T> T mapFromJson(String json, Class<T> clazz)
//			throws JsonParseException, JsonMappingException, IOException {
//
//		ObjectMapper objectMapper = new ObjectMapper();
//		return objectMapper.readValue(json, clazz);
//	}

}

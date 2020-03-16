package br.imd.aqueducte.controllers.location;

import br.imd.aqueducte.treats.location.MunicipioEntityNGSILDTreat;
import br.imd.aqueducte.utils.MessageUtils;
import br.imd.aqueducte.utils.RequestsUtils;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.List;

import static br.imd.aqueducte.config.PropertiesParams.URL_SGEOL;

@RestController
@RequestMapping("/sync")
@CrossOrigin(origins = "*")
public class AqueducteLocationController {

    @PostMapping(value = "/jsonStateRN")
    public ResponseEntity<Object> getJsonStateRN() throws MalformedURLException, IOException {
        RequestsUtils requestsUtils = new RequestsUtils();
        InputStreamReader is = new InputStreamReader(
                getClass().getResourceAsStream("/br/imd/smartsysnc/utils/rn_geojson.json"), "utf-8");
        try {
            MunicipioEntityNGSILDTreat municipioEntityNGSILDTreat = new MunicipioEntityNGSILDTreat();
            BufferedReader rd = new BufferedReader(is);
            String jsonText = requestsUtils.readAll(rd);
            JSONObject json = new JSONObject(jsonText);

            List<Object> listStatesNGSILD = municipioEntityNGSILDTreat
                    .converterStateRNJsonToEntityNGSILD(json.getJSONArray("features").toList());

            RestTemplate rt = new RestTemplate();
            rt.getMessageConverters().add(new StringHttpMessageConverter());

            String url = URL_SGEOL + "municipio";

            for (int i = 0; i < listStatesNGSILD.size(); i++) {
                rt.postForEntity(url, listStatesNGSILD.get(i), String.class);
            }

            return MessageUtils.sendMessage("Data was imported!", HttpStatus.OK);

        } catch (Exception e) {
            return MessageUtils.sendMessage("Error on importation", HttpStatus.BAD_REQUEST);
        } finally {
            is.close();
        }
    }

}

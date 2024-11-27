package by.victory.myapp.service;

import by.victory.myapp.domain.Positioning;
import by.victory.myapp.domain.Statement;
import by.victory.myapp.domain.Trip;
import by.victory.myapp.repository.PositioningRepository;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class PositioningAPIService {

    private final String url = "http://localhost:3000/api/bestCoordinates";

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final PositioningRepository positioningRepository;

    public PositioningAPIService(PositioningRepository positioningRepository) {
        this.positioningRepository = positioningRepository;
    }

    public JSONObject buildPositioningJsonObject(Trip trip, List<Statement> statements) {
        log.debug("Build positioning json object: {}, {}", trip, statements);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("authorizedCapital", trip.getAuthorizedCapital());
        jsonObject.put("threshold", trip.getThreshold());
        JSONArray customers = new JSONArray();

        for (Statement statement : statements) {
            JSONObject coordinates = new JSONObject();
            coordinates.put("latitude", statement.getPositioning().getLatitude());
            coordinates.put("longitude", statement.getPositioning().getLongitude());

            JSONObject customer = new JSONObject();
            customer.put("coordinates", coordinates);
            customer.put("transportTariff", statement.getTransportTariff());
            customer.put("productVolume", statement.getDeliveryScope());
            customers.put(customer);
        }
        log.debug("Json positioning object is built: {}, {}", trip, statements);

        jsonObject.put("customers", customers);
        return jsonObject;
    }

    public Positioning getPositioningFromAPI(JSONObject jsonObject) {
        log.debug("REST request to get best positioning: {}", jsonObject);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<String>(jsonObject.toString(), headers);
        ResponseEntity<Positioning> response = restTemplate.postForEntity(url, request, Positioning.class);
        Positioning positioning = response.getBody();

        return positioningRepository.save(positioning);
    }
}

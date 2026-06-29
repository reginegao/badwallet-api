package badwallet_api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class PaymentClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String PAYMENT_URL = "http://localhost:8081/api/factures";

    // Récupérer factures impayées du mois en cours
    public List<Map> getFacturesMoisCourant(String walletCode, String unite) {
        String url = PAYMENT_URL + "/" + walletCode + "/current";
        if (unite != null) {
            url += "?unite=" + unite;
        }
        ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
        return response.getBody();
    }

    // Récupérer factures sur une période
    public List<Map> getFacturesPeriode(String walletCode, String debut, String fin) {
        String url = PAYMENT_URL + "/" + walletCode + "/periode?debut=" + debut + "&fin=" + fin;
        ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
        return response.getBody();
    }

    // Payer facture du mois en cours
    public Map payerFactureMoisCourant(String walletCode, String serviceNom) {
        String url = PAYMENT_URL + "/pay/current";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = new HashMap<>();
        body.put("walletCode", walletCode);
        body.put("serviceNom", serviceNom);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        return response.getBody();
    }

    // Payer factures par références
    public List<Map> payerFacturesParReference(List<String> references) {
        String url = PAYMENT_URL + "/pay/references";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new HashMap<>();
        body.put("references", references);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<List> response = restTemplate.postForEntity(url, request, List.class);
        return response.getBody();
    }
}
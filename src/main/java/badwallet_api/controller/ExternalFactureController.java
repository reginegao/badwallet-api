package badwallet_api.controller;

import badwallet_api.service.PaymentClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/external/factures")
public class ExternalFactureController {

    @Autowired
    private PaymentClient paymentClient;

    // Factures impayées du mois en cours
    @GetMapping("/{walletCode}/current")
    public ResponseEntity<List<Map>> getFacturesMoisCourant(
            @PathVariable String walletCode,
            @RequestParam(required = false) String unite) {
        return ResponseEntity.ok(
            paymentClient.getFacturesMoisCourant(walletCode, unite));
    }

    // Factures impayées sur une période
    @GetMapping("/{walletCode}/periode")
    public ResponseEntity<List<Map>> getFacturesPeriode(
            @PathVariable String walletCode,
            @RequestParam String debut,
            @RequestParam String fin) {
        return ResponseEntity.ok(
            paymentClient.getFacturesPeriode(walletCode, debut, fin));
    }
}
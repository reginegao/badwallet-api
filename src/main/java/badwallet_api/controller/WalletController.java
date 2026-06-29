package badwallet_api.controller;

import badwallet_api.model.Wallet;
import badwallet_api.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    @Autowired
    private WalletService walletService;

    // 1. Créer un wallet
    @PostMapping
    public ResponseEntity<Wallet> createWallet(@RequestBody Wallet wallet) {
        Wallet created = walletService.createWallet(wallet);
        return ResponseEntity.ok(created);
    }

    // 2. Lister les wallets avec pagination
    @GetMapping
    public ResponseEntity<Page<Wallet>> getAllWallets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(walletService.getAllWallets(page, size));
    }

    // 3. Consulter un wallet par téléphone
    @GetMapping("/{phoneNumber}")
    public ResponseEntity<Wallet> getWalletByPhone(@PathVariable String phoneNumber) {
        Optional<Wallet> wallet = walletService.getWalletByPhone(phoneNumber);
        return wallet.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. Consulter le solde
    @GetMapping("/{phoneNumber}/balance")
    public ResponseEntity<Double> getBalance(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(walletService.getBalance(phoneNumber));
    }
}
package badwallet_api.controller;

import badwallet_api.model.Transaction;
import badwallet_api.model.Wallet;
import badwallet_api.service.WalletSeederService;
import badwallet_api.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletSeederService walletSeederService;

    // 1. Seeder
    @PostMapping("/seed")
    public ResponseEntity<String> seed(
            @RequestParam(defaultValue = "10") int numWallets,
            @RequestParam(defaultValue = "100") int eventsPerWallet) {
        return ResponseEntity.ok(walletSeederService.seed(numWallets, eventsPerWallet));
    }

    // 2. Créer un wallet
    @PostMapping
    public ResponseEntity<Wallet> createWallet(@RequestBody Wallet wallet) {
        return ResponseEntity.ok(walletService.createWallet(wallet));
    }

    // 3. Lister les wallets avec pagination
    @GetMapping
    public ResponseEntity<Page<Wallet>> getAllWallets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(walletService.getAllWallets(page, size));
    }

    // 4. Consulter un wallet par téléphone
    @GetMapping("/{phoneNumber}")
    public ResponseEntity<Wallet> getWalletByPhone(@PathVariable String phoneNumber) {
        Optional<Wallet> wallet = walletService.getWalletByPhone(phoneNumber);
        return wallet.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. Consulter le solde
    @GetMapping("/{phoneNumber}/balance")
    public ResponseEntity<Double> getBalance(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(walletService.getBalance(phoneNumber));
    }

    // 6. Dépôt
    @PostMapping("/{id}/deposit")
    public ResponseEntity<Wallet> deposit(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        Double amount = Double.valueOf(body.get("amount").toString());
        String paymentMethod = body.get("paymentMethod").toString();
        return ResponseEntity.ok(walletService.deposit(id, amount, paymentMethod));
    }

    // 7. Retrait
    @PostMapping("/withdraw")
    public ResponseEntity<Wallet> withdraw(@RequestBody Map<String, Object> body) {
        String phoneNumber = body.get("phoneNumber").toString();
        Double amount = Double.valueOf(body.get("amount").toString());
        return ResponseEntity.ok(walletService.withdraw(phoneNumber, amount));
    }

    // 8. Transfert
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody Map<String, Object> body) {
        String senderPhone = body.get("senderPhone").toString();
        String receiverPhone = body.get("receiverPhone").toString();
        Double amount = Double.valueOf(body.get("amount").toString());
        walletService.transfer(senderPhone, receiverPhone, amount);
        return ResponseEntity.ok("Transfert effectué avec succès");
    }

    // 9. Historique des transactions
    @GetMapping("/{phoneNumber}/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(
            @PathVariable String phoneNumber) {
        return ResponseEntity.ok(walletService.getTransactions(phoneNumber));
    }
}
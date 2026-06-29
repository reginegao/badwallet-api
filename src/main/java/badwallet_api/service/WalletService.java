package badwallet_api.service;

import badwallet_api.model.Wallet;
import badwallet_api.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    // Créer un wallet
    public Wallet createWallet(Wallet wallet) {
        if (walletRepository.existsByPhoneNumber(wallet.getPhoneNumber())) {
            throw new RuntimeException("Ce numéro de téléphone existe déjà");
        }
        if (walletRepository.existsByEmail(wallet.getEmail())) {
            throw new RuntimeException("Cet email existe déjà");
        }
        return walletRepository.save(wallet);
    }

    // Lister les wallets avec pagination
    public Page<Wallet> getAllWallets(int page, int size) {
        return walletRepository.findAll(PageRequest.of(page, size));
    }

    // Consulter un wallet par téléphone
    public Optional<Wallet> getWalletByPhone(String phoneNumber) {
        return walletRepository.findByPhoneNumber(phoneNumber);
    }

    // Consulter le solde
    public Double getBalance(String phoneNumber) {
        Wallet wallet = walletRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Wallet non trouvé"));
        return wallet.getBalance();
    }
}
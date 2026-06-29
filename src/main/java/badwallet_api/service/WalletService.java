package badwallet_api.service;

import badwallet_api.model.Transaction;
import badwallet_api.model.Wallet;
import badwallet_api.repository.TransactionRepository;
import badwallet_api.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

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

    // Dépôt
    public Wallet deposit(Long id, Double amount, String paymentMethod) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wallet non trouvé"));
        wallet.setBalance(wallet.getBalance() + amount);
        walletRepository.save(wallet);

        Transaction t = new Transaction();
        t.setType("DEPOSIT");
        t.setAmount(amount);
        t.setFees(0.0);
        t.setPaymentMethod(paymentMethod);
        t.setReceiverPhone(wallet.getPhoneNumber());
        transactionRepository.save(t);

        return wallet;
    }

    // Retrait (frais 1% plafonné à 5000)
    public Wallet withdraw(String phoneNumber, Double amount) {
        Wallet wallet = walletRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Wallet non trouvé"));

        Double fees = Math.min(amount * 0.01, 5000.0);
        Double total = amount + fees;

        if (wallet.getBalance() < total) {
            throw new RuntimeException("Solde insuffisant");
        }

        wallet.setBalance(wallet.getBalance() - total);
        walletRepository.save(wallet);

        Transaction t = new Transaction();
        t.setType("WITHDRAW");
        t.setAmount(amount);
        t.setFees(fees);
        t.setSenderPhone(phoneNumber);
        transactionRepository.save(t);

        return wallet;
    }

    // Transfert
    public void transfer(String senderPhone, String receiverPhone, Double amount) {
        Wallet sender = walletRepository.findByPhoneNumber(senderPhone)
                .orElseThrow(() -> new RuntimeException("Wallet expéditeur non trouvé"));
        Wallet receiver = walletRepository.findByPhoneNumber(receiverPhone)
                .orElseThrow(() -> new RuntimeException("Wallet destinataire non trouvé"));

        if (sender.getBalance() < amount) {
            throw new RuntimeException("Solde insuffisant");
        }

        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);
        walletRepository.save(sender);
        walletRepository.save(receiver);

        Transaction t = new Transaction();
        t.setType("TRANSFER");
        t.setAmount(amount);
        t.setFees(0.0);
        t.setSenderPhone(senderPhone);
        t.setReceiverPhone(receiverPhone);
        transactionRepository.save(t);
    }

    // Historique des transactions
    public List<Transaction> getTransactions(String phoneNumber) {
        return transactionRepository
                .findBySenderPhoneOrReceiverPhone(phoneNumber, phoneNumber);
    }
}
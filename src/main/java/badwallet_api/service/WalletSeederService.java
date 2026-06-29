package badwallet_api.service;

import badwallet_api.model.Transaction;
import badwallet_api.model.Wallet;
import badwallet_api.repository.TransactionRepository;
import badwallet_api.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class WalletSeederService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public String seed(int numWallets, int eventsPerWallet) {
        Random random = new Random();
        String[] methods = {"CREDIT_CARD", "WALLET_TARGET"};

        for (int i = 1; i <= numWallets; i++) {
            // Créer wallet
            Wallet wallet = new Wallet();
            wallet.setPhoneNumber("+22177000000" + i);
            wallet.setEmail("client" + i + "@ism.sn");
            wallet.setCode(String.format("WLT-%07d", i));
            wallet.setCurrency("XOF");
            wallet.setBalance(50000.0 + (random.nextInt(200) * 1000));
            walletRepository.save(wallet);

            // Créer des transactions
            for (int j = 0; j < eventsPerWallet; j++) {
                Transaction t = new Transaction();
                int type = random.nextInt(3);
                if (type == 0) {
                    t.setType("DEPOSIT");
                    t.setAmount(5000.0 + random.nextInt(50) * 1000);
                    t.setFees(0.0);
                    t.setPaymentMethod(methods[random.nextInt(2)]);
                    t.setReceiverPhone(wallet.getPhoneNumber());
                } else if (type == 1) {
                    t.setType("WITHDRAW");
                    t.setAmount(1000.0 + random.nextInt(10) * 1000);
                    t.setFees(100.0);
                    t.setSenderPhone(wallet.getPhoneNumber());
                } else {
                    t.setType("TRANSFER");
                    t.setAmount(2000.0 + random.nextInt(20) * 1000);
                    t.setFees(0.0);
                    t.setSenderPhone(wallet.getPhoneNumber());
                    t.setReceiverPhone("+221770000001");
                }
                transactionRepository.save(t);
            }
        }
        return "Seeding terminé : " + numWallets + " wallets créés avec " 
               + eventsPerWallet + " transactions chacun";
    }
}
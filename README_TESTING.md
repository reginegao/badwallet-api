# BadWallet API & Payment Service - Guide de Test

## 📋 Vue d'ensemble du Projet

Ce projet est composé de **2 services Spring Boot**:

### 1. **BadWallet API** (Port 8080)
Gestion complète des portefeuilles numériques avec support des transactions, dépôts, retraits et paiements de factures.

### 2. **Payment Service** (Port 8081)
Microservice de gestion des factures (ISM, WOYAFAL) avec consultation et paiement.

---

## 🏗️ Architecture Git Flow

### BadWallet API - Branches Feature
```
develop (main branch)
├── feature/wallet-creation          → Créer/Consulter/Lister portefeuilles
├── feature/wallet-seeder            → Initialiser les données de test
├── feature/transaction-deposit      → Dépôts/Retraits/Transferts
├── feature/payment-services         → Paiement de factures (proxy)
└── feature/http-test-file           → Suite de tests HTTP
```

### Payment Service - Branches Feature
```
develop (main branch)
├── feature/factures-seeder          → Initialiser les factures
├── feature/factures-consultation    → Récupérer les factures
└── feature/factures-paiement        → Payer les factures
```

---

## 🚀 Installation et Démarrage

### Prérequis
- Java 17+
- Maven 3.8+
- Git

### 1️⃣ Cloner les projets
```bash
git clone https://github.com/reginegao/badwallet-api.git
git clone https://github.com/reginegao/payment-service.git
```

### 2️⃣ Compiler les projets
```bash
# BadWallet API
cd badwallet-api
mvn clean compile

# Payment Service
cd ../payment-service
mvn clean compile
```

### 3️⃣ Lancer les services
```bash
# Terminal 1 - BadWallet API (Port 8080)
cd badwallet-api
mvn spring-boot:run

# Terminal 2 - Payment Service (Port 8081)
cd payment-service
mvn spring-boot:run
```

Les services seront accessibles sur:
- **BadWallet API**: http://localhost:8080
- **Payment Service**: http://localhost:8081
- **H2 Console BadWallet**: http://localhost:8080/h2-console
- **H2 Console Payment**: http://localhost:8081/h2-console

---

## 🧪 Tester les Endpoints

### Avec VS Code REST Client (Recommandé)

1. Ouvrir le fichier `src/test.http` dans VS Code
2. Installer l'extension **REST Client** (Huachao Mao)
3. Cliquer sur **"Send Request"** au-dessus de chaque endpoint

### Avec cURL
```bash
# Exemple: Lister les portefeuilles
curl http://localhost:8080/api/wallets?page=0&size=10

# Exemple: Consulter le solde
curl http://localhost:8080/api/wallets/+221770000003/balance
```

### Avec Postman
Importer le fichier `src/test.http` ou configurer manuellement les endpoints.

---

## 📌 Endpoints Disponibles

### PARTIE 1: BadWallet API (Port 8080)

#### Gestion des Portefeuilles
| # | Endpoint | Méthode | Description |
|---|----------|---------|-------------|
| 1.1 | `/api/wallets/seed` | POST | Initialiser la BD avec des données de test |
| 1.2 | `/api/wallets` | POST | Créer un nouveau portefeuille |
| 1.3 | `/api/wallets?page=0&size=10` | GET | Lister tous les portefeuilles (paginé) |
| 1.4 | `/api/wallets/{phoneNumber}` | GET | Consulter un portefeuille |
| 1.5 | `/api/wallets/{phoneNumber}/balance` | GET | Consulter uniquement le solde |

#### Transactions
| # | Endpoint | Méthode | Description |
|---|----------|---------|-------------|
| 1.6 | `/api/wallets/{id}/deposit` | POST | Effectuer un dépôt |
| 1.7 | `/api/wallets/withdraw` | POST | Effectuer un retrait (frais 1%) |
| 1.8 | `/api/wallets/transfer` | POST | Transfert entre deux portefeuilles |
| 1.11 | `/api/wallets/{phoneNumber}/transactions` | GET | Historique des transactions |

#### Paiement de Factures
| # | Endpoint | Méthode | Description |
|---|----------|---------|-------------|
| 1.9 | `/api/wallets/pay` | POST | Payer facture du mois en cours |
| 1.10 | `/api/wallets/pay-factures` | POST | Payer des factures spécifiques |

### PARTIE 2: Proxy Factures dans BadWallet API (Port 8080)
| # | Endpoint | Méthode | Description |
|---|----------|---------|-------------|
| 2.1 | `/api/external/factures/{walletCode}/current` | GET | Factures impayées du mois en cours |
| 2.2 | `/api/external/factures/{walletCode}/current?unite=WOYAFAL` | GET | Factures filtrées par unité |
| 2.3 | `/api/external/factures/{walletCode}/periode?debut=...&fin=...` | GET | Factures sur une période |

### PARTIE 3: Payment Service (Port 8081)
| Endpoint | Méthode | Description |
|----------|---------|-------------|
| `/api/factures/seed/{walletCode}` | POST | Initialiser les factures |
| `/api/factures/{walletCode}/current` | GET | Factures du mois en cours |
| `/api/factures/{walletCode}/periode` | GET | Factures par période |
| `/api/factures/pay/current` | POST | Payer facture du mois |
| `/api/factures/pay/references` | POST | Payer par références |

---

## 🧑‍💼 Données de Test

### Créer un Portefeuille de Test
```json
POST http://localhost:8080/api/wallets

{
  "phoneNumber": "+221779998877",
  "email": "testclient@gmail.com",
  "balance": 25000,
  "code": "WLT-TEST001",
  "currency": "XOF"
}
```

### Effectuer un Dépôt
```json
POST http://localhost:8080/api/wallets/1/deposit

{
  "amount": 50000.00,
  "paymentMethod": "CREDIT_CARD"
}
```

### Effectuer un Retrait (Frais: 1% plafonné à 5000 CFA)
```json
POST http://localhost:8080/api/wallets/withdraw

{
  "phoneNumber": "+221770000001",
  "amount": 10000.00
}
```

### Transfert entre Portefeuilles
```json
POST http://localhost:8080/api/wallets/transfer

{
  "senderPhone": "+221770000001",
  "receiverPhone": "+221770000002",
  "amount": 2000.00
}
```

### Payer une Facture
```json
POST http://localhost:8080/api/wallets/pay

{
  "phoneNumber": "+221770000003",
  "serviceName": "ISM",
  "amount": 75000.00
}
```

---

## 🛠️ Troubleshooting

### Les services ne démarrent pas?
```bash
# Vérifier les ports
netstat -ano | findstr ":8080"
netstat -ano | findstr ":8081"

# Tuer le processus qui occupe le port
taskkill /PID <PID> /F
```

### Erreur de compilation?
```bash
# Nettoyer et reconstruire
mvn clean install

# Vérifier la version de Java
java -version
```

### Base de données vide?
Appelez l'endpoint `/api/wallets/seed` pour initialiser les données de test.

---

## 📚 Documentation Supplémentaire

- **Spring Boot**: https://spring.io/projects/spring-boot
- **H2 Database**: http://www.h2database.com/
- **REST Client**: https://marketplace.visualstudio.com/items?itemName=humao.rest-client

---

## ✅ Checklist de Vérification

- [ ] Les deux services compilent sans erreur
- [ ] BadWallet API démarre sur le port 8080
- [ ] Payment Service démarre sur le port 8081
- [ ] Les endpoints de test.http fonctionnent tous
- [ ] Les données sont bien persistées en H2
- [ ] Les transactions sont enregistrées correctement
- [ ] Les frais de retrait sont appliqués (1% max 5000)

---

## 👨‍💻 Auteur
Projet réalisé selon les consignes du professeur avec Git Flow et Structure de Microservices.

**Date**: 29 Juin 2026  
**Version**: 1.0

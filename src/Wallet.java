import java.security.*;
import java.util.Map;

public class Wallet {
    //Attribute
    private PublicKey publicKey;
    private PrivateKey privateKey;

    private List myUTXOs; //Liste aller UTXOs, die dem Walletbesitzer gehoeren

    //Erstellen eines private und public Keys
    public Wallet () {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");                                               //Erstellt eine elliptic curve
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            keyGen.initialize(256,random);

            KeyPair keyBundle = keyGen.generateKeyPair();

            publicKey = keyBundle.getPublic();
            privateKey = keyBundle.getPrivate();

        } catch (Exception e) { throw new RuntimeException(e); }

    }

    //Alle UTXOs aus Nextcoin.java durchsuchen, und die die diesem Wallet gehoehren in der eigenen Liste speichern
    private void updateMyUTXOs() {
        List updatedUTXOs = new List();
        for(Map.Entry<String, tOutput> entry : Nextcoin.getAllUTXOs().entrySet()) {
            String key = entry.getKey();
            tOutput value = entry.getValue();

            if(value.isMine(publicKey)) {
                updatedUTXOs.append(value);
            }
        }

        myUTXOs = updatedUTXOs;
    }

    //Zusammenrechnen aller UTXOs die diesem Wallet gehoeren
    public double getBalance () {
        updateMyUTXOs();

        double balance = 0;
        myUTXOs.toFirst();
        while (myUTXOs.hasAccess()) {
                balance += ((tOutput)myUTXOs.getObject()).getAmount();
                myUTXOs.next();
        }
        return balance;
    }

    //Coins an ein anderes Wallet transferieren
    public Transaction sendCoins (PublicKey to, double amount) {
        if((getBalance() - amount) < 0) { System.out.println("Not enough money!"); return null; }
        List myInputs = new List();
        myUTXOs.toFirst();
        int balance = 0;
        while (myUTXOs.hasAccess()) {
            if (balance < amount) { //Fuege myUTXOs solange zu Inputs hinzu, bis Sendebetrag erreicht ist
                tInput newInput = new tInput(((tOutput)myUTXOs.getObject()));

                myInputs.append(newInput);

                Nextcoin.removeUTXO(((tOutput) myUTXOs.getObject()).getId());
                myUTXOs.remove();
            } else { break; }
        }
        //Erstelle neue Transaktion
        Transaction newTransaction = new Transaction(publicKey,to,amount,myInputs);
        newTransaction.sign(privateKey);
        return newTransaction;
    }

    //Getter Methoden
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}

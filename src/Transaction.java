import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Date;

public class Transaction {
    //Attribute
    private PublicKey from; //Sender
    private PublicKey to; //Empfaenger
    private double amount; //Betrag
    private String txID; //Transactions Id
    private long time; //Um bei gleichbleibenden Bedingungen trotzdem einen anderen hash zu bekommen
    private byte [] signature; //Signatur der Transaktion

    //Hier werden die In und Outputs der Transaktion gespeichert, damit man sie spaeter noch aufrufen kann
    private List inputs = new List();
    private List outputs = new List();


    //Dieser Konstruktor ist fuer die Coinbase transaktionen wichtig
    public Transaction(PublicKey from, PublicKey to, double amount, boolean genesis) {
        this.time = new Date().getTime(); this.from = from; this.to = to; this.amount = amount;
        this.txID = Helper.toSha256(time + Helper.publicKeyToString(from) + Helper.publicKeyToString(to) + amount);
        makeTransaction(genesis);
    }

    //Dieser Konstruktor is fuer alle anderen Transaktionen
    public Transaction(PublicKey from, PublicKey to, double amount, List inputs) {
        this.time = new Date().getTime(); this.from = from; this.to = to; this.amount = amount;
        this.txID = Helper.toSha256(time + Helper.publicKeyToString(from) + Helper.publicKeyToString(to) + amount);
        if(inputs != null) { this.inputs = inputs; }
        makeTransaction(false);
    }


    //Signiert die Transaktion
    public void sign(PrivateKey privateKey) {
        try {
            Signature dsa = Signature.getInstance("SHA1withECDSA");//Algorithmus
            dsa.initSign(privateKey); //Womit wird signiert
            dsa.update((Helper.toSha256(Helper.publicKeyToString(from) + Helper.publicKeyToString(to)
                    + amount + txID)).getBytes()); //input der signiert werden soll
            signature = dsa.sign();
        } catch (Exception e) { throw new RuntimeException(e); } //falls die Signatur nicht funktioniert

    }
    //Verifiziert die Signierung
    public boolean verifySign() {
        try {
            Signature dsa = Signature.getInstance("SHA1withECDSA");
            dsa.initVerify(from);
            dsa.update((Helper.toSha256(Helper.publicKeyToString(from) + Helper.publicKeyToString(to) + amount + txID)).getBytes());
            return dsa.verify(signature);
        } catch (Exception e) { throw new RuntimeException(e); }

    }

    public void makeTransaction(boolean coinbase) {

        double change = inputValue() - amount;

        //Erstelle nun neue Outputs
        tOutput newOutput;
        tOutput changeOutput;
        if (change < 0 && !coinbase) {
            System.out.println("Zu großer Input: " + change); return;
        } else if (change > 0) {
            newOutput = new tOutput(amount, to, txID);
            changeOutput = new tOutput(change,from, txID);

            //Fuege neue Outputs zu UTXOs hinzu
            Nextcoin.addUTXO(newOutput.getId(),newOutput);
            Nextcoin.addUTXO(changeOutput.getId(),changeOutput);

            //Fuege sie zu Transaction outputs hinzu
            outputs.append(newOutput);
            outputs.append(changeOutput);
        } else {
            newOutput = new tOutput(amount, to, txID);

            //Fuege sie zu UTXOs hinzu
            Nextcoin.addUTXO(newOutput.getId(),newOutput);

            //Fuege sie zu Transaction outputs hinzu
            outputs.append(newOutput);
        }

        //Loesche inputs (alte Outputs) aus UTXOs
        inputs.toFirst();
        while (inputs.hasAccess()) {
            tInput inp = ((tInput)inputs.getObject());
            Nextcoin.removeUTXO(inp.getUTXO().getParentId());
            inputs.next();
        }
        return;
    }
    //Hilfsmethode die ueberprueft, wie groß die UTXOs von dem Sender sind
    private double inputValue() {
        double value = 0;
        inputs.toFirst();
        while (inputs.hasAccess()){
            tInput inp = ((tInput)inputs.getObject());
            value += inp.getUTXO().getAmount();
            inputs.next();
        }
        return value;
    }

    public String getTxID() {
        return txID;
    }
}
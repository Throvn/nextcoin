import java.security.PublicKey;

public class tOutput {
    //Attribute (Deklarieren)
    private String tId; //Eine eigene Output ID
    private double amount; //Betrag
    private PublicKey owner; // Besitzer dieses Outputs
    private String parentId; //Transaktions ID in der dieser Output erstellt wurde

    //Initialiseren der Attribute
    public tOutput(double amount, PublicKey owner, String parentTransactionId) {
        this.amount = amount;
        this.owner = owner;
        this.parentId = parentTransactionId;
        this.tId = (Helper.toSha256( amount + Helper.publicKeyToString(owner) + parentId));
    }

    //Somit kann man die Outputs schneller filtern
    public boolean isMine(PublicKey wallet) {
        return wallet == owner;
    }

    //Getter Methoden
    public double getAmount() {
        return amount;
    }

    public String getId() {
        return tId;
    }

    public String getParentId () {
        return parentId;
    }
}

import java.util.Date;

public class Block {
    //Attribute
    private String hash;
    private String prevHash;
    private List data = new List();
    private long time; //Speichern im UNIX-Epoch
    private int nonce; //


    public Block (String pPrevHash) {
        //Initialisieren
        prevHash = pPrevHash;
        time = new Date().getTime();

        hash = Helper.toSha256(prevHash + time + nonce);// Hash kalkulieren um, damit wir mineBlock() ueberhaupt starten koennen
    }

    public String getHash() {
        return hash;
    }

    public String getPrevHash() { return prevHash; }

    public List getData() { return data; }


    public void mineBlock(int difficulty) {
        //SO viele Nullen erstellen, wie es die Difficulty verlangt
        String target = "";
        for (int i = 0; i < difficulty; i++) { target += '0'; }

        //Loesen des Raetzels um den Block zur Blockchain hinzufuegen zu koennen
        while(!this.hash.substring(0,difficulty).equals(target)){
            this.nonce++;
            hash = Helper.toSha256(data + prevHash + time + nonce);
        }
        System.out.println("\nBlock mined: "+ this.hash);
    }

    //Eine Transaktion zu dem Block hinzufuegen
    public boolean addTransaction(Transaction pTransaction) {
        if (pTransaction == null) { System.out.println("Transaction was invalid"); return false; }
        if (pTransaction.verifySign()) { data.append(pTransaction); return true;}
        return false;
    }
}
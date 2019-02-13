import java.util.HashMap;
public class Nextcoin {
    //Attribute:
    private static List blocks = new List();
    final private static int difficulty = 5; //time of adding a block
    private static HashMap<String, tOutput> UTXOs = new HashMap<>();

    private static Wallet coinbase = new Wallet();

    //Fuegt pBlock zur Blockchain (blocks) hinzu
    public static void addBlock(Block pBlock) {
        pBlock.mineBlock(difficulty);
        blocks.append(pBlock);
    }

    //Ausfuermethode
    public static void main(String args[]) {
        //Create Wallets
        Wallet coinbase = new Wallet();
        Wallet larrysWallet = new Wallet();
        Wallet billsWallet = new Wallet();
        Wallet walmartsWallet = new Wallet();

        //Make genesisTransaction (Larry gets 100 LOU)
        System.out.println("Attempting to create and mine Genesis Block...");
        Transaction genesisTransaction = new Transaction(coinbase.getPublicKey(),larrysWallet.getPublicKey(),100.00,true);
        genesisTransaction.sign(coinbase.getPrivateKey());
        Block genesisBlock = new Block("0");
        genesisBlock.addTransaction(genesisTransaction);
        addBlock(genesisBlock);

        //Get Account Balances
        System.out.println("\n ---- Coinbase transfers 100 LOU to Larry ---- ");
        System.out.println("Larry's Balance: " + larrysWallet.getBalance());
        System.out.println("Bill's Balcance: " + billsWallet.getBalance());


        System.out.println("\n ---- Larry tries to transfer 4 LOU to Bill ---- ");
        Block block1 = new Block(genesisBlock.getHash());
        block1.addTransaction(larrysWallet.sendCoins(billsWallet.getPublicKey(),4.0));
        addBlock(block1); //Now Transactions are stored
        System.out.println("Larry's Balance: " + larrysWallet.getBalance());
        System.out.println("Bill's Balcance: " + billsWallet.getBalance());

        //Multiple Transactions are also possible
        System.out.println("\n ---- Bill buys bubblegum for 1.25 LOU & Larry buys vegetables for 20.99 LOU from Walmart ---- ");
        Block block2 = new Block(block1.getHash());
        block1.addTransaction(billsWallet.sendCoins(walmartsWallet.getPublicKey(),1.25));
        block1.addTransaction(larrysWallet.sendCoins(walmartsWallet.getPublicKey(),20.99));
        addBlock(block2);

        System.out.println("\n ---- Bill sends 1000 LOU to Walmart ---- ");
        Block block3 = new Block(block2.getHash());
        block3.addTransaction(billsWallet.sendCoins(walmartsWallet.getPublicKey(),1000));
        addBlock(block3);

        System.out.println("Larry's Balance: " + larrysWallet.getBalance());
        System.out.println("Bill's Balcance: " + billsWallet.getBalance());
        System.out.println("Walmart's Balance: " + walmartsWallet.getBalance());

        System.out.println("\n == Is Chain valid? == ");
        System.out.println(isValid());
    }

    public static HashMap<String, tOutput> getAllUTXOs() { return UTXOs; }

    public static void addUTXO(String key, tOutput UTXO) {
        UTXOs.put(key, UTXO);
    }

    public static void removeUTXO(String key) {
        UTXOs.remove(key);
    }

    //Prueft ob die Chain auch wirklich gueltig ist
    public static boolean isValid() {
        blocks.toFirst();
        if (((Block)blocks.getObject()).getPrevHash() != "0") return false;//Pruefe Genesisblock

        while (blocks.hasAccess()) {

            Block firstBlock = ((Block)blocks.getObject());
            List blockData = ((Block)blocks.getObject()).getData();
            blockData.toFirst();

            //Pruefe Transaktionen auf Richtigkeit
            while (blockData.hasAccess()) {
                Transaction transaction = (Transaction) blockData.getObject();
                if (!transaction.verifySign()) { System.out.println("Transacion:" + transaction.getTxID() + " was tempered with!"); return false; }
                blockData.next();
            }

            blocks.next();
            if ((Block)blocks.getObject() == null) continue; // Springt zum Ende der Schleife (Ueberspringt die naechsten beiden Statments)
            String secondHash = ((Block)blocks.getObject()).getPrevHash();
            if (!firstBlock.getHash().equals(secondHash)) return false; //Pruefe Hash uebereinstimmung
        }
        return true;
    }
}
public class tInput {
    private tOutput UTXO;

    public tInput (tOutput pOutput) {
        this.UTXO = pOutput;
    }

    public tOutput getUTXO() {
        return UTXO;
    }
}

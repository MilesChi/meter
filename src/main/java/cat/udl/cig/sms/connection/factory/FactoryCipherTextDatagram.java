package cat.udl.cig.sms.connection.factory;

import cat.udl.cig.cryptography.cryptosystems.ciphertexts.ElGamalCiphertext;
import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.RingElement;
import cat.udl.cig.sms.connection.datagram.CipherTextDatagram;
import cat.udl.cig.sms.data.LoadCurve;

import java.util.Arrays;

/**
 * Builds the CypherTextDatagram
 */
public class FactoryCipherTextDatagram implements FactorySMSDatagram {

    private final int LENGTH_CURVE;
    private static final int NUM_POINTS = 2 * 2;
    private final LoadCurve loadCurve;

    /**
     * @param loadCurve to get the information of the ECC
     */
    public FactoryCipherTextDatagram(LoadCurve loadCurve) {
        LENGTH_CURVE = loadCurve.getCurve().getCardinalityFactors().get(0).bitLength() / 8 + 1;
        this.loadCurve = loadCurve;
    }

    /**
     * @param bytes that represent the Ciphertext as the pair of two GeneralECPoint
     * @return the new CipherTextDatagram constructed.
     */
    @Override
    public CipherTextDatagram buildDatagram(byte[] bytes) {
        RingElement cx = fromBytes(bytes, 0, LENGTH_CURVE);
        RingElement cy = fromBytes(bytes, LENGTH_CURVE, LENGTH_CURVE * 2);
        RingElement dx = fromBytes(bytes, LENGTH_CURVE * 2, LENGTH_CURVE * 3);
        RingElement dy = fromBytes(bytes, LENGTH_CURVE * 3, LENGTH_CURVE * 4);
        GeneralECPoint c = new GeneralECPoint(loadCurve.getCurve(), cx, cy);
        GeneralECPoint d = new GeneralECPoint(loadCurve.getCurve(), dx, dy);
        ElGamalCiphertext ciphertext = new ElGamalCiphertext(new GeneralECPoint[]{c, d});
        return new CipherTextDatagram(ciphertext);
    }

    private RingElement fromBytes(byte[] bytes, int from, int to) {
        return loadCurve.getField().fromBytes(Arrays.copyOfRange(bytes, from, to));
    }

    /**
     * @return the size of the content of the datagram.
     */
    @Override
    public int getByteSize() {
        return LENGTH_CURVE * NUM_POINTS;
    }
}
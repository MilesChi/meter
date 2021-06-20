package cat.udl.cig.sms.recsi.substation.states;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.busom.SubstationBusomControllerInt;
import cat.udl.cig.sms.connection.ConnectionSubstation;
import cat.udl.cig.sms.crypt.CurveConfiguration;
import cat.udl.cig.sms.recsi.substation.SubstationContext;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeyEstablishmentSubstationTest {

    private SubstationContext substationContext;
    private final BigInteger SUM = BigInteger.TEN;
    private BigInteger EXPECTED_PRIVATE_KEY;

    @BeforeEach
    void setUp() throws IOException, NullMessageException {
        SubstationBusomControllerInt controller = Mockito.mock(SubstationBusomControllerInt.class);
        Mockito.when(controller.receiveSecretKey()).then((Answer<BigInteger>) invoc -> SUM);
        ConnectionSubstation connection = Mockito.mock(ConnectionSubstation.class);
        substationContext = new SubstationContext(CurveConfiguration.P192(), connection);
        BigInteger order = substationContext.getLoadCurve().getGroup().getSize();
        EXPECTED_PRIVATE_KEY = SUM.negate().add(order).remainder(order);
        substationContext.setSubstationBusomControllerInt(controller);
    }

    @Test
    void next() throws IOException, NullMessageException {
        substationContext.startKeyEstablishment();
        Optional<BigInteger> privateKey = substationContext.getPrivateKey();
        assertTrue(privateKey.isPresent());
        assertEquals(EXPECTED_PRIVATE_KEY, privateKey.get());
    }
}
package udl.cig.sms.connection;

import udl.cig.sms.connection.datagram.SMSDatagram;

import java.io.IOException;

public interface Receiver {

    SMSDatagram receive() throws IOException;
}

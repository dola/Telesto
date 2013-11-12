package ch.ethz.syslab.telesto.client;

import java.io.IOException;

import ch.ethz.syslab.telesto.client.exception.ProcessingException;
import ch.ethz.syslab.telesto.common.protocol.Packet.UnknownMethodException;

public class Main {
    public static final void main(String[] args) throws IOException, UnknownMethodException, ProcessingException {
        ITelestoClient client = new TelestoClient();
        long time = 0;
        for (int i = 0; i < 100000; i++) {
            time += client.ping();
        }
        System.out.println(time / 1000000000D);
    }
}

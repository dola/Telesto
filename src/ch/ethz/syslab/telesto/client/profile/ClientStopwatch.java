package ch.ethz.syslab.telesto.client.profile;

import ch.ethz.syslab.telesto.common.protocol.Packet;
import ch.ethz.syslab.telesto.profile.BenchmarkLog;
import ch.ethz.syslab.telesto.profile.Stopwatch;

public class ClientStopwatch extends Stopwatch {
    public ClientStopwatch(BenchmarkLog log) {
        super(log);
    }

    private long sentMethodId;
    private long receivedMethodId;

    public void setReceivedPacket(Packet packet) {
        receivedMethodId = packet.methodId();
    }

    public void setSentPacket(Packet packet) {
        sentMethodId = packet.methodId();
    }

    @Override
    protected void flush() {
        // These phases are misused as an easy way to store method IDs in the log file.
        measurements[Phase.PARSING.ordinal()] = sentMethodId;
        measurements[Phase.RESPONSE.ordinal()] = receivedMethodId;
        super.flush();
    }
}

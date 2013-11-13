package ch.ethz.syslab.telesto.client.profile;

import ch.ethz.syslab.telesto.common.protocol.Packet;
import ch.ethz.syslab.telesto.profile.BenchmarkLog;
import ch.ethz.syslab.telesto.profile.Stopwatch;

public class ClientStopwatch extends Stopwatch {
    public ClientStopwatch(BenchmarkLog log) {
        super(log);
    }

    private long lastMethodId;

    public void setLastPacket(Packet packet) {
        lastMethodId = packet.methodId();
    }

    @Override
    protected void flush() {
        // The parsing phase is misused as an easy way to store method IDs in the log file.
        measurements[Phase.PARSING.ordinal()] = lastMethodId;
        super.flush();
    }
}

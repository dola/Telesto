package ch.ethz.syslab.telesto.server.network;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import ch.ethz.syslab.telesto.common.network.Connection;

public class ServerConnection extends Connection {
    private SelectionKey selectionKey;

    public ServerConnection(SocketChannel socket) {
        super(socket);
    }

    public void setSelectionKey(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    @Override
    public void disconnect() {
        selectionKey.cancel();
        super.disconnect();
    }
}

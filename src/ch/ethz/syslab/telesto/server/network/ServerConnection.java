package ch.ethz.syslab.telesto.server.network;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import ch.ethz.syslab.telesto.common.model.Client;
import ch.ethz.syslab.telesto.common.network.Connection;
import ch.ethz.syslab.telesto.server.controller.ServerAuthenticationProtocolHandler;
import ch.ethz.syslab.telesto.server.controller.ServerProtocolHandler;
import ch.ethz.syslab.telesto.server.db.Database;

public class ServerConnection extends Connection {
    private SelectionKey selectionKey;
    private Database database;

    public ServerConnection(SocketChannel socket, Database database) {
        super(socket);
        this.database = database;
        protocolHandler = new ServerAuthenticationProtocolHandler(database, this);
    }

    public void setSelectionKey(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    @Override
    public void disconnect() {
        selectionKey.cancel();
        super.disconnect();
    }

    public void setClient(Client client) {
        this.client = client;
        protocolHandler = new ServerProtocolHandler(database, client);
    }
}

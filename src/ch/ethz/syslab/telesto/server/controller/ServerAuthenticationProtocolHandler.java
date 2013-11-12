package ch.ethz.syslab.telesto.server.controller;

import java.util.List;

import ch.ethz.syslab.telesto.common.model.Client;
import ch.ethz.syslab.telesto.common.protocol.IdentifyClientPacket;
import ch.ethz.syslab.telesto.common.protocol.Packet;
import ch.ethz.syslab.telesto.common.protocol.PingPacket;
import ch.ethz.syslab.telesto.common.protocol.PongPacket;
import ch.ethz.syslab.telesto.common.protocol.RegisterClientPacket;
import ch.ethz.syslab.telesto.common.protocol.RegisterClientResponsePacket;
import ch.ethz.syslab.telesto.common.protocol.handler.IServerAuthenticationProtocolHandler;
import ch.ethz.syslab.telesto.common.protocol.handler.PacketProcessingException;
import ch.ethz.syslab.telesto.common.protocol.handler.ProtocolHandler;
import ch.ethz.syslab.telesto.server.db.Database;
import ch.ethz.syslab.telesto.server.db.procedure.ClientProcedure;

public class ServerAuthenticationProtocolHandler extends ProtocolHandler implements IServerAuthenticationProtocolHandler {

    private Database db;

    public ServerAuthenticationProtocolHandler(Database database) {
        db = database;
    }

    @Override
    public Packet handle(PingPacket packet) throws PacketProcessingException {
        return new PongPacket();
    }

    @Override
    public Packet handle(RegisterClientPacket packet) throws PacketProcessingException {
        int clientId = db.callSimpleProcedure(ClientProcedure.REQUEST_ID, packet.clientName, packet.mode);

        return new RegisterClientResponsePacket(clientId);
    }

    @Override
    public Packet handle(IdentifyClientPacket packet) throws PacketProcessingException {
        List<Client> res = db.callClientProcedure(ClientProcedure.IDENTIFY, packet.clientId);
        if (!res.isEmpty()) {
            // TODO: IdentifyClientResponsePacket is missing!
            // return new IdentifyClientResponsePacket(res.get(0));
        } else {
            throw new PacketProcessingException("Client id not found");
        }
        return null;
    }
}

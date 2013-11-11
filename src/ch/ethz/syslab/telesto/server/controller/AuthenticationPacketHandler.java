package ch.ethz.syslab.telesto.server.controller;

import java.util.List;

import ch.ethz.syslab.telesto.model.Client;
import ch.ethz.syslab.telesto.protocol.ComplexTestPacket;
import ch.ethz.syslab.telesto.protocol.CreateQueuePacket;
import ch.ethz.syslab.telesto.protocol.DeleteQueuePacket;
import ch.ethz.syslab.telesto.protocol.GetActiveQueuesPacket;
import ch.ethz.syslab.telesto.protocol.GetMessagesPacket;
import ch.ethz.syslab.telesto.protocol.GetQueueIdPacket;
import ch.ethz.syslab.telesto.protocol.GetQueueNamePacket;
import ch.ethz.syslab.telesto.protocol.GetQueuesPacket;
import ch.ethz.syslab.telesto.protocol.IdentifyClientPacket;
import ch.ethz.syslab.telesto.protocol.MessageTestPacket;
import ch.ethz.syslab.telesto.protocol.Packet;
import ch.ethz.syslab.telesto.protocol.PingPacket;
import ch.ethz.syslab.telesto.protocol.PongPacket;
import ch.ethz.syslab.telesto.protocol.PutMessagePacket;
import ch.ethz.syslab.telesto.protocol.QueueTestPacket;
import ch.ethz.syslab.telesto.protocol.ReadMessagePacket;
import ch.ethz.syslab.telesto.protocol.RegisterClientPacket;
import ch.ethz.syslab.telesto.protocol.RegisterClientResponsePacket;
import ch.ethz.syslab.telesto.server.db.Database;
import ch.ethz.syslab.telesto.server.db.procedure.ClientProcedure;

public class AuthenticationPacketHandler implements IPacketHandler {

    private Database db;

    public AuthenticationPacketHandler(Database database) {
        db = database;
    }

    @Override
    public Packet handle(Packet packet) throws PacketProcessingException {
        throw new PacketProcessingException("Not authenticated");
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

    @Override
    public Packet handle(CreateQueuePacket packet) throws PacketProcessingException {
        return handle(packet);
    }

    @Override
    public Packet handle(DeleteQueuePacket packet) throws PacketProcessingException {
        return handle(packet);
    }

    @Override
    public Packet handle(GetQueueIdPacket packet) throws PacketProcessingException {
        return handle(packet);
    }

    @Override
    public Packet handle(GetQueueNamePacket packet) throws PacketProcessingException {
        return handle(packet);
    }

    @Override
    public Packet handle(GetQueuesPacket packet) throws PacketProcessingException {
        return handle(packet);
    }

    @Override
    public Packet handle(GetActiveQueuesPacket packet) throws PacketProcessingException {
        return handle(packet);
    }

    @Override
    public Packet handle(GetMessagesPacket packet) throws PacketProcessingException {
        return handle(packet);
    }

    @Override
    public Packet handle(PutMessagePacket packet) throws PacketProcessingException {
        return handle(packet);
    }

    @Override
    public Packet handle(ReadMessagePacket packet) throws PacketProcessingException {
        return handle(packet);
    }

    @Override
    public Packet handle(ComplexTestPacket packet) throws PacketProcessingException {
        return handle(packet);
    }

    @Override
    public Packet handle(MessageTestPacket packet) throws PacketProcessingException {
        return handle(packet);
    }

    @Override
    public Packet handle(QueueTestPacket packet) throws PacketProcessingException {
        return handle(packet);
    }

}

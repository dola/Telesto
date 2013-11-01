package ch.ethz.syslab.telesto.server.controller;

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
import ch.ethz.syslab.telesto.protocol.PutMessagePacket;
import ch.ethz.syslab.telesto.protocol.QueueTestPacket;
import ch.ethz.syslab.telesto.protocol.ReadMessagePacket;
import ch.ethz.syslab.telesto.protocol.RegisterClientPacket;
import ch.ethz.syslab.telesto.protocol.RegisterClientResponsePacket;
import ch.ethz.syslab.telesto.server.db.Database;
import ch.ethz.syslab.telesto.server.db.procedure.ClientProcedure;

public class PacketHandler implements IPacketHandler {

    private Database db;

    public PacketHandler(Database database) {
        db = database;
    }

    @Override
    public Packet handle(Packet packet) throws PacketProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Packet handle(PingPacket packet) throws PacketProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Packet handle(RegisterClientPacket packet) throws PacketProcessingException {
        // TODO: should return whole information; i.e. [client_id, client_name, operation_mode]

        int clientId = db.callSimpleProcedure(ClientProcedure.REQUEST_ID, packet.clientName, packet.mode);

        return new RegisterClientResponsePacket(packet.packetId + 1, clientId);
    }

    @Override
    public Packet handle(IdentifyClientPacket packet) throws PacketProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Packet handle(CreateQueuePacket packet) throws PacketProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Packet handle(DeleteQueuePacket packet) throws PacketProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Packet handle(GetQueueIdPacket packet) throws PacketProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Packet handle(GetQueueNamePacket packet) throws PacketProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Packet handle(GetQueuesPacket packet) throws PacketProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Packet handle(GetActiveQueuesPacket packet) throws PacketProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Packet handle(GetMessagesPacket packet) throws PacketProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Packet handle(PutMessagePacket packet) throws PacketProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Packet handle(ReadMessagePacket packet) throws PacketProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Packet handle(ComplexTestPacket packet) throws PacketProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Packet handle(MessageTestPacket packet) throws PacketProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Packet handle(QueueTestPacket packet) throws PacketProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

}

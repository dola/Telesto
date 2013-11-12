package ch.ethz.syslab.telesto.server.controller;

import ch.ethz.syslab.telesto.protocol.ComplexTestPacket;
import ch.ethz.syslab.telesto.protocol.CreateQueuePacket;
import ch.ethz.syslab.telesto.protocol.DeleteQueuePacket;
import ch.ethz.syslab.telesto.protocol.GetActiveQueuesPacket;
import ch.ethz.syslab.telesto.protocol.GetMessagesPacket;
import ch.ethz.syslab.telesto.protocol.GetQueueIdPacket;
import ch.ethz.syslab.telesto.protocol.GetQueueNamePacket;
import ch.ethz.syslab.telesto.protocol.GetQueuesPacket;
import ch.ethz.syslab.telesto.protocol.MessageTestPacket;
import ch.ethz.syslab.telesto.protocol.Packet;
import ch.ethz.syslab.telesto.protocol.PingPacket;
import ch.ethz.syslab.telesto.protocol.PongPacket;
import ch.ethz.syslab.telesto.protocol.PutMessagePacket;
import ch.ethz.syslab.telesto.protocol.QueueTestPacket;
import ch.ethz.syslab.telesto.protocol.ReadMessagePacket;
import ch.ethz.syslab.telesto.protocol.handler.IServerProtocolHandler;
import ch.ethz.syslab.telesto.protocol.handler.PacketProcessingException;
import ch.ethz.syslab.telesto.protocol.handler.ProtocolHandler;
import ch.ethz.syslab.telesto.server.db.Database;

public class ServerProtocolHandler extends ProtocolHandler implements IServerProtocolHandler {

    private Database db;

    public ServerProtocolHandler(Database database) {
        db = database;
    }

    @Override
    public Packet handle(PingPacket packet) throws PacketProcessingException {
        return new PongPacket();
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

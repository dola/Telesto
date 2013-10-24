package ch.ethz.syslab.telesto.server.controller;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

import ch.ethz.syslab.telesto.protocol.ComplexTestPacket;
import ch.ethz.syslab.telesto.protocol.CreateQueuePacket;
import ch.ethz.syslab.telesto.protocol.DeleteQueuePacket;
import ch.ethz.syslab.telesto.protocol.GetActiveQueuesPacket;
import ch.ethz.syslab.telesto.protocol.GetMessagesPacket;
import ch.ethz.syslab.telesto.protocol.GetQueueIdPacket;
import ch.ethz.syslab.telesto.protocol.GetQueueNamePacket;
import ch.ethz.syslab.telesto.protocol.GetQueuesPacket;
import ch.ethz.syslab.telesto.protocol.IdentifyClientPacket;
import ch.ethz.syslab.telesto.protocol.Packet;
import ch.ethz.syslab.telesto.protocol.PingPacket;
import ch.ethz.syslab.telesto.protocol.PutMessagePacket;
import ch.ethz.syslab.telesto.protocol.ReadMessagePacket;
import ch.ethz.syslab.telesto.protocol.RegisterClientPacket;
import ch.ethz.syslab.telesto.server.db.Database;

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

        CallableStatement stm = db.prepareCall("request_id", 2, true);

        try {
            stm.registerOutParameter(1, Types.INTEGER);

            stm.setObject(2, packet.clientName, Types.VARCHAR);

            stm.setString(2, packet.clientName);
            stm.setByte(3, packet.mode);

            stm.execute();
            stm.getResultSet();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
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

}

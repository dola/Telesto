package ch.ethz.syslab.telesto.server.controller;

import ch.ethz.syslab.telesto.protocol.Packet;

public interface IPacketHandler {
    public abstract Packet handle(Packet packet) throws PacketProcessingException;
}

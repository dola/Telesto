package ch.ethz.syslab.telesto.protocol.handler;

import ch.ethz.syslab.telesto.protocol.*;

/* 
 * Do not edit this file! 
 * 
 * Edit the template at tools/protocol/telesto/templates/handler.java instead.
 */
public interface IClientProtocolHandler {
   public abstract Packet handle(PongPacket packet) throws PacketProcessingException;
   public abstract Packet handle(SuccessPacket packet) throws PacketProcessingException;
   public abstract Packet handle(ErrorPacket packet) throws PacketProcessingException;
   public abstract Packet handle(RegisterClientResponsePacket packet) throws PacketProcessingException;
   public abstract Packet handle(IdentifyClientResponsePacket packet) throws PacketProcessingException;
   public abstract Packet handle(CreateQueueResponsePacket packet) throws PacketProcessingException;
   public abstract Packet handle(GetQueueIdResponsePacket packet) throws PacketProcessingException;
   public abstract Packet handle(GetQueueNameResponsePacket packet) throws PacketProcessingException;
   public abstract Packet handle(GetQueuesResponsePacket packet) throws PacketProcessingException;
   public abstract Packet handle(GetActiveQueuesResponsePacket packet) throws PacketProcessingException;
   public abstract Packet handle(GetMessagesResponsePacket packet) throws PacketProcessingException;
   public abstract Packet handle(ReadMessageResponsePacket packet) throws PacketProcessingException;
   public abstract Packet handle(ComplexTestPacket packet) throws PacketProcessingException;
   public abstract Packet handle(MessageTestPacket packet) throws PacketProcessingException;
   public abstract Packet handle(QueueTestPacket packet) throws PacketProcessingException;
}
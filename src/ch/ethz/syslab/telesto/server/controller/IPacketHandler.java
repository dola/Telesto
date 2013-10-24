package ch.ethz.syslab.telesto.server.controller;

import ch.ethz.syslab.telesto.protocol.*;

/* 
 * Do not edit this file! 
 * 
 * Edit the template at tools/protocol/telesto/templates/handler.java instead.
 */
public interface IPacketHandler {
   public abstract void handle(Packet packet);
   public abstract void handle(PingPacket packet);
   public abstract void handle(RegisterClientPacket packet);
   public abstract void handle(IdentifyClientPacket packet);
   public abstract void handle(CreateQueuePacket packet);
   public abstract void handle(DeleteQueuePacket packet);
   public abstract void handle(GetQueueIdPacket packet);
   public abstract void handle(GetQueueNamePacket packet);
   public abstract void handle(GetQueuesPacket packet);
   public abstract void handle(GetActiveQueuesPacket packet);
   public abstract void handle(GetMessagesPacket packet);
   public abstract void handle(PutMessagePacket packet);
   public abstract void handle(ReadMessagePacket packet);
   public abstract void handle(ComplexTestPacket packet);
}
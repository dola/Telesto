package ch.ethz.syslab.telesto.server.controller;

import ch.ethz.syslab.telesto.protocol.*;

/* 
 * Do not edit this file! {# Ignore this, you're in the right place. #}
 * 
 * Edit the template at tools/protocol/telesto/templates/handler.java instead.
 */
public interface {{ handler }} extends IPacketHandler {
   {%- for message in messages if message and condition(message) %}
   public abstract Packet handle({{ message.__name__ }}{{ superclass }} packet) throws PacketProcessingException;
   {%- endfor %}
}

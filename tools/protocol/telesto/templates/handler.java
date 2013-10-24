package ch.ethz.syslab.telesto.server.controller;

import ch.ethz.syslab.telesto.protocol.*;

/* 
 * Do not edit this file! {# Ignore this, you're in the right place. #}
 * 
 * Edit the template at tools/protocol/telesto/templates/handler.java instead.
 */
public interface PacketHandler {
   public abstract void handle(Packet packet);
   {%- for message in messages if message %}
   public abstract void handle({{ message.__name__ }}{{ superclass }} packet);
   {%- endfor %}
}

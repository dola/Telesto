{% import "parse.java" as parse %}
{% import "emit.java" as emit %}
package ch.ethz.syslab.telesto.protocol;

import java.nio.ByteBuffer;

import ch.ethz.syslab.telesto.protocol.model.*;

{% set name = message.__name__ + superclass %}

/* 
 * Do not edit this file! {# Ignore this, you're in the right place. #}
 * 
 * Edit the template at tools/protocol/telesto/templates/packet.java instead.
 */
public class {{ name }} extends {{ superclass }} {
    {%- for field in message._fields.itervalues() if field.name != "packet_id" %}
    public {{ field.java_type }} {{ field.java_name }};
    {%- endfor %}

    public {{ name }}() {
    }
    
    {% set args = [] -%}
    {%- for field in message._fields.itervalues() -%}
        {%- do args.append("%s %s" % (field.java_type, field.java_name)) -%}
    {%- endfor -%}
    public {{ name }}({{ args|join(", ") }}) {
        {%- for field in message._fields.itervalues() %}
        this.{{ field.java_name }} = {{ field.java_name }};
        {%- endfor %}
    }

    @Override
    public byte methodId() {
        return {{ message.method_id }};
    }

    @Override
    public void emit(ByteBuffer buffer) {
        int lengthIndex = buffer.position();
        buffer.position(lengthIndex + 2);
        buffer.put(methodId());
        {%- for field in message._fields.itervalues() %}
        {{ emit.emit(field) }}
        {%- endfor %}
        buffer.putShort(lengthIndex, (short) (buffer.position() - lengthIndex - 2));
    }

    @Override
    public void parse(ByteBuffer buffer) {
        {%- for field in message._fields.itervalues() %}
        {{ parse.parse(field) }}
        {%- endfor %}
    }

    @Override
    public {{ name }} newInstance() {
        return new {{ name }}();
    }
    
    public String toString() {
        return "{{ name }}";
    }
}

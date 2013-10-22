package ch.ethz.syslab.telesto.protocol;

import java.nio.ByteBuffer;

public class /*{name}*/ extends /*{superclass}*/ {
    /*{fields}*/

    public /*{name}*/() {
    }
    
    public /*{name}*/(/*{constructorargs}*/) {
        /*{constructor}*/
    }

    @Override
    public byte methodId() {
        return /*{methodid}*/;
    }

    @Override
    public void emit(ByteBuffer buffer) {
        int lengthIndex = buffer.position();
        buffer.position(lengthIndex + 2);
        buffer.put(methodId());
        /*{emit}*/
        buffer.putShort(lengthIndex, (short) (buffer.position() - lengthIndex - 2));
    }

    @Override
    public void parse(ByteBuffer buffer) {
        /*{parse}*/
    }

    @Override
    public /*{name}*/ newInstance() {
        return new /*{name}*/();
    }
    
    public String toString() {
        return "/*{name}*/";
    }
}

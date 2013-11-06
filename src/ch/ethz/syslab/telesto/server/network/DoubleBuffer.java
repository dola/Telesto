package ch.ethz.syslab.telesto.server.network;

import java.nio.ByteBuffer;

import ch.ethz.syslab.telesto.server.config.CONFIG;

public class DoubleBuffer {
    public ByteBuffer writeBuffer;
    public ByteBuffer readBuffer;
    private int startPosition = 0;

    public DoubleBuffer(int capacity) {
        writeBuffer = ByteBuffer.allocateDirect(CONFIG.MW_READ_BUFFER_SIZE);
        readBuffer = writeBuffer.duplicate();
    }

    public void prepare() {
        startPosition = readBuffer.position();
    }

    public int bytesAvailable() {
        return writeBuffer.position() - readBuffer.position();
    }

    public void limit(int limit) {
        readBuffer.limit(readBuffer.position() + limit);
    }

    public int bytesRead() {
        return readBuffer.position() - startPosition;
    }

    public boolean dataRemaining() {
        return writeBuffer.position() != readBuffer.position();
    }

    public void cleanup() {
        if (writeBuffer.position() * 4 > writeBuffer.capacity() * 3 && readBuffer.position() * 4 > readBuffer.capacity()) {
            synchronized (writeBuffer) {
                readBuffer.limit(writeBuffer.position());
                readBuffer.compact();
                writeBuffer.position(readBuffer.position());
                readBuffer.flip();
            }
        }
    }
}

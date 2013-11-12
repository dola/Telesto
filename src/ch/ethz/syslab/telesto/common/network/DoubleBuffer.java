package ch.ethz.syslab.telesto.common.network;

import java.nio.ByteBuffer;

import ch.ethz.syslab.telesto.server.config.CONFIG;

public class DoubleBuffer {
    public ByteBuffer writeView;
    public ByteBuffer readView;
    private boolean acquired;
    private int startPosition = 0;

    public DoubleBuffer(int capacity) {
        writeView = ByteBuffer.allocateDirect(CONFIG.MW_READ_BUFFER_SIZE);
        readView = writeView.duplicate();
    }

    public synchronized boolean acquire() {
        if (acquired) {
            return false;
        }
        acquired = true;
        return acquired;
    }

    public void release() {
        acquired = false;
    }

    public void prepare() {
        startPosition = readView.position();
    }

    public int bytesAvailable() {
        return writeView.position() - readView.position();
    }

    public void limit(int limit) {
        readView.limit(readView.position() + limit);
    }

    public int bytesRead() {
        return readView.position() - startPosition;
    }

    public boolean dataRemaining() {
        return writeView.position() != readView.position();
    }

    public void cleanup() {
        if (writeView.position() * 4 > writeView.capacity() * 3 && readView.position() * 4 > readView.capacity()) {
            readView.limit(writeView.position());
            readView.compact();
            writeView.position(readView.position());
            readView.flip();
        }
    }
}

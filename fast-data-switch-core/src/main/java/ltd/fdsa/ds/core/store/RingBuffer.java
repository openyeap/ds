package ltd.fdsa.ds.core.store;

import lombok.var;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;
/*

ssssssss
*/
public class RingBuffer {
    final DataBlock[] buffer;
    final int size;
    final AtomicLong counter;

    public RingBuffer(long initialValue, int size) {
        if (size > Integer.MAX_VALUE) {
            size = Integer.MAX_VALUE;
        }

        this.counter = new AtomicLong(initialValue);
        this.size = size;
        this.buffer = new DataBlock[size];
    }

    public long getOffset() {
        return this.counter.get();
    }

    public int getSize() {
        return this.size;
    }

    public DataBlock pull(long offset) {
        if (offset > this.size) {
            offset %= this.size;
        }
        return this.buffer[(int) offset];
    }

    public long push(DataBlock data) {
        var offset = this.counter.incrementAndGet();
        if (offset > this.size) {
            offset %= this.size;
        }
        this.buffer[(int) offset] = data;
        return this.counter.get();
    }
}

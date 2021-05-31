package io.github.loovien.locked;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.stream.Stream;

@Slf4j
public abstract class DLocked implements Lock {
    @Override
    public void lock() {
        throw new NotImplementException("not not implemented yet.");
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        throw new NotImplementException("not not implemented yet.");
    }

    @Override
    public boolean tryLock() {
        throw new NotImplementException("not not implemented yet.");
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        throw new NotImplementException("not not implemented yet.");
    }

    @Override
    public void unlock() {
        throw new NotImplementException("not not implemented yet.");
    }

    @Override
    public Condition newCondition() {
        throw new NotImplementException("not not implemented yet.");
    }

    protected void closeIt(AutoCloseable... closeable) {
        Arrays.stream(closeable).forEach((close) -> {
            if (close != null) {
                try {
                    close.close();
                } catch (Exception e) {
                    log.error("close resource: {} not successfully", close, e);
                }
            }
        });
    }
}

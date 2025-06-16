package org.example.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Service.LogisticsBase;
import org.example.States.TruckState;
import org.example.States.WaitingState;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Truck implements Runnable {
    private static final Logger logger = LogManager.getLogger(Truck.class);

    private final int id;
    private final boolean isPerishable;
    private TruckState state;
    private final LogisticsBase logisticsBase;
    private Terminal terminal;
    private final Lock lock;
    private boolean isProcessed;

    public Truck(int id, boolean isPerishable, LogisticsBase logisticsBase) {
        this.id = id;
        this.isPerishable = isPerishable;
        this.logisticsBase = logisticsBase;
        this.isProcessed = false;
        this.lock = new ReentrantLock();
    }

    public void start() {
        enterState(new WaitingState());
    }

    public int getId() {
        return id;
    }

    public boolean isPerishable() {
        return isPerishable;
    }

    public TruckState getState() {
        return state;
    }

    public LogisticsBase getLogisticsBase() {
        return logisticsBase;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public void enterState(TruckState newState) {
        lock.lock();
        try {
            if (this.state != null) {
                this.state.ended(this);
            }
            this.state = newState;
            this.state.entered(this);
            logger.info("Truck {} transitioned to state {}", id, newState.getClass().getSimpleName());
        } finally {
            lock.unlock();
        }
    }

    public void setProcessed(boolean isProcessed) {
        lock.lock();
        try {
            this.isProcessed = isProcessed;
            logger.info("Set processed to {} for Truck {}", this.isProcessed, this.id);
        } finally {
            lock.unlock();
        }
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    @Override
    public void run() {
        logger.info("Truck {} started", id);
        start();
        while (!isProcessed) {
            state.process(this);
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                logger.error("Truck {} interrupted", id, e);
                Thread.currentThread().interrupt();
            }
        }
        logger.info("Truck {} finished", id);
    }

    @Override
    public String toString() {
        return "Truck id=" + id + ", isPerishable=" + isPerishable;
    }
}
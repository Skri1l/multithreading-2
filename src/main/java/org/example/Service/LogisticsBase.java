package org.example.Service;
import org.example.States.CompletedState;
import org.example.entity.Terminal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.entity.Truck;

import static org.example.Data.DataConfigurer.getTerminalCount;

public final class LogisticsBase {

    private static final Logger logger = LogManager.getLogger(LogisticsBase.class);

    private static LogisticsBase instance;
    private final Semaphore terminals;
    private final static Lock lock = new ReentrantLock();
    private final List<Terminal> terminalList;



    private LogisticsBase(){
        int terminalCount = getTerminalCount();
        this.terminals = new Semaphore(terminalCount, true);
        this.terminalList = new ArrayList<>();
        for(int i = 0; i < terminalCount; i++){
            terminalList.add(new Terminal(i));
        }
    }

    public static LogisticsBase getInstance(){
        if (instance == null) {
            lock.lock();
            try {
                if (instance == null) {
                    instance = new LogisticsBase();
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }

    public Terminal acquireAvailableTerminal(){
        try {
            terminals.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        lock.lock();
        try{
            for (Terminal terminal : terminalList){
                if (!terminal.isBusy){
                    terminal.setBusy(true);{
                        return terminal;
                    }
                }
            }
        }finally {
            lock.unlock();
        }
        throw new IllegalStateException("No free terminal ");
    }

    public void releaseTerminal(Terminal terminal){
        lock.lock();
        try{
            terminal.setBusy(false);
        }finally {
            lock.unlock();
        }
        terminals.release();
    }

    public List<Terminal> getTerminalList(){
        return terminalList;
    }
}

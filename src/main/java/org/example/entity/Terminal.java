package org.example.entity;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Terminal {

    private final int id;

    public boolean isBusy;

    private final Lock lock;

    public Terminal(int id){
        this.id = id;
        this.isBusy = false;
        this.lock = new ReentrantLock();
    }

    public int getId(){
        return id;
    }

    public boolean isBusy(){
        lock.lock();
        try {
            return isBusy;
        }finally {
            lock.unlock();
        }
    }

    public void occupy(){
        lock.lock();
        isBusy = true;
        lock.unlock();
    }

    public void release(){
        lock.lock();
        isBusy = false;
        lock.unlock();
    }

    public void setBusy(boolean busy){
        isBusy = busy;
    }

}

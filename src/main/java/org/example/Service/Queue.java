package org.example.Service;

import org.example.Data.DataConfigurer;
import org.example.entity.Truck;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.example.Data.DataConfigurer.getTerminalCount;

public class Queue {

    private final PriorityQueue<Truck> queue;
    private final Lock lock = new ReentrantLock();
    private final int capacity;

    public Queue(int queueCapacity){
        this.capacity = DataConfigurer.getQueueCapacity();
        this.queue = new PriorityQueue<>(Comparator.comparing(Truck::isPerishable).reversed());
    }

    public boolean addTruck(Truck truck){
        lock.lock();
        try{
            if (queue.size() >= capacity) {
                return false;
            }
            queue.add(truck);
            return true;
        }finally {
            lock.unlock();
        }
    }

    public Truck pollTruck(){
        lock.lock();
        try {
            return queue.poll();
        }finally {
            lock.unlock();
        }
    }

    public boolean isEmpty(){
        lock.lock();
        try {
            return queue.isEmpty();
        }finally {
            lock.unlock();
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public int getSize() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }
}

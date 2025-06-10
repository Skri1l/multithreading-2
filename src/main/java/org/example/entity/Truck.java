package org.example.entity;

import org.example.Service.LogisticsBase;
import org.example.States.TruckState;
import org.example.States.WaitingState;



public class Truck implements Runnable {

    private final int id;
    private final boolean isPerishable;
    private TruckState state;
    private final LogisticsBase logisticsBase;
    private Terminal terminal;

    public Truck(int id, boolean isPerishable, LogisticsBase logisticsBase) {
        this.id = id;
        this.isPerishable = isPerishable;
        this.logisticsBase = logisticsBase;
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
        if (this.state != null) {
            this.state.ended(this);
        }
        this.state = newState;
        this.state.entered(this);
        this.state.process(this);
    }

    @Override
    public void run() {
        System.out.println("Truck " + id + "get started");
        start();
    }

    @Override
    public String toString() {
        return "Truck id=" + id + ", isPerishable=" + isPerishable;
    }
}

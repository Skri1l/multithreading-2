package org.example.States;

import org.example.entity.Truck;

public abstract class TruckState {
    public abstract void entered(Truck truck);

    public abstract void process(Truck truck);

    public abstract void ended(Truck truck);
}

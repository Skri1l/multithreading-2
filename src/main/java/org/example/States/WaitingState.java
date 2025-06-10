package org.example.States;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Service.LogisticsBase;
import org.example.entity.Truck;
import org.example.Service.TerminalDispatcher;


public class WaitingState extends TruckState {
    private static final Logger logger = LogManager.getLogger(WaitingState.class);

    @Override
    public void entered(Truck truck) {
        logger.info("Truck {} has entered the Waiting State", truck.getId());
    }

    @Override
    public void process(Truck truck) {
        LogisticsBase logisticsBase = truck.getLogisticsBase();
        logger.info("Truck {} is in processing", truck.getId());
        TerminalDispatcher.getInstance().submitTruck(truck);
    }

    @Override
    public void ended(Truck truck) {
        logger.info("Truck {} is leaving the Waiting State", truck.getId());
    }
}

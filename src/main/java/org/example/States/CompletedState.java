package org.example.States;

import org.example.Service.LogisticsBase;
import org.example.entity.Truck;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CompletedState extends TruckState{
    private static final Logger logger = LogManager.getLogger(CompletedState.class);

    public void entered(Truck truck){
        logger.info("Truck {} is entered Completed State", truck.getId());
    }
    @Override
    public void process(Truck truck) {
        LogisticsBase logisticsBase = truck.getLogisticsBase();
        logger.info("Truck {} is in Processing State", truck.getId());

        logisticsBase.releaseTerminal(truck.getTerminal());

        logger.info("Truck {} has left the base", truck.getId());

    }

    public void ended(Truck truck){
        logger.info("Truck {} is exiting Completed State", truck.getId());
    }
}

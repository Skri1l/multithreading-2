package org.example.States;

import org.example.Service.LogisticsBase;
import org.example.entity.Terminal;
import org.example.entity.Truck;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class ProcessingState extends TruckState{
    private static final Logger logger = LogManager.getLogger(ProcessingState.class);

    public void entered(Truck truck){
        logger.info("Truck {} has entered the Proccesing State", truck.getId());
    }


    @Override
    public void process(Truck truck) {
        LogisticsBase logisticsBase = truck.getLogisticsBase();
        logger.info("Truck {} is processing in Processing State", truck.getId());
        try{
            logger.info("Truck {} is waiting for terminal", truck.getId());

            Terminal terminal = logisticsBase.acquireAvailableTerminal();
            truck.setTerminal(terminal);

            logger.info("Truck {} started processing at the trminal {}",truck.getId(), terminal.getId());

            TimeUnit.SECONDS.sleep(5);

            logger.info("Truck {} is finished at the terminal {}", truck.getId(), terminal.getId());

            truck.enterState(new CompletedState());

        } catch (InterruptedException e){
            logger.error("Truck {} was interrupted during processing ", truck.getId(), e);
        }

    }

    public void ended(Truck truck){
        logger.info("Truck {} is leaving Processing state", truck.getId());
    }
}

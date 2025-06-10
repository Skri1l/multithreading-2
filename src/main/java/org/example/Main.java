package org.example;

import org.example.Data.DataConfigurer;
import org.example.Service.LogisticsBase;
import org.example.Service.Queue;
import org.example.Service.TerminalDispatcher;
import org.example.entity.Truck;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataConfigurer.loadTerminalCountFromConfig("src/main/resources/config.json");
        DataConfigurer.loadQueueCapacityFromConfig("src/main/resources/config.json");

        int terminalCount = DataConfigurer.getTerminalCount();
        int queueCapacity = DataConfigurer.getQueueCapacity();
        if (terminalCount <= 0) {
            throw new IllegalStateException("terminalCount must be positive");
        }
        if (queueCapacity <= 0) {
            throw new IllegalStateException("queueCapacity must be positive");
        }
        LogisticsBase logisticsBase = LogisticsBase.getInstance();
        Queue queue = new Queue(queueCapacity);
        TerminalDispatcher.init(queue, terminalCount);

        List<Truck> trucks = DataConfigurer.loadTrucksFromJSON("src/main/resources/trucks.json", logisticsBase);

        for (Truck truck : trucks) {
            new Thread(truck).start();
        }
    }
}

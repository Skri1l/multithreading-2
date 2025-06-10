package org.example.Data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Service.LogisticsBase;
import org.example.entity.Truck;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DataConfigurer {
    private static int terminalCount;

    private static int queueCapacity;


    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<Truck> loadTrucksFromJSON(String filePath, LogisticsBase base) {
        try {
            List<TruckJson> truckJsonList = mapper.readValue(new File(filePath), new TypeReference<List<TruckJson>>() {});
            return truckJsonList.stream()
                    .map(tj -> new Truck(tj.getId(), tj.isPerishable(), base))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read truck data from JSON file: " + filePath, e);
        }
    }

    public static void loadQueueCapacityFromConfig(String filePath) {
        try {
            Config config = mapper.readValue(new File(filePath), Config.class);
            queueCapacity = config.getQueueCapacity();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении конфигурации: " + filePath, e);
        }
    }

    public static int getQueueCapacity() {
        return queueCapacity;
    }



    public static void loadTerminalCountFromConfig(String filePath) {
        try {
            Config config = mapper.readValue(new File(filePath), Config.class);
            terminalCount = config.getTerminalCount();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении конфигурации: " + filePath, e);
        }
    }

    public static int getTerminalCount() {
        return terminalCount;
    }


    private static class Config {
        private int terminalCount;
        private int dispatcherThreads;
        private int queueCapacity;

        public int getTerminalCount() {
            return terminalCount;
        }

        public int getQueueCapacity() {
            return queueCapacity;
        }
    }

    private static class TruckJson {
        private int id;
        private boolean isPerishable;

        public TruckJson() {}

        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }

        public boolean isPerishable() {
            return isPerishable;
        }
        public void setIsPerishable(boolean isPerishable) {
            this.isPerishable = isPerishable;
        }
    }
}

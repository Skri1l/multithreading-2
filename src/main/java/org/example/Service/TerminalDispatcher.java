package org.example.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.States.ProcessingState;
import org.example.entity.Terminal;
import org.example.entity.Truck;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class TerminalDispatcher {

    private static final Logger logger = LogManager.getLogger(TerminalDispatcher.class);

    private static final Object lock = new Object();
    private static TerminalDispatcher instance;

    private final Queue queue;
    private final ExecutorService executor;

    private TerminalDispatcher(Queue queue, int threadPoolSize) {
        this.queue = queue;
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
        startDispatching();
    }

    public static TerminalDispatcher getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Dispatcher is not initialized. Call init() first.");
        }
        return instance;
    }

    public static void init(Queue queue, int threadPoolSize) {
        if (instance == null) {
            instance = new TerminalDispatcher(queue, threadPoolSize);
        }
    }

    public void submitTruck(Truck truck) {
        boolean added = queue.addTruck(truck);
        if (!added) {
            logger.warn("Не удалось добавить грузовик {} в очередь: очередь заполнена", truck.getId());
            while (!queue.addTruck(truck)) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void startDispatching() {
        Runnable dispatcherTask = () -> {
            while (true) {
                if (!queue.isEmpty()) {
                    Truck truck = queue.pollTruck();
                    if (truck != null) {
                        Terminal terminal = truck.getLogisticsBase().acquireAvailableTerminal();
                        truck.setTerminal(terminal);
                        truck.enterState(new ProcessingState());
                    }
                } else {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        };
        Thread dispatcherThread = new Thread(dispatcherTask);
        dispatcherThread.setDaemon(true);
        dispatcherThread.start();
    }
}
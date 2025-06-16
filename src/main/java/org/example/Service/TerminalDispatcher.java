package org.example.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.States.ProcessingState;
import org.example.entity.Truck;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class TerminalDispatcher {
    private static final Logger logger = LogManager.getLogger(TerminalDispatcher.class);
    private static TerminalDispatcher instance;
    private final Queue queue;
    private final ExecutorService executor;

    private boolean isWorking = true;

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
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                submitTruck(truck);
            } catch (InterruptedException e) {
                logger.error("Interrupted while retrying to add Truck {}", truck.getId(), e);
                Thread.currentThread().interrupt();
            }
        } else {
            logger.info("Truck {} added to queue", truck.getId());
        }
    }

    public void setWorking(boolean isWorking){
        this.isWorking = isWorking;
    }

    private void startDispatching() {
        logger.info("Dispatcher started");
        Runnable dispatcherTask = () -> {
            while (isWorking) {
                try {
                    logger.info("Dispatcher checking queue");
                    if (!queue.isEmpty()) {
                        Truck truck = queue.pollTruck();
                        if (truck != null) {
                            logger.info("Polled Truck {} from queue", truck.getId());
                            executor.submit(() -> {
                                truck.enterState(new ProcessingState());
                            });
                        }
                    } else {
                        TimeUnit.MILLISECONDS.sleep(100);
                    }
                } catch (InterruptedException e) {
                    logger.error("Dispatcher interrupted", e);
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        };
        Thread dispatcherThread = new Thread(dispatcherTask);
        dispatcherThread.start(); // Убрали setDaemon(true)
    }
}
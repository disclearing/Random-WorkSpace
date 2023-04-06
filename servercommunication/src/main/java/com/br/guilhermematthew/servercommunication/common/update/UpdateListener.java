package com.br.guilhermematthew.servercommunication.common.update;

import java.util.concurrent.ConcurrentLinkedQueue;

public class UpdateListener implements Runnable {

    private long seconds;

    private final ConcurrentLinkedQueue<UpdateEvent> list;

    private boolean running;

    public UpdateListener() {
        list = new ConcurrentLinkedQueue<>();
    }

    public void register(UpdateEvent updateEvent) {
        list.add(updateEvent);
    }

    public void unregister(UpdateEvent updateEvent) {
        list.remove(updateEvent);
    }

    @Override
    public void run() {
        running = true;

        while (running) {
            try {
                Thread.sleep(1000);
            } catch (Exception ex) {
            }

            seconds++;

            call(UpdateType.SECOND);

            if (seconds % 60 == 0)
                call(UpdateType.MINUTE);
        }
    }

    private void call(UpdateType updateEvent) {
        list.forEach(update -> update.onUpdate(updateEvent));
    }

    public enum UpdateType {
        SECOND, MINUTE
    }

    public interface UpdateEvent {

        void onUpdate(UpdateType updateType);

    }
}
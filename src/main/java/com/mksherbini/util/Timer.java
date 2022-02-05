package com.mksherbini.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Timer {
    private long time;
    private String id;

    public Timer(String id) {
        this.id = id;
    }

    public Timer() {
    }

    public long mark() {
        long last = time;
        time = System.currentTimeMillis();
        return (time - last);
    }

    public void log() {
        if (id != null)
            log.info("Time since last mark for {}: {}", id, mark());
        else
            log.info("Time since last mark: {}", mark());
    }
}

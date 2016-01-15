package com.marcouberti.sonicboomwatchface.utils.stopwatch;

/**
 * Created by Marco on 22/11/15.
 */
public class StopWatch {
    private long startTime = 0;
    public boolean running = false;
    public boolean paused = false;
    private long currentTime = 0;

    public void start() {
        this.startTime = System.currentTimeMillis();
        this.paused = false;
        this.running = true;
    }

    public void stop() {
        this.running = false;
        this.paused = false;
    }

    public void pause() {
        this.running = false;
        this.paused = true;
        currentTime = System.currentTimeMillis() - startTime;
    }
    public void resume() {
        this.running = true;
        this.paused = false;
        this.startTime = System.currentTimeMillis() - currentTime;
    }

    //elaspsed time in milliseconds
    public long getElapsedTimeMilib() {
        long elapsed = 0;
        if (running) {
            elapsed =((System.currentTimeMillis() - startTime)/100) % 1000 ;
        }
        return elapsed;
    }

    public long getElapsedTimeMillis() {
        long elapsed = 0;
        if (running) {
            elapsed =((System.currentTimeMillis() - startTime)) % 1000 ;
        }
        return elapsed;
    }

    //elaspsed time in seconds
    public long getElapsedTimeSecs() {
        long elapsed = 0;
        if (running) {
            elapsed = ((System.currentTimeMillis() - startTime) / 1000) % 60;
        }
        return elapsed;
    }

    //elaspsed time in minutes
    public long getElapsedTimeMin() {
        long elapsed = 0;
        if (running) {
            elapsed = (((System.currentTimeMillis() - startTime) / 1000) / 60 ) % 60;
        }
        return elapsed;
    }

    //elaspsed time in hours
    public long getElapsedTimeHour() {
        long elapsed = 0;
        if (running) {
            elapsed = ((((System.currentTimeMillis() - startTime) / 1000) / 60 ) / 60);
        }
        return elapsed;
    }

    public String toString() {

        return String.format("%02d", (int)getElapsedTimeHour()) + ":" + String.format("%02d", (int) getElapsedTimeMin()) + ":"
                + String.format("%02d", (int) getElapsedTimeSecs());
    }
}
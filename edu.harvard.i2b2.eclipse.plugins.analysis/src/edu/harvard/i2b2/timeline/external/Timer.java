package edu.harvard.i2b2.timeline.external;

import java.awt.Component;
import java.awt.Event;

/**
 * 
 * 
 * 
 * @see
 * 
 * @version 1.0, Nov 26, 1996
 * 
 * @author Symantec
 * 
 */

public class Timer implements Runnable {
    Component target;
    int eventType;
    boolean repeat;
    boolean repeating;
    boolean execute;
    Thread thread;
    int delay;

    public Timer(Component t) {
	this(t, 1000);
    }

    public Timer(Component t, int d) {
	this(t, d, false);
    }

    public Timer(Component t, int d, boolean r) {
	this(t, d, r, Event.ACTION_EVENT);
    }

    public Timer(Component t, int d, boolean r, int e) {
	target = t;
	delay = d;
	repeat = r;
	execute = false;
	thread = new Thread(this);
	eventType = e;
	thread.start();
    }

    public void setEventType(int type) {
	eventType = type;
    }

    public int getEventType() {
	return eventType;
    }

    public void setTarget(Component t) {
	target = t;
    }

    public Component getTarget() {
	return target;
    }

    public void setDelay(int d) {
	delay = d;
    }

    public int getDelay() {
	return delay;
    }

    public void start() {
	execute = true;
	thread.resume();
    }

    public void setRepeat(boolean f) {
	repeat = f;
    }

    public boolean getRepeat() {
	return repeat;
    }

    public void start(int d) {
	delay = d;

	start();
    }

    public void start(boolean r) {
	repeat = r;

	start();
    }

    public void start(int d, boolean r) {
	delay = d;
	repeat = r;

	start();
    }

    public void stop() {
	execute = false;
	repeating = false;
    }

    public void run() {
	if (!execute)
	    thread.suspend();
	try {
	    while (true) {
		repeating = repeat;

		do {
		    Thread.sleep(delay);

		    if (execute) {
			target.handleEvent(new Event(this, eventType, null));
		    }
		} while (repeating);

		thread.suspend();
	    }
	} catch (InterruptedException e) {
	}
    }
}

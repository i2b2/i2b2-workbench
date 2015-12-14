/*
 * Copyright (c)  2006-2007 University Of Maryland
 * All rights  reserved.  
 * Modifications done by Massachusetts General Hospital
 *  
 *  Contributors:
 *  
 *  	Wensong Pan (MGH)
 *
 */
/*
 *
 * Copyright (c) 1998 Jean-Daniel Fekete
 *
 * Permission to use, copy, modify, distribute, and sell this software and 
 * its documentation for any purpose is hereby granted without fee, provided
 * that (i) the above copyright notices and this permission notice appear in
 * all copies of the software and related documentation, and (ii) the name of
 * Jean-Daniel Fekete may not be used in any advertising or publicity
 * relating to the software without his specific, prior written
 * permission.
 * 
 * THE SOFTWARE IS PROVIDED "AS-IS" AND WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS, IMPLIED OR OTHERWISE, INCLUDING WITHOUT LIMITATION, ANY 
 * WARRANTY OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  
 *
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, INCIDENTAL,
 * INDIRECT OR CONSEQUENTIAL DAMAGES OF ANY KIND, OR ANY DAMAGES WHATSOEVER
 * RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER OR NOT ADVISED OF
 * THE POSSIBILITY OF DAMAGE, AND ON ANY THEORY OF LIABILITY, ARISING OUT
 * OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
  
package edu.harvard.i2b2.timeline.excentric;


import java.awt.*;
import java.awt.event.*;

/**
 * The <code>Idle</code> class triggers a method when the user is idle
 * for a second.
 *
 * @version 	0.1, 07/30/98
 * @author 	Jean-Daniel Fekete
 * @since       JDK1.1.5
 */
public abstract class Idle implements MouseMotionListener, MouseListener, Runnable {
	private int idle_time;
	private int timeout = 1000;
	private int time_slice = 200;
	private Thread thread;
	private boolean stopped;
	private int idle_x, idle_y;
  
	public Idle(int timeout, int time_slice) {
		this.timeout = timeout;
	    this.time_slice = time_slice;
	    idle_time = timeout;
	    stopped = true;
  }

  /**
   * MouseMoveListener method.
   */
  public void mouseDragged(MouseEvent ev) {
    activate(ev.getX(), ev.getY());
  }
  /**
   * MouseMoveListener method.
   */
  public void mouseMoved(MouseEvent ev) {
    activate(ev.getX(), ev.getY());
  }
  /**
   * MouseListener method.
   */
  public void mouseClicked(MouseEvent e) { }
  /**
   * MouseListener method.
   */
  public void mousePressed(MouseEvent e) { }
  /**
   * MouseListener method.
   */
  public void mouseReleased(MouseEvent e) { }
  /**
   * MouseListener method.
   * We start worrying about idle time when we enter the managed Component.
   */
  public void mouseEntered(MouseEvent e) { start(); }
  /**
   * MouseListener method.
   * We stop worrying about idle time when we leave the managed Component.
   */
  public void mouseExited(MouseEvent e) { stop(); }


  /**
   * The Component to manage should be registered for the
   * <code>Idle</code> to react.
   * <p>
   * @param	c	The Component to manage.
   */
  public void register(Component c) {
    c.addMouseMotionListener(this);
    c.addMouseListener(this);
  }
  /**
   * The idle method is called when no mouse move happened in the
   * last second on the managed component.  By default, this method
   * does nothing.
   * <p>
   * @param	x	The X position of the mouse.
   * @param	y	The Y position of the mouse.
   */
  public abstract void idle(int x, int y);

  /**
   * The active method is called when the component becomes active.
   * <p>
   * @param	x	The X position of the mouse.
   * @param	y	The Y position of the mouse.
   */
  public abstract void active(int x, int y);

  /**
   * Implements the <code>Runnable</code> interface.
   */
  public void run() {
	  while (! stopped) {
      try {
    	  Thread.sleep(time_slice);
      } 
      catch(InterruptedException e) { 
    	  //e.printStackTrace();
      }
      idle_time -= time_slice;
      if (! stopped && idle_time == 0)
    	  idle(idle_x, idle_y);
    }
  }

  /**
   * Starts to manage timeouts.
   */
  public void start() {
    stop();
    stopped = false;
    thread = new Thread(this);
    thread.start();
  }
  /**
   * Stop managing timeouts.
   */
  public void stop() {
    if (thread != null && thread.isAlive()) {
      stopped = true;
      thread.interrupt();
    }
    thread = null;
  }

  public Idle() {
    idle_time = timeout;
    stopped = true;
  }
  
  /**
   * called when a mouse action occured.
   */
  protected void activate(int x, int y) {
    idle_x = x;
    idle_y = y;
    if (idle_time <= 0) 
      active(x, y);
    idle_time = timeout;
  }
}


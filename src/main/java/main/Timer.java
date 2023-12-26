package main;

import page.MainFrame;

import javax.swing.*;

public strictfp class Timer extends Thread {

	public static int fps = 1000 / 30;
	public static int inter = 0;
	protected static boolean state;

	public static void manualTick() {
		state = true; //Only used for followup popup, avoid using unless absolutely necessary
	}

	@Override
	public void run() {
		while (true) {
			long m = System.currentTimeMillis();
			state = false;
			Inv thr;
			SwingUtilities.invokeLater(thr = new Inv());
			try {
				boolean end = false;
				while (!end) {
					synchronized (thr) {
						end = state;
					}
					if (!end)
						sleep(1);
				}
				thr.join();
				int delay = (int) (System.currentTimeMillis() - m);
				inter = (inter * 9 + 100 * delay / fps) / 10;
				int sle = delay >= fps ? 1 : fps - delay;
				sleep(sle);
			} catch (InterruptedException e) {
				return;
			}
		}
	}

}

strictfp class Inv extends Thread {

	@Override
	public void run() {
		MainFrame.timer(Timer.fps);
		synchronized (this) {
			Timer.state = true;
		}

	}

}
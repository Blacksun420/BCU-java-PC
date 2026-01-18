package jogl;

import common.battle.BattleField;
import page.battle.BBRecd;

class GLBBRecd extends GLBattleBox implements BBRecd {

	private static final long serialVersionUID = 1L;

	private final GLRecorder glr;

	private int time;
	private boolean btlTick = true;//for 60 fps

	protected GLBBRecd(OuterBox bip, BattleField bf, String path, int type) {
		super(bip, bf, 0);
		glr = GLRecorder.getIns(this, path, type, bip);
		glr.start();
	}

	@Override
	public void end() {
		glr.end();

	}

	@Override
	public String info() {
		return "" + glr.remain();
	}

	@Override
	public void paint() {
		super.paint();
		if (bbp.bf.sb.time > time) {
			glr.update();
			time = bbp.bf.sb.time;
		} else if (bbp.bf.battleTick() != btlTick)
			glr.update();
		btlTick = bbp.bf.battleTick();
	}

	@Override
	public void quit() {
		glr.quit();
	}

}

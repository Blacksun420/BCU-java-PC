package jogl;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import common.battle.BattleField;
import common.battle.SBCtrl;
import common.util.unit.AbForm;
import common.util.unit.Enemy;
import common.util.unit.Form;
import jogl.util.GLGraphics;
import page.battle.BBCtrl;
import page.battle.BattleBox;

public class GLBattleBox extends GLCstd implements BattleBox, GLEventListener {

	private static final long serialVersionUID = 1L;

	protected final BBPainter bbp;

	public GLBattleBox(OuterBox bip, BattleField bf, int type) {
		bbp = type == 0 ? new BBPainter(bip, bf, this) : new BBCtrl(bip, (SBCtrl) bf, this);
		for (AbForm[] fs : bbp.bf.sb.b.lu.fs)
			for (AbForm f : fs)
				if (f instanceof Form)
					((Form) f).anim.check();
		for (Enemy e : bbp.bf.sb.st.data.getAllEnemy())
			e.anim.check();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		GLGraphics g = new GLGraphics(drawable.getGL().getGL2(), getWidth(), getHeight());
		bbp.draw(g);
		g.dispose();
		gl.glFlush();
	}

	@Override
	public BBPainter getPainter() {
		return bbp;
	}

	@Override
	public void paint() {
		display();
	}

	@Override
	public void reset() {
	}

	@Override
	public void releaseData() {
		bbp.bf.sb.release();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		bbp.reset();
	}

}

package utils.ui;

import com.badlogic.gdx.scenes.scene2d.Group;

public class CustomGroup extends Group {
	boolean	ignoreUpdate	= false;

	@Override
	public void act(float delta) {
		if (ignoreUpdate)
			return;
		super.act(delta);
	}

	public boolean isIgnoreUpdate() {
		return ignoreUpdate;
	}

	public void setIgnoreUpdate(boolean ignoreUpdate) {
		this.ignoreUpdate = ignoreUpdate;
	}

}

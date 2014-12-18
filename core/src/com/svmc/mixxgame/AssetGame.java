package com.svmc.mixxgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

public class AssetGame implements Disposable {
	private TextureAtlas	textureAtlas;

	private TextureRegion	force_background;
	private TextureRegion	logo;
	private TextureRegion	menu;
	private TextureRegion	facebook;
	private TextureRegion	googlePlus;
	private TextureRegion	next;
	private TextureRegion	replay;
	private TextureRegion	rect;
	private TextureRegion	reg_hex_tile;

	private TextureRegion	reg_cursor_normal;
	private TextureRegion	reg_cursor_highlight;
	public Animation		animBlue;
	public Animation		animRed;
	private Texture			textureHex;

	public AssetGame(TextureAtlas textureAtlas) {
		this.textureAtlas = textureAtlas;
	}

	public TextureRegion getFacebookButton() {
		if (facebook == null)
			facebook = textureAtlas.findRegion("ui/facebook");
		return facebook;
	}

	public TextureRegion getMenuButton() {
		if (menu == null)
			menu = textureAtlas.findRegion("ui/menu");
		return menu;
	}

	public TextureRegion getGooglePlusButton() {
		if (googlePlus == null)
			googlePlus = textureAtlas.findRegion("ui/googleplus");
		return googlePlus;
	}

	public TextureRegion getReplay() {
		if (replay == null)
			replay = textureAtlas.findRegion("ui/re");
		return replay;
	}

	public TextureRegion getNext() {
		if (next == null)
			next = textureAtlas.findRegion("ui/next");
		return next;
	}

	public Animation getAnimationBlue() {
		if (animBlue == null) {
			TextureRegion reg = textureAtlas.findRegion("ui/blueanimation");
			animBlue = creatAnimation(reg, 3, 1);
		}
		return animBlue;
	}

	public Animation getAnimationRed() {
		if (animRed == null)
			animRed = creatAnimation(
					textureAtlas.findRegion("ui/redanimation"), 3, 1);
		return animRed;
	}

	public TextureRegion getRectangle(Rectangle rectangle) {
		if (rect == null)
			rect = textureAtlas.findRegion("ui/rect1");
		return rect;
	}

	public TextureRegion getCursorNormal() {
		if (reg_cursor_normal == null)
			reg_cursor_normal = textureAtlas.findRegion("ui/cursor_normal");
		return reg_cursor_normal;
	}

	public TextureRegion getCursorHighlight() {
		if (reg_cursor_highlight == null)
			reg_cursor_highlight = textureAtlas
					.findRegion("ui/cursor_higlight");
		return reg_cursor_highlight;
	}

	public void resetAll() {
	}

	@Override
	public void dispose() {
	}

	public TextureRegion getHexTile() {
		if (reg_hex_tile == null)
			reg_hex_tile = textureAtlas.findRegion("ui/hex-tile");
		return reg_hex_tile;
	}

	public TextureRegion getLogo() {
		if (logo == null)
			logo = textureAtlas.findRegion("ui/logo");
		return logo;
	}

	public TextureRegion getForceBackground() {
		if (force_background == null)
			force_background = textureAtlas.findRegion("ui/force");
		return force_background;
	}

	public TextureRegion[] getArrayTextureRegion(TextureRegion textureRegion,
			int FRAME_COLS, int FRAME_ROWS) {
		float width = textureRegion.getRegionWidth() / FRAME_COLS;
		float height = textureRegion.getRegionHeight() / FRAME_ROWS;

		TextureRegion[] textureRegions = new TextureRegion[FRAME_COLS
				* FRAME_ROWS];
		TextureRegion[][] temp = textureRegion.split((int) width, (int) height);
		int index = 0;
		for (int i = 0; i < FRAME_ROWS; i++) {
			for (int j = 0; j < FRAME_COLS; j++) {
				textureRegions[index++] = temp[i][j];
			}
		}

		return textureRegions;
	}

	public TextureRegion[] getArrayTextureRegion(TextureRegion textureRegion,
			int FRAME_COLS, int FRAME_RAWS, int startFrame, int endFrame) {

		TextureRegion[][] arrayAnimations = (textureRegion.split(
				textureRegion.getRegionWidth() / FRAME_COLS,
				textureRegion.getRegionHeight() / FRAME_RAWS));
		TextureRegion[] arrayAnimation_temp = new TextureRegion[FRAME_RAWS
				* FRAME_COLS];
		int index = 0;
		for (int i = 0; i < FRAME_RAWS; i++) {
			for (int j = 0; j < FRAME_COLS; j++) {
				arrayAnimation_temp[index++] = arrayAnimations[i][j];
			}
		}
		if ((startFrame > endFrame) || (startFrame < 0)
				|| (endFrame > arrayAnimation_temp.length)
				|| (startFrame > arrayAnimation_temp.length)) {
			System.out.println("Loi Khoi Tao Sai Sprite. AssetGame.java");
		}

		TextureRegion[] arrayAnimation = new TextureRegion[endFrame
				- startFrame + 1];
		index = 0;
		for (int i = startFrame; i < endFrame + 1; i++) {
			arrayAnimation[index++] = arrayAnimation_temp[i];
		}
		return arrayAnimation;
	}

	public Animation creatAnimation(TextureRegion textureRegion,
			int FRAME_COLS, int FRAME_RAWS) {
		return new Animation(1.0f / 10.0f, getArrayTextureRegion(textureRegion,
				FRAME_COLS, FRAME_RAWS));
	}

	public Animation creatAnimation(TextureRegion textureRegion,
			int FRAME_COLS, int FRAME_RAWS, int startFrame, int endFrame) {
		return new Animation(1.0f / 10.0f, getArrayTextureRegion(textureRegion,
				FRAME_COLS, FRAME_RAWS, startFrame, endFrame));
	}

	public Animation creatAnimation(TextureRegion textureRegion,
			float frameDuration, int FRAME_COLS, int FRAME_RAWS) {

		return new Animation(frameDuration, getArrayTextureRegion(
				textureRegion, FRAME_COLS, FRAME_RAWS));

	}

	public Animation creatAnimation(TextureRegion textureRegion,
			float frameDuration, int FRAME_COLS, int FRAME_RAWS,
			int startFrame, int endFrame) {
		return new Animation(frameDuration, getArrayTextureRegion(
				textureRegion, FRAME_COLS, FRAME_RAWS, startFrame, endFrame));
	}

	public Animation creatAnimation(TextureRegion textureRegion,
			float frameDuration, int looping, int FRAME_COLS, int FRAME_RAWS) {
		return new Animation(frameDuration, getArrayTextureRegion(
				textureRegion, textureRegion.getRegionWidth() / FRAME_COLS,
				textureRegion.getRegionHeight() / FRAME_RAWS));

	}

	public Texture getTextureHex() {
		if (textureHex == null)
			textureHex = new Texture(Gdx.files.internal("Img/hex.png"));
		return textureHex;
	}
}

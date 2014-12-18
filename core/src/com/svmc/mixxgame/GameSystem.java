package com.svmc.mixxgame;

import utils.factory.Factory;
import utils.interfaces.IGameEvent;
import utils.listener.OnCompleteListener;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.svmc.mixxgame.attribute.EventType;
import com.svmc.mixxgame.attribute.R;
import com.svmc.mixxgame.entity.FollowEntity;
import com.svmc.mixxgame.entity.FollowEntity.FollowType;
import com.svmc.mixxgame.entity.GoalController;
import com.svmc.mixxgame.entity.GoalEntity;
import com.svmc.mixxgame.entity.Line;
import com.svmc.mixxgame.entity.MainPlayer;
import com.svmc.mixxgame.entity.RectangleEntity;

public class GameSystem {
	private MainPlayer				mainPlayer;
	public static boolean			redDone					= false;
	public static boolean			blueDone				= false;

	public Array<RectangleEntity>	rects					= new Array<RectangleEntity>();
	Array<FollowEntity>				follows					= new Array<FollowEntity>();
	Array<Line>						lines					= new Array<Line>();
	Line							gameBound;
	GoalEntity						goalEntityRed, goalEntityBlue;
	public GoalController			goalController;

	private boolean					isDoneRectangleAction	= true;
	private boolean					isDoneLineAction		= true;
	private boolean					isDoneGoalAction		= true;
	private boolean					isShowLine				= true;
	private boolean					isShowRect				= true;
	private boolean					isShowGoal				= false;

	public GameSystem() {
	}

	public void render(SpriteBatch batch, float delta) {
		goalController.render(batch, delta);
		for (RectangleEntity entity : rects) {
			entity.render(batch, delta);
		}
		for (Line line : lines) {
			line.render(batch, delta);
		}
		for (FollowEntity follow : follows) {
			follow.render(batch, delta);
			for (RectangleEntity rect : rects) {
				if (follow.getRectangle().overlaps(rect.getRectangle())) {
					follow.reset(new OnCompleteListener() {
						@Override
						public void onComplete() {

						}
					});
				}
			}
		}
	}

	int		count		= 0;
	boolean	isFollowRed	= false;

	void autoCreateFollow() {
		int number = Level.getAutocreateBall();
		if (number == 0)
			return;
		if (follows.size >= number)
			return;
		if (mainPlayer != null)
			count++;
		if (count == 300) {
			FollowEntity followEntity = new FollowEntity(
					isFollowRed ? FollowType.RED : FollowType.BLUE);
			isFollowRed = !isFollowRed;
			followEntity.registerPlayer(mainPlayer);
			follows.add(followEntity);
			count = 0;
		}
	}

	public void reset() {
		blueDone = false;
		redDone = false;
	}

	public void createWorld() {
		rects.clear();
		goalController = GoalController.getInstance();
		Assets.instance.assetMap.loadLevel(Level.getLevel());
		TiledMap map = Assets.instance.assetMap.getMap();
		for (MapObject object : map.getLayers().get(R.LAYER_ENVIROMENT)
				.getObjects()) {
			if (object instanceof RectangleMapObject) {
				if (object.getName() != null
						&& object.getName().equalsIgnoreCase("goalred")) {
					goalController.createGoalRed((RectangleMapObject) object);
				} else if (object.getName() != null
						&& object.getName().equalsIgnoreCase("goalblue")) {
					goalController.createGoalBlue((RectangleMapObject) object);
				} else {
					rects.add(new RectangleEntity((RectangleMapObject) object));
				}
			}
			goalController.validateType();
			if (object instanceof PolylineMapObject) {
				Polyline polyline = ((PolylineMapObject) object).getPolyline();
				float[] vts = polyline.getTransformedVertices();
				Array<Vector2> points = new Array<Vector2>();
				for (int i = 0; i < vts.length - 1; i++) {
					if (i % 2 == 0) {
						Vector2 point = new Vector2(vts[i], vts[i + 1]);
						points.add(point);
					}
				}
				Color color = null;
				if (object.getProperties().containsKey("color")) {
					color = Factory.getColor((String) object.getProperties()
							.get("color"));
				} else if (object.getProperties().containsKey("color_r")) {
					color = new Color(Float.parseFloat((String) object
							.getProperties().get("color_r")) / 255f,
							Float.parseFloat((String) object.getProperties()
									.get("color_g")) / 255f,
							Float.parseFloat((String) object.getProperties()
									.get("color_b")) / 255f,
							Float.parseFloat((String) object.getProperties()
									.get("color_a")));

				}

				Line line = (color == null) ? new Line(points) : new Line(
						points, color);
				line.hide();
				lines.add(line);
				if (object.getName() != null
						&& object.getName().equalsIgnoreCase("gamebound")) {
					gameBound = line;
				}
			}
		}
	}

	public void resetFollow() {
		for (FollowEntity follow : follows) {
			follow.reset(new OnCompleteListener() {
				@Override
				public void onComplete() {
				}
			});
		}
	}

	public void showRectangleEntities(float duration,
			final OnCompleteListener onCompleteListener) {
		if (rects.size == 0) {
			setDoneRectangleAction(true);
			setShowRect(true);
			if (onCompleteListener != null)
				onCompleteListener.onComplete();
			return;
		}
		setDoneRectangleAction(false);
		for (int i = 0; i < rects.size; i++) {
			rects.get(i).show(duration,
					(i != rects.size - 1) ? null : new OnCompleteListener() {

						@Override
						public void onComplete() {
							setShowRect(true);
							setDoneRectangleAction(true);
							if (onCompleteListener != null)
								onCompleteListener.onComplete();
						}
					});
		}
	}

	public void hideRectangleEntities(float duration,
			final OnCompleteListener onCompleteListener) {
		if (rects.size == 0) {
			setDoneRectangleAction(true);
			setShowRect(false);
			if (onCompleteListener != null)
				onCompleteListener.onComplete();
			return;
		}
		setDoneRectangleAction(false);
		for (int i = 0; i < rects.size; i++) {
			rects.get(i).hide(duration,
					(i != rects.size - 1) ? null : new OnCompleteListener() {

						@Override
						public void onComplete() {

							setShowRect(false);
							setDoneRectangleAction(true);
							if (onCompleteListener != null)
								onCompleteListener.onComplete();
						}
					});
		}
	}

	public void showLineEntities(float duration,
			final OnCompleteListener onCompleteListener) {
		if (lines.size == 0) {
			setDoneLineAction(true);
			setShowLine(true);
			if (onCompleteListener != null)
				onCompleteListener.onComplete();
			return;
		}
		setDoneLineAction(false);
		for (int i = 0; i < lines.size; i++) {
			lines.get(i).show(duration,
					(i != lines.size - 1) ? null : new OnCompleteListener() {
						@Override
						public void onComplete() {
							setShowLine(true);
							setDoneLineAction(true);
							if (onCompleteListener != null)
								onCompleteListener.onComplete();
						}
					});
		}
	}

	public void hideLineEntities(float duration,
			final OnCompleteListener onCompleteListener) {
		if (lines.size == 0) {
			setDoneLineAction(true);
			setShowLine(false);
			if (onCompleteListener != null)
				onCompleteListener.onComplete();
			return;
		}
		setDoneLineAction(false);
		for (int i = 0; i < lines.size; i++) {
			lines.get(i).hide(duration,
					(i != lines.size - 1) ? null : new OnCompleteListener() {
						@Override
						public void onComplete() {
							setShowLine(false);
							setDoneLineAction(true);
							if (onCompleteListener != null)
								onCompleteListener.onComplete();
						}
					});
		}
	}

	public void showGoalEntities(float duration,
			final OnCompleteListener onCompleteListener) {
		setDoneGoalAction(false);
		goalController.show(duration, onCompleteListener);
	}

	public void hideGoalEntities(float duration,
			final OnCompleteListener onCompleteListener) {
		setDoneGoalAction(false);

	}

	public void registerMainPlayer(MainPlayer mainPlayer) {
		this.mainPlayer = mainPlayer;
		goalController.registerMainPlayer(mainPlayer);
	}

	public boolean isDoneRectangleAction() {
		return isDoneRectangleAction;
	}

	public void setDoneRectangleAction(boolean isDoneRectangleAction) {
		this.isDoneRectangleAction = isDoneRectangleAction;
	}

	public boolean isDoneLineAction() {
		return isDoneLineAction;
	}

	public void setDoneLineAction(boolean isDoneLineAction) {
		this.isDoneLineAction = isDoneLineAction;
	}

	public boolean isShowLine() {
		return isShowLine;
	}

	public void setShowLine(boolean isShowLine) {
		this.isShowLine = isShowLine;
	}

	public boolean isShowRect() {
		return isShowRect;
	}

	public void setShowRect(boolean isShowRect) {
		this.isShowRect = isShowRect;
	}

	public boolean isDoneGoalAction() {
		return isDoneGoalAction;
	}

	public void setDoneGoalAction(boolean isDoneGoalAction) {
		this.isDoneGoalAction = isDoneGoalAction;
	}

	public boolean isShowGoal() {
		return isShowGoal;
	}

	public void setShowGoal(boolean isShowGoal) {
		this.isShowGoal = isShowGoal;
	}

	public void updateWorldRed(float delta, IGameEvent event) {
		if (gameBound != null
				&& (gameBound.intersect(mainPlayer.getRedBound()))) {
			event.broadcastEvent(EventType.GAME_OVER, mainPlayer.position.x,
					mainPlayer.position.y);
			return;
		}
		for (RectangleEntity entity : rects) {
			entity.update(delta);
		}

		for (FollowEntity followEntity : follows) {
			followEntity.update(delta);
		}

		for (RectangleEntity entity : rects) {
			Rectangle rectangle = entity.getRectangle();
			if (rectangle.overlaps(mainPlayer.getRedBound())) {
				event.broadcastEvent(EventType.GAME_OVER,
						mainPlayer.position.x, mainPlayer.position.y);
				break;
			}
		}

		for (Line line : lines) {
			if (line.intersect(mainPlayer.getRedBound())) {
				event.broadcastEvent(EventType.GAME_OVER,
						mainPlayer.position.x, mainPlayer.position.y);
				break;
			}
		}

		for (FollowEntity followEntity : follows) {
			Rectangle followRect = followEntity.getRectangle();
			Rectangle redRect = mainPlayer.getRedBound();

			if (followRect.overlaps(redRect)) {
				event.broadcastEvent(EventType.GAME_OVER, followRect.x / 2
						+ followRect.width / 4 + redRect.x / 2 + redRect.width
						/ 4, followRect.y / 2 + followRect.height / 4
						+ redRect.y / 2 + redRect.height / 4);
				break;
			}

			for (Line line : lines) {
				if (line.intersect(followRect)) {
					followEntity.reset(new OnCompleteListener() {
						@Override
						public void onComplete() {

						}
					});
					break;
				}
			}
		}
		autoCreateFollow();
	}

	public void updateWorldBlue(float delta, IGameEvent event) {
		if (gameBound != null
				&& (gameBound.intersect(mainPlayer.getBlueBound()))) {
			event.broadcastEvent(EventType.GAME_OVER, mainPlayer.position.x,
					mainPlayer.position.y);
			return;
		}
		for (RectangleEntity entity : rects) {
			entity.update(delta);
		}

		for (FollowEntity followEntity : follows) {
			followEntity.update(delta);
		}

		for (RectangleEntity entity : rects) {
			Rectangle rectangle = entity.getRectangle();
			if (rectangle.overlaps(mainPlayer.getBlueBound())) {
				event.broadcastEvent(EventType.GAME_OVER,
						mainPlayer.position.x, mainPlayer.position.y);
				break;
			}
		}

		for (Line line : lines) {
			if (line.intersect(mainPlayer.getBlueBound())) {
				event.broadcastEvent(EventType.GAME_OVER,
						mainPlayer.position.x, mainPlayer.position.y);
				break;
			}
		}

		for (FollowEntity followEntity : follows) {
			Rectangle followRect = followEntity.getRectangle();
			Rectangle blueRect = mainPlayer.getBlueBound();
			if (followRect.overlaps(blueRect)) {
				event.broadcastEvent(EventType.GAME_OVER, followRect.x / 2
						+ followRect.width / 4 + blueRect.x / 2
						+ blueRect.width / 4, followRect.y / 2
						+ followRect.height / 4 + blueRect.y / 2
						+ blueRect.height / 4);
				break;
			}

			for (Line line : lines) {
				if (line.intersect(followRect)) {
					followEntity.reset(new OnCompleteListener() {
						@Override
						public void onComplete() {

						}
					});
					break;
				}
			}
		}
		autoCreateFollow();
	}

	public void updateWorld(float delta, IGameEvent event) {
		if (gameBound != null
				&& (gameBound.intersect(mainPlayer.getBlueBound()) || gameBound
						.intersect(mainPlayer.getRedBound()))) {
			event.broadcastEvent(EventType.GAME_OVER, mainPlayer.position.x,
					mainPlayer.position.y);
			return;
		}
		for (RectangleEntity entity : rects) {
			entity.update(delta);
		}

		for (FollowEntity followEntity : follows) {
			followEntity.update(delta);
		}

		for (RectangleEntity entity : rects) {
			Rectangle rectangle = entity.getRectangle();
			if (rectangle.overlaps(mainPlayer.getBlueBound())
					|| rectangle.overlaps(mainPlayer.getRedBound())) {
				event.broadcastEvent(EventType.GAME_OVER,
						mainPlayer.position.x, mainPlayer.position.y);
				break;
			}
		}

		for (Line line : lines) {
			if (line.intersect(mainPlayer.getBlueBound())
					|| line.intersect(mainPlayer.getRedBound())) {
				event.broadcastEvent(EventType.GAME_OVER,
						mainPlayer.position.x, mainPlayer.position.y);
				break;
			}
		}

		for (FollowEntity followEntity : follows) {
			Rectangle followRect = followEntity.getRectangle();
			Rectangle blueRect = mainPlayer.getBlueBound();
			Rectangle redRect = mainPlayer.getRedBound();
			if (followRect.overlaps(blueRect)) {
				event.broadcastEvent(EventType.GAME_OVER, followRect.x / 2
						+ followRect.width / 4 + blueRect.x / 2
						+ blueRect.width / 4, followRect.y / 2
						+ followRect.height / 4 + blueRect.y / 2
						+ blueRect.height / 4);
				break;
			}

			if (followRect.overlaps(redRect)) {
				event.broadcastEvent(EventType.GAME_OVER, followRect.x / 2
						+ followRect.width / 4 + redRect.x / 2 + redRect.width
						/ 4, followRect.y / 2 + followRect.height / 4
						+ redRect.y / 2 + redRect.height / 4);
				break;
			}

			for (Line line : lines) {
				if (line.intersect(followRect)) {
					followEntity.reset(new OnCompleteListener() {
						@Override
						public void onComplete() {

						}
					});
					break;
				}
			}
		}
		autoCreateFollow();
	}

}

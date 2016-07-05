package astraeus.game.model.entity.mob;

import astraeus.game.model.Location;
import astraeus.net.packet.out.UpdateMapRegion;

import java.util.Deque;
import java.util.LinkedList;

/**
 * @author Dylan Vicchiarelli
 *
 * @author Graham Edgecombe
 * 
 *         Handles the movement of an entity on the global palate.
 */
public final class Movement {

	/**
	 * The directions for entity X coordinate movement.
	 */
	private static final byte[] DIRECTION_DELTA_X = new byte[] { -1, 0, 1, -1, 1, -1, 0, 1 };

	/**
	 * The directions for entity Y coordinate movement.
	 */
	private static final byte[] DIRECTION_DELTA_Y = new byte[] { 1, 1, 1, 0, 0, -1, -1, -1 };

	/**
	 * The entity performing the movement.
	 */
	private final Mob entity;

	/**
	 * The points of focus.
	 */
	private final Deque<MovementPoint> focusPoints = new LinkedList<MovementPoint>();

	/**
	 * If the entity is currently running.
	 */
	private boolean isRunning = false;

	/**
	 * If the entity running queue is enabled.
	 */
	private boolean isRunningQueueEnabled = false;

	/**
	 * Prevents the entity from moving.
	 */
	private boolean lockMovement;

	/**
	 * The default class constructor.
	 * 
	 * @param entity
	 *            The acting entity.
	 */
	public Movement(Mob entity) {
		this.entity = entity;
	}

	/**
	 * Completes the movement path of the entity.
	 */
	public final void finish() {
		focusPoints.removeFirst();
	}

	/**
	 * Returns the acting entity.
	 *
	 * @return The returned entity.
	 */
	public final Mob getEntity() {
		return entity;
	}

	/**
	 * Checks if the entity is running.
	 *
	 * @return The result of the check,
	 */
	public final boolean isRunning() {
		return isRunning;
	}

	/**
	 * Modifies if the entity is running.
	 *
	 * @param isRunning
	 *            The new modification.
	 */
	public final void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	/**
	 * Checks if the entity running Queue is enabled.
	 *
	 * @return The result of the check.
	 */
	public final boolean isRunningQueueEnabled() {
		return isRunningQueueEnabled;
	}

	/**
	 * Modifies if the entity running Queue is enabled.
	 *
	 * @param isRunningQueueEnabled
	 *            The new modification.
	 */
	public final void setRunningQueueEnabled(boolean isRunningQueueEnabled) {
		this.isRunningQueueEnabled = isRunningQueueEnabled;
	}

	/**
	 * Resets the movement variables of the entity.
	 */
	public final void reset() {
		setRunningQueueEnabled(false);
		focusPoints.clear();
		focusPoints.add(new MovementPoint(getEntity().getLocation().getX(), getEntity().getLocation().getY(), -1));
	}

	/**
	 * Handles the cycling movement requests of the entity. This method receives
	 * prior processing to the main updating procedure to ensure that all
	 * movement is registered in time to be updated in synchronization with the
	 * main procedure.
	 */
	public final void handleEntityMovement() {
		if (lockMovement) {
			return;
		}

		MovementPoint walkingPoint = null, runningPoint = null;

		walkingPoint = getNextPoint();

		if (isRunning()) {
			runningPoint = getNextPoint();
		}

		getEntity().setWalkingDirection(walkingPoint == null ? -1 : walkingPoint.getDirection());

		getEntity().setRunningDirection(runningPoint == null ? -1 : runningPoint.getDirection());

		int deltaX = entity.getLocation().getX() - entity.getLastLocation().getRegionalX() * 8;

		int deltaY = entity.getLocation().getY() - entity.getLastLocation().getRegionalY() * 8;
		
		if (entity.isPlayer()) {
			if (deltaX < 16 || deltaX >= 88 || deltaY < 16 || deltaY > 88) {
				entity.getPlayer().send(new UpdateMapRegion());
			}

			if (walkingPoint != null || runningPoint != null) {
				entity.getMob().onMovement();
			}
		}
	}

	/**
	 * Returns the next movement point in the designated path.
	 * 
	 * @return The next point that was returned.
	 */
	public final MovementPoint getNextPoint() {

		MovementPoint availableFocusPoint = focusPoints.poll();

		if (availableFocusPoint == null || availableFocusPoint.getDirection() == -1) {
			return null;
		} else {
			getEntity().getLocation().move(DIRECTION_DELTA_X[availableFocusPoint.getDirection()], DIRECTION_DELTA_Y[availableFocusPoint.getDirection()], getEntity().getLocation().getHeight());
			return availableFocusPoint;
		}
	}

	public final void walk(Location location) {
		reset();
		addToPath(location);
		finish();
	}

	public final void addToPath(Location location) {

		if (focusPoints.isEmpty()) {
			reset();
		}

		MovementPoint last = focusPoints.peekLast();

		int deltaX = location.getX() - last.getX();

		int deltaY = location.getY() - last.getY();
		
		int max = Math.max(Math.abs(deltaX), Math.abs(deltaY));

		for (int i = 0; i < max; i++) {

			if (deltaX < 0) {
				deltaX++;
			} else if (deltaX > 0) {
				deltaX--;
			}

			if (deltaY < 0) {
				deltaY++;
			} else if (deltaY > 0) {
				deltaY--;
			}

			addStep(location.getX() - deltaX, location.getY() - deltaY);
		}
	}

	/**
	 * Adds a step into the internal memory of the movement Queue.
	 * 
	 * @param x
	 *            The X coordinate of the step.
	 * 
	 * @param y
	 *            The Y coordinate of the step.
	 */
	private final void addStep(int x, int y) {

		if (focusPoints.size() >= 50) {
			return;
		}

		MovementPoint lastPosition = focusPoints.peekLast();

		int direction = parseDirection(x - lastPosition.getX(), y - lastPosition.getY());
		
		if (direction > -1) {
			focusPoints.add(new MovementPoint(x, y, direction));
		}
	}

	public static int parseDirection(int deltaX, int deltaY) {
		if (deltaX < 0) {
			if (deltaY < 0) {
				return 5;
			}
			if (deltaY > 0) {
				return 0;
			}
			return 3;
		}
		if (deltaX > 0) {
			if (deltaY < 0) {
				return 7;
			}
			if (deltaY > 0) {
				return 2;
			}
			return 4;
		}
		if (deltaY < 0) {
			return 6;
		}
		if (deltaY > 0) {
			return 1;
		}
		return -1;
	}

	public boolean isMoving() {
		return !focusPoints.isEmpty();
	}

	public boolean isMovementDone() {
		return focusPoints.isEmpty();
	}

	public boolean isLockMovement() {
		return lockMovement;
	}

	public void setLockMovement(boolean lockMovement) {
		this.lockMovement = lockMovement;
	}

	/**
	 * Represents a point that an entity is moving towards.
	 *
	 * @author Seven
	 */
	private final class MovementPoint {

		/**
		 * The {@code x} coordinate of this point.
		 */
		private final int x;

		/**
		 * The {@code y} coordinate of this point.
		 */
		private final int y;

		/**
		 * The facing {@code direction} for this point.
		 */
		private final int direction;

		/**
		 * Creates a new {@link MovementPoint}.
		 *
		 * @param x
		 *            The {@code x} coordinate.
		 *
		 * @param y
		 *            The {@code y} coordinate.
		 *
		 * @param direction
		 *            The facing {@code direction} of this entity.
		 */
		public MovementPoint(int x, int y, int direction) {
			this.x = x;
			this.y = y;
			this.direction = direction;
		}

		/**
		 * Gets the direction the entity is facing.
		 *
		 * @return direction
		 */
		public int getDirection() {
			return direction;
		}

		/**
		 * Gets the x coordinate.
		 *
		 * @return x
		 */
		public int getX() {
			return x;
		}

		/**
		 * Gets the y coordinate.
		 *
		 * @return y
		 */
		public int getY() {
			return y;
		}

	}

}

package shared.entities;

import org.lwjgl.util.vector.Vector2f;

import client.WorldView;
import shared.Entity;
import shared.MoveableEntity;
import shared.World;

/**
 * A simple example of how to implement a game entity.
 * @author Chris
 *
 */
public class ExampleEntity extends MoveableEntity {

	private long nextColorChangeTime = 0;

	private final int WIDTH = 50;
	private final int HEIGHT = 100;
	
	private final long MOVE_INTERVAL_MS = 500;
	
	public ExampleEntity(World world, float x, float y) {
		super(world, x, y);
	}

	@Override
	public void update(int delta) {
		if (this.getWorld().getTime() < this.nextColorChangeTime)
			return;
		
		this.nextColorChangeTime += MOVE_INTERVAL_MS;		
	}

	@Override
	public void render(WorldView view) {
		Vector2f p = getPosition();	
		view.drawWorldRectangleCentered(p.getX(), p.getY(), WIDTH, HEIGHT, 1.0f, 0.0f, 1.0f, true);	
	}

}

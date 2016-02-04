package shared.entities;

import shared.World;

import java.io.IOException;
import java.io.DataInputStream;

/**
 * A boundary used to block access to somewhere.
 * Used to surround the map and limit map size.
 * @author Chris, Barney
 *
 */
public class Boundary extends TexturedBlock {
	
	
	//****Constructors****
	
	
	public Boundary(World world, float x, float y, int width, int height) {
	
		super(world, x, y, width, height, "boundary");
		
	}

	public Boundary(World world, DataInputStream in) throws IOException {
		
		super(world, in);
		
	}
	
}

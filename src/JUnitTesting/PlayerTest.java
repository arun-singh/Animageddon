package JUnitTesting;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import client.ClientWorld;
import shared.GameWindow;
import shared.entities.Flag;
import shared.entities.Player;

/**
 * Test for the player
 * @author Richard, Thomas
 *
 */
public class PlayerTest {
	
	ClientWorld clientWorld = null;
	
	Player testPlayer = null;	
	
	int xPos;
	
	int yPos;
	
	Random random = new Random();

	@Before
	public void setUp() throws Exception {
		
		clientWorld = new ClientWorld(new GameWindow());
		
		xPos = random.nextInt();
		
		yPos = random.nextInt();
		
		testPlayer = new Player(clientWorld, xPos, yPos);
		
		assertNotNull("Checks the player entity was created successfully by the test class' setup method", testPlayer);

		
	}

	@Test
	public void testWriteToNetStream() throws IOException {
		
		long playerHealth = 0;
		
		//Create expected value
		ByteArrayOutputStream tempBOut = new ByteArrayOutputStream();
		DataOutputStream tempOut = new DataOutputStream(tempBOut);
						
		tempOut.writeInt(0);
		tempOut.writeFloat(xPos);
		tempOut.writeFloat(yPos);
		
		//Networked velocity info
		tempOut.writeFloat(0.0f);
		tempOut.writeFloat(0.0f);
		tempOut.writeFloat(0.0f);
		tempOut.writeFloat(0.0f);
		tempOut.writeLong(0);
		tempOut.writeInt(0);
		tempOut.writeBoolean(false);
		
		
		//Networked entity id - held flag
		tempOut.writeInt(-1);
		
		//Networked entity id - orientation
		tempOut.writeFloat(0.0f);
		
		//Networked vector info - aim vector
		tempOut.writeFloat(0.0f);
		tempOut.writeFloat(0.0f);
		
		//Networked boolean info - muzzle flash
		tempOut.writeBoolean(false);
		
		//Networked boolean info - isDead
		tempOut.writeBoolean(false);
		
		//Networked long info - health
		tempOut.writeLong(playerHealth);
		
		//Networked string info - class name (empty because it is run on the client side world)
		tempOut.writeUTF("");
		
		byte[] expectedOutput = tempBOut.toByteArray(); 
					
		tempOut.close();
		tempBOut.close();
						
						
		//Get actual value		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bout);
						
		testPlayer.writeToNetStream(out);
					
		byte[] actualOutput = bout.toByteArray();

				
		//Compare for equality
		assertEquals("The output should be the id, the postition, the velocity information, the heldFlag id, the orientation id, "
				+ "the aim vector, the muzzle flash, the isDead information, the player's health and the class name", new String(expectedOutput), new String(actualOutput));
						
				
		//Close streams
		out.close();
		bout.close();
		
	}

	@Test
	public void testDie() {
		
		Flag testFlag = new Flag(clientWorld, 0, 0);
		
		testFlag.setFlagHolder(testPlayer);
		
		testPlayer.setHeldFlag(testFlag);
		
		assertFalse("Player should not be dead initially", testPlayer.getIsDead());
		
		assertNotNull("Player should be holding a flag initially", testPlayer.getHeldFlag());
		
		assertNotNull("The flag should be held by the player initially", testFlag.getFlagHolder());
		
		testPlayer.die();
		
		assertTrue("Player should be set to 'dead' following the die method", testPlayer.getIsDead());
		
		assertNull("Player should not be holding a flag following the die method", testPlayer.getHeldFlag());
		
		assertNull("The flag should not be held by anyone following the die method", testFlag.getFlagHolder());
		
	}

	@Test
	public void testDoDamage() {
		
		long startingHealth = 0;

		assertEquals("Player's health should be " + startingHealth + " before the test", startingHealth, testPlayer.getHealth());
		
		long damage = 10;
		
		testPlayer.doDamage(damage);
		
		long newHealth = startingHealth - damage;
		
		assertEquals("Player's health should be " + newHealth + " following the test", newHealth, testPlayer.getHealth());
				
		
	}
	
}

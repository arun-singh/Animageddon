package JUnitTesting;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.lwjgl.util.vector.Vector2f;

import shared.entities.Flag;
import shared.entities.Player;
import client.ClientWorld;

/**
 * Test for the flag
 * @author Richard
 *
 */
public class FlagTest {

	int flagxPos;
	
	int flagyPos;
	
	int playerxPos;
	
	int playeryPos;
	
	Random random = new Random();
	
	Flag testFlag = null;
	
	Player testPlayer = null;
	
	@Before
	public void setUp() throws Exception {
		
		ClientWorld clientWorld = new ClientWorld(null);
		
		flagxPos = random.nextInt();
		
		flagyPos = random.nextInt();
		
		testFlag = new Flag(clientWorld, flagxPos, flagyPos);
		
		playerxPos = random.nextInt();
		
		playeryPos = random.nextInt();
		
		testPlayer = new Player(clientWorld, playerxPos, playeryPos);
		
		assertNotNull("Checks the Flag entity was created successfully by the test class' setup method", testFlag);
		
		assertNotNull("Checks the player entity was created successfully by the test class' setup method", testPlayer);
		
	}

	//TODO
	@Test
	public void testWriteToNetStream() throws IOException {
		
		//Create expected value
		ByteArrayOutputStream tempBOut = new ByteArrayOutputStream();
		DataOutputStream tempOut = new DataOutputStream(tempBOut);
						
		tempOut.writeInt(0);
		tempOut.writeFloat(flagxPos);
		tempOut.writeFloat(flagyPos);
		
		//networked velocity info
		tempOut.writeFloat(0.0f);
		tempOut.writeFloat(0.0f);
		tempOut.writeFloat(0.0f);
		tempOut.writeFloat(0.0f);
		tempOut.writeLong(0);
		tempOut.writeInt(0);
		tempOut.writeBoolean(false);
		
		tempOut.writeInt(-1);
		
		byte[] expectedOutput = tempBOut.toByteArray(); 
					
		tempOut.close();
		tempBOut.close();
						
						
		//Get actual value		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bout);
						
		testFlag.writeToNetStream(out);
					
		byte[] actualOutput = bout.toByteArray();

				
		//Compare for equality
		assertEquals("The output should be the id, the postition, the networked velocity information and the networked entity id", new String(expectedOutput), new String(actualOutput));
						
				
		//Close streams
		out.close();
		bout.close();
	}

	@Test
	public void testIsTouchable() {
		
		assertTrue("The flag should be touchable because there is no flag holder yet", testFlag.isTouchable());
		
		//Set a flag holder
		testFlag.setFlagHolder(testPlayer);
		
		assertFalse("The flag should not be touchable because there is a flag holder now", testFlag.isTouchable());	
		
	}

	@Test
	public void testOnTouch() {
		
		assertNull("The flag holder should be null initially", testFlag.getFlagHolder());
		
		testFlag.onTouch(testPlayer);
		
		assertNotNull("The flag holder should not be null after onTouch", testFlag.getFlagHolder());
		
		
	}

	//TODO
	@Test
	public void testReset() {
		
		testFlag.setFlagHolder(testPlayer);
		
		assertNotNull("Tests the flag holder is not null - setup for testReset", testFlag.getFlagHolder());
		
		testFlag.reset();
		
		assertNull("The flag holder should be null following a reset", testFlag.getFlagHolder());
		
		assertEquals("The flag position should be reset to it's original value", new Vector2f(flagxPos, flagyPos), testFlag.getPosition());	
		
	}

	@Test
	public void testSetFlagHolder() {
		
		assertNull("There should be no flag holder initially", testFlag.getFlagHolder());

		testFlag.setFlagHolder(testPlayer);
		
		assertEquals("The player should be set as the flag holder", testPlayer, testFlag.getFlagHolder());
		
		assertEquals("The flag position should be set to the player's position", testPlayer.getPosition(), testFlag.getPosition());
				
	}

}

/**
 * @author Richard Maskell 1238287
 */
package JUnitTesting;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import client.ClientWorld;
import shared.entities.TexturedBlock;

/**
 * Test for the textured block
 * @author Richard
 *
 */
public class TexturedBlockTest {
	
	TexturedBlock testBlock = null;
	
	float xPos;
	
	float yPos;
	
	int width;
	
	int height;
	
	String textureName = "Test Block";
	
	Random random = new Random();

	@Before
	public void setUp() throws Exception {
		
		ClientWorld clientWorld = new ClientWorld(null);
		
		xPos = random.nextFloat();
		
		yPos = random.nextFloat();
		
		width = random.nextInt();
		
		height = random.nextInt();
		
		testBlock = new TexturedBlock(clientWorld, xPos, yPos, width, height, "Test Block");
		
		assertNotNull("Checks the TexturedBlock object was created successfully", testBlock);

	}

	@Test
	public void testWriteToNetStream() throws IOException {
		
		//Create expected value
		ByteArrayOutputStream tempBOut = new ByteArrayOutputStream();
		DataOutputStream tempOut = new DataOutputStream(tempBOut);
				
		tempOut.writeInt(0);
		tempOut.writeFloat(xPos);
		tempOut.writeFloat(yPos);	
		tempOut.writeInt(width);
		tempOut.writeInt(height);
		tempOut.writeUTF(textureName);
		
		byte[] expectedOutput = tempBOut.toByteArray(); 
				
		tempOut.close();
		tempBOut.close();
				
				
		//Get actual value		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bout);
				
		testBlock.writeToNetStream(out);
			
		byte[] actualOutput = bout.toByteArray();

		
		//Compare for equality
		assertEquals("The output should be the id, the postition, the dimensions and the texture name", new String(expectedOutput), new String(actualOutput));
				
		
		//Close streams
		out.close();
		bout.close();
		
	}

}

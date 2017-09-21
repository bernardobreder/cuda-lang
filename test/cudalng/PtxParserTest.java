package cudalng;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;

import org.junit.Test;

public class PtxParserTest {

	@Test
	public void test() throws IOException, ParseException {
		PtxFile ptxFile = new PtxParser(new PtxLexer(Files.newBufferedReader(Paths.get("cuda.ptx")))).parse();
		assertEquals(14, ptxFile.getVersion(), 0.01);
		assertEquals("sm_10", ptxFile.getTarget());
	}

}

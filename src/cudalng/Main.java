package cudalng;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

	public static void main(String[] args) throws IOException {
		Path path = Paths.get("cuda.ptx");
		PtxLexer tokenizer = new PtxLexer(Files.newBufferedReader(path));
		while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
			if (tokenizer.ttype == StreamTokenizer.TT_WORD) {
				System.out.println(tokenizer.sval);
			} else if (tokenizer.ttype == StreamTokenizer.TT_NUMBER) {
				System.out.println(tokenizer.nval);
			} else {
				System.out.println((char) tokenizer.ttype);
			}
		}
	}

}

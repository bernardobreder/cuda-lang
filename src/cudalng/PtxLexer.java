package cudalng;

import java.io.Reader;
import java.io.StreamTokenizer;

public class PtxLexer extends StreamTokenizer {

	public PtxLexer(Reader reader) {
		super(reader);
		slashStarComments(true);
		slashSlashComments(true);
		quoteChar('\"');
		eolIsSignificant(false);
		whitespaceChars(0, ' ');
		ordinaryChar('.');
		wordChars('a', 'z');
		wordChars('A', 'Z');
		wordChars('_', '_');
		wordChars('%', '%');
//		wordChars('.', '.');
	}

}

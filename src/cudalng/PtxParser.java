package cudalng;

import static java.io.StreamTokenizer.TT_EOF;
import static java.io.StreamTokenizer.TT_NUMBER;
import static java.io.StreamTokenizer.TT_WORD;

import java.io.EOFException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import cudalng.PtxNode.BitWidthType;
import cudalng.PtxNode.CacheOperationType;
import cudalng.PtxNode.PtxAddNode;
import cudalng.PtxNode.PtxAddValueNode;
import cudalng.PtxNode.PtxCmdNode;
import cudalng.PtxNode.PtxCvtNode;
import cudalng.PtxNode.PtxDoubleNode;
import cudalng.PtxNode.PtxEntryNode;
import cudalng.PtxNode.PtxExitNode;
import cudalng.PtxNode.PtxIntegerNode;
import cudalng.PtxNode.PtxLdNode;
import cudalng.PtxNode.PtxLongNode;
import cudalng.PtxNode.PtxMulNode;
import cudalng.PtxNode.PtxParamNode;
import cudalng.PtxNode.PtxParameterNode;
import cudalng.PtxNode.PtxRegisterNode;
import cudalng.PtxNode.PtxStNode;
import cudalng.PtxNode.PtxSubNode;
import cudalng.PtxNode.PtxThreadIdNode;
import cudalng.PtxNode.PtxValueNode;
import cudalng.PtxNode.StageSpaceType;
import cudalng.PtxNode.PtxValueType;

public class PtxParser {

	private static boolean equal(String left, String right) {
		int len = left.length();
		if (len != right.length()) {
			return false;
		} else if (left.charAt(0) != right.charAt(0)) {
			return false;
		}
		switch (len) {
		case 1:
			return true;
		case 2:
			return left.charAt(1) == right.charAt(1);
		case 3:
			return left.charAt(1) == right.charAt(1) && left.charAt(2) == right.charAt(2);
		case 4:
			return left.charAt(1) == right.charAt(1) && left.charAt(2) == right.charAt(2)
					&& left.charAt(3) == right.charAt(3);
		case 5:
			return left.charAt(1) == right.charAt(1) && left.charAt(2) == right.charAt(2)
					&& left.charAt(3) == right.charAt(3) && left.charAt(4) == right.charAt(4);
		}
		return left.equals(right);
	}

	private static PtxValueType type(String name) {
		try {
			return PtxValueType.valueOf(name.toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private PtxLexer lex;

	private boolean weak = false;

	private boolean visible = false;

	private double version = 1.0f;

	private String target = "sm_10";

	private int addressSize = 64;

	private final List<PtxEntryNode> entrys = new ArrayList<>();

	public PtxParser(PtxLexer lex) {
		this.lex = lex;
	}

	private EOFException exceptionEof() {
		return new EOFException();
	}

	private ParseException exceptionExpected(String expected) {
		return new ParseException("expected " + expected + " value", lex.lineno());
	}

	private ParseException exceptionStringParse() {
		return new ParseException(lex.sval, lex.lineno());
	}

	private boolean lookSymbol(char symbol) throws IOException, ParseException {
		if (lex.nextToken() == symbol) {
			return true;
		}
		pushBack();
		return false;
	}

	public PtxFile parse() throws IOException, ParseException {
		while (lookSymbol('.')) {
			switch (readId()) {
			case "version": {
				version = readDouble();
				break;
			}
			case "target": {
				target = readId();
				if (lookSymbol(',')) {
					readId();
				}
				break;
			}
			case "file": {
				readInt();
				readString();
				if (lookSymbol(',')) {
					readLong();
					if (lookSymbol(',')) {
						readLong();
					}
				}
				break;
			}
			case "address_size": {
				addressSize = readInt();
				break;
			}
			case "weak": {
				weak = true;
				break;
			}
			case "visible": {
				visible = true;
				break;
			}
			case "func": {
				reset();
				break;
			}
			case "entry": {
				String name = readId();
				List<PtxParamNode> paramList = parseEntryParamList();
				List<PtxCmdNode> cmdList = parseEntryCmdList();
				entrys.add(new PtxEntryNode(name,paramList,cmdList));
				reset();
				break;
			}
			}
		}
		int versionInt = (int) (version * 10);
		return new PtxFile(versionInt, target, addressSize,entrys);
	}

	private Optional<CacheOperationType> parseAttrCacheOperation() throws IOException, ParseException {
		CacheOperationType value = parseCacheOperation();
		if (value == null) {
			pushBack();
			return Optional.empty();
		} else {
			readSymbol('.');
			return Optional.of(value);
		}
	}

	private boolean parseAttrFrnd() throws IOException, ParseException {
		return parseAttrWord("frnd");
	}

	private boolean parseAttrFtz() throws IOException, ParseException {
		return parseAttrWord("ftz");
	}

	private boolean parseAttrIrnd() throws IOException, ParseException {
		return parseAttrWord("irnd");
	}

	private boolean parseAttrSat() throws IOException, ParseException {
		return parseAttrWord("sat");
	}

	private Optional<StageSpaceType> parseAttrStageSpace() throws IOException, ParseException {
		StageSpaceType value = parseStageSpace();
		if (value == null) {
			pushBack();
			return Optional.empty();
		} else {
			readSymbol('.');
			return Optional.of(value);
		}
	}

	private boolean parseAttrVec() throws IOException, ParseException {
		return parseAttrWord("vec");
	}

	private boolean parseAttrVolatile() throws IOException, ParseException {
		return parseAttrWord("volatile");
	}

	private boolean parseAttrWord(String right) throws IOException, ParseException {
		if (!equal(readId(), right)) {
			pushBack();
			return false;
		} else {
			readSymbol('.');
			return true;
		}
	}

	private BitWidthType parseBitWidth() throws IOException, ParseException {
		try {
			return BitWidthType.valueOf(readId().toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private CacheOperationType parseCacheOperation() throws IOException, ParseException {
		try {
			return CacheOperationType.valueOf(readId().toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	protected PtxCmdNode parseCmd() throws IOException, ParseException {
		if (lookSymbol('.')) {
			switch (readId()) {
			case "reg": {
				return parseCmdReg();
			}
			case "loc": {
				return parseCmdLoc();
			}
			default:
				throw exceptionStringParse();
			}
		}
		switch (readId()) {
		case "add": {
			return parseCmdAdd();
		}
		case "cvt": {
			return parseCmdCvt();
		}
		case "ld": {
			return parseCmdLd();
		}
		case "st": {
			return parseCmdSt();
		}
		case "exit": {
			return parseCmdExit();
		}
		case "mul": {
			return parseCmdMul();
		}
		case "sub": {
			return parseCmdSub();
		}
		}
		throw exceptionStringParse();
	}

	protected PtxAddNode parseCmdAdd() throws IOException, ParseException {
		readSymbol('.');
		boolean sat = parseAttrSat();
		PtxValueType type = parseType();
		PtxValueNode d = parseValue();
		readSymbol(',');
		PtxValueNode a = parseValue();
		readSymbol(',');
		PtxValueNode b = parseValue();
		readSymbol(';');
		return new PtxAddNode(sat, type, d, a, b);
	}

	protected PtxCvtNode parseCmdCvt() throws IOException, ParseException {
		readSymbol('.');
		boolean irnd = parseAttrIrnd();
		boolean frnd = parseAttrFrnd();
		boolean ftz = parseAttrFtz();
		boolean sat = parseAttrSat();
		PtxValueType leftType = parseType();
		readSymbol('.');
		PtxValueType rightType = parseType();
		PtxValueNode d = parseValue();
		readSymbol(',');
		PtxValueNode a = parseValue();
		readSymbol(';');
		return new PtxCvtNode(irnd, frnd, ftz, sat, leftType, rightType, d, a);
	}

	protected PtxExitNode parseCmdExit() throws IOException, ParseException {
		readSymbol(';');
		return new PtxExitNode();
	}

	protected PtxLdNode parseCmdLd() throws IOException, ParseException {
		readSymbol('.');
		boolean vol = parseAttrVolatile();
		Optional<StageSpaceType> stage = parseAttrStageSpace();
		Optional<CacheOperationType> cache = parseAttrCacheOperation();
		boolean vec = parseAttrVec();
		PtxValueType type = parseType();
		PtxValueNode d = parseValue();
		Optional<PtxValueNode> a = Optional.empty();
		if (lookSymbol(',')) {
			a = Optional.of(parseValue());
		}
		readSymbol(';');
		return new PtxLdNode(vol, vec, stage, cache, type, d, a);
	}

	protected PtxCmdNode parseCmdLoc() throws IOException, ParseException {
		readInt();
		readInt();
		readInt();
		return null;
	}

	protected PtxMulNode parseCmdMul() throws IOException, ParseException {
		readSymbol('.');
		BitWidthType width = parseBitWidth();
		readSymbol('.');
		PtxValueType type = parseType();
		PtxValueNode d = parseValue();
		readSymbol(',');
		PtxValueNode a = parseValue();
		readSymbol(',');
		PtxValueNode b = parseValue();
		readSymbol(';');
		return new PtxMulNode(width, type, d, a, b);
	}

	protected PtxCmdNode parseCmdReg() throws IOException, ParseException {
		readSymbol('.');
		parseType();
		readId();
		readSymbol('<');
		readInt();
		readSymbol('>');
		readSymbol(';');
		return null;
	}

	protected PtxStNode parseCmdSt() throws IOException, ParseException {
		readSymbol('.');
		boolean vol = parseAttrVolatile();
		Optional<StageSpaceType> stage = parseAttrStageSpace();
		Optional<CacheOperationType> cache = parseAttrCacheOperation();
		boolean vec = parseAttrVec();
		PtxValueType type = parseType();
		PtxValueNode a = parseValue();
		readSymbol(',');
		PtxValueNode b = parseValue();
		readSymbol(';');
		return new PtxStNode(vol, vec, stage, cache, type, a, b);
	}

	protected PtxSubNode parseCmdSub() throws IOException, ParseException {
		readSymbol('.');
		boolean sat = parseAttrSat();
		PtxValueType type = parseType();
		PtxValueNode d = parseValue();
		readSymbol(',');
		PtxValueNode a = parseValue();
		readSymbol(',');
		PtxValueNode b = parseValue();
		readSymbol(';');
		return new PtxSubNode(sat, type, d, a, b);
	}

	private List<PtxCmdNode> parseEntryCmdList() throws IOException, ParseException {
		List<PtxCmdNode> list = new ArrayList<>();
		readSymbol('{');
		while (!lookSymbol('}')) {
			list.add(parseCmd());
		}
		return list;
	}

	private PtxParamNode parseEntryParam() throws IOException, ParseException {
		readParamWord();
		readSymbol('.');
		PtxValueType type = parseType();
		String name = readId();
		return new PtxParamNode(type, name);
	}

	private List<PtxParamNode> parseEntryParamList() throws IOException, ParseException {
		List<PtxParamNode> list = new ArrayList<>();
		readSymbol('(');
		if (!lookSymbol(')')) {
			list.add(parseEntryParam());
			while (lookSymbol(',')) {
				list.add(parseEntryParam());
			}
		}
		readSymbol(')');
		return list;
	}

	private StageSpaceType parseStageSpace() throws IOException, ParseException {
		try {
			return StageSpaceType.valueOf(readId().toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private PtxValueType parseType() throws IOException, ParseException {
		return type(readId());
	}

	protected PtxValueNode parseValue() throws IOException, ParseException {
		if (lex.nextToken() == TT_EOF) {
			throw exceptionEof();
		}
		switch (lex.ttype) {
		case TT_WORD: {
			String sval = lex.sval;
			if (sval.startsWith("%r")) {
				return new PtxRegisterNode(sval);
			} else if (sval.startsWith("__cudaparm__")) {
				return new PtxParameterNode(sval);
			} else if (sval.startsWith("%ctaid")) {
				readSymbol('.');
				int index = readId().charAt(0) - 'x';
				return new PtxThreadIdNode(index);
			}
		}
		case TT_NUMBER: {
			double doubleValue = lex.nval;
			long longValue = (long) doubleValue;
			if (doubleValue == longValue) {
				if (doubleValue < Integer.MAX_VALUE) {
					return new PtxIntegerNode((int) doubleValue);
				} else {
					return new PtxLongNode(longValue);
				}
			} else {
				return new PtxDoubleNode(doubleValue);
			}
		}
		case '[': {
			PtxValueNode left = parseValue();
			if (lookSymbol('+')) {
				PtxValueNode right = parseValue();
				readSymbol(']');
				return new PtxAddValueNode(left, right);
			}
			readSymbol(']');
			return left;
		}
		}
		throw new ParseException("expected value", lex.lineno());
	}

	private void pushBack() {
		lex.pushBack();
	}

	private double readDouble() throws IOException, ParseException {
		switch (lex.nextToken()) {
		case TT_EOF:
			throw exceptionEof();
		case TT_NUMBER:
			return lex.nval;
		default:
			throw exceptionExpected("double");
		}
	}

	private String readId() throws IOException, ParseException {
		switch (lex.nextToken()) {
		case TT_EOF:
			throw exceptionEof();
		case TT_WORD:
			return lex.sval;
		default:
			throw exceptionExpected("string");
		}
	}

	private int readInt() throws IOException, ParseException {
		switch (lex.nextToken()) {
		case TT_EOF:
			throw exceptionEof();
		case TT_NUMBER:
			return (int) lex.nval;
		default:
			throw exceptionExpected("integer");
		}
	}

	private long readLong() throws IOException, ParseException {
		switch (lex.nextToken()) {
		case TT_EOF:
			throw exceptionEof();
		case TT_NUMBER:
			return (long) lex.nval;
		default:
			throw exceptionExpected("long");
		}
	}

	private String readParamWord() throws IOException, ParseException {
		readSymbol('.');
		if (lex.nextToken() == TT_EOF) {
			throw exceptionEof();
		} else if (lex.ttype != TT_WORD || !equal(lex.sval, "param")) {
			throw new ParseException("expected .param", lex.lineno());
		}
		return lex.sval;
	}

	private String readString() throws IOException, ParseException {
		switch (lex.nextToken()) {
		case TT_EOF:
			throw exceptionEof();
		case '\"':
			return lex.sval;
		default:
			throw exceptionExpected("string");
		}
	}

	private void readSymbol(char symbol) throws IOException, ParseException {
		switch (lex.nextToken()) {
		case TT_EOF:
			throw exceptionEof();
		case TT_NUMBER:
		case TT_WORD:
			throw exceptionExpected("" + symbol);
		}
	}

	private void reset() {
		weak = false;
		visible = false;
	}

}

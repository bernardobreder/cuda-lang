package cudalng;

import java.util.List;

import cudalng.PtxNode.PtxEntryNode;

public class PtxFile {

	private final int version;

	private final String target;

	private final int addressSize;

	private final List<PtxEntryNode> entrys;

	public PtxFile(int version, String target, int addressSize, List<PtxEntryNode> entrys) {
		super();
		this.version = version;
		this.target = target;
		this.addressSize = addressSize;
		this.entrys = entrys;
	}

	public int getVersion() {
		return version;
	}

	public String getTarget() {
		return target;
	}

	public int getAddressSize() {
		return addressSize;
	}

	public List<PtxEntryNode> getEntrys() {
		return entrys;
	}

}

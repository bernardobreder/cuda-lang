package cudalng;

import java.util.List;
import java.util.Optional;

public class PtxNode {

	public static enum PtxValueType {

		S8, S16, S32, S64, U8, U16, U32, U64;

	}

	public static enum BitWidthType {

		WIDE, LO, HI;

	}

	public static enum CacheOperationType {

		CA, CG, CS, LU, CV

	}

	public static enum StageSpaceType {

		CONST, GLOBAL, LOCAL, PARAM, SHARED;

	}

	public static class PtxValueNode extends PtxNode {

	}

	public static class PtxCmdNode extends PtxNode {

	}

	public static class PtxParamNode extends PtxNode {

		private final PtxValueType type;

		private final String name;

		public PtxParamNode(PtxValueType type, String name) {
			super();
			this.type = type;
			this.name = name;
		}

	}

	public static class PtxEntryNode extends PtxNode {

		private final String name;

		private final List<PtxParamNode> paramList;

		private final List<PtxCmdNode> cmdList;

		public PtxEntryNode(String name, List<PtxParamNode> paramList, List<PtxCmdNode> cmdList) {
			this.name = name;
			this.paramList = paramList;
			this.cmdList = cmdList;
		}

	}

	public static class PtxIntegerNode extends PtxValueNode {

		private final int value;

		public PtxIntegerNode(int value) {
			this.value = value;
		}

	}

	public static class PtxLongNode extends PtxValueNode {

		private final long value;

		public PtxLongNode(long value) {
			this.value = value;
		}

	}

	public static class PtxDoubleNode extends PtxValueNode {

		private final double value;

		public PtxDoubleNode(double value) {
			this.value = value;
		}

	}

	public static class PtxRegisterNode extends PtxValueNode {

		private final String name;

		public PtxRegisterNode(String name) {
			this.name = name;
		}

	}

	public static class PtxParameterNode extends PtxValueNode {

		private final String name;

		public PtxParameterNode(String name) {
			this.name = name;
		}

	}

	public static class PtxThreadIdNode extends PtxValueNode {

		private final int index;

		public PtxThreadIdNode(int index) {
			super();
			this.index = index;
		}

	}

	public static class PtxAddValueNode extends PtxValueNode {

		private final PtxValueNode left;

		private final PtxValueNode right;

		public PtxAddValueNode(PtxValueNode left, PtxValueNode right) {
			super();
			this.left = left;
			this.right = right;
		}

	}

	public static class PtxAddNode extends PtxCmdNode {

		private final boolean sat;

		private final PtxValueType type;

		private final PtxValueNode target;

		private final PtxValueNode left;

		private final PtxValueNode right;

		public PtxAddNode(boolean sat, PtxValueType type, PtxValueNode target, PtxValueNode left, PtxValueNode right) {
			this.sat = sat;
			this.type = type;
			this.target = target;
			this.left = left;
			this.right = right;
		}

	}

	public static class PtxSubNode extends PtxCmdNode {

		private final boolean sat;

		private final PtxValueType type;

		private final PtxValueNode target;

		private final PtxValueNode left;

		private final PtxValueNode right;

		public PtxSubNode(boolean sat, PtxValueType type, PtxValueNode target, PtxValueNode left, PtxValueNode right) {
			this.sat = sat;
			this.type = type;
			this.target = target;
			this.left = left;
			this.right = right;
		}

	}

	public static class PtxMulNode extends PtxCmdNode {

		private final BitWidthType width;

		private final PtxValueType type;

		private final PtxValueNode target;

		private final PtxValueNode left;

		private final PtxValueNode right;

		public PtxMulNode(BitWidthType width, PtxValueType type, PtxValueNode target, PtxValueNode left, PtxValueNode right) {
			super();
			this.width = width;
			this.type = type;
			this.target = target;
			this.left = left;
			this.right = right;
		}

	}

	public static class PtxCvtNode extends PtxCmdNode {

		private final boolean irnd;

		private final boolean frnd;

		private final boolean ftz;

		private final boolean sat;

		private final PtxValueType leftType;

		private final PtxValueType rightType;

		private final PtxValueNode target;

		private final PtxValueNode value;

		public PtxCvtNode(boolean irnd, boolean frnd, boolean ftz, boolean sat, PtxValueType leftType, PtxValueType rightType,
				PtxValueNode target, PtxValueNode value) {
			this.irnd = irnd;
			this.frnd = frnd;
			this.ftz = ftz;
			this.sat = sat;
			this.leftType = leftType;
			this.rightType = rightType;
			this.target = target;
			this.value = value;
		}

	}

	public static class PtxLdNode extends PtxCmdNode {

		private final boolean vol;

		private final boolean isVec;

		private final Optional<StageSpaceType> stage;

		private final Optional<CacheOperationType> cache;

		private final PtxValueType type;

		private final PtxValueNode target;

		private final Optional<PtxValueNode> variable;

		public PtxLdNode(boolean vol, boolean vec, Optional<StageSpaceType> stage, Optional<CacheOperationType> cache,
				PtxValueType type, PtxValueNode target, Optional<PtxValueNode> variable) {
			this.vol = vol;
			this.isVec = vec;
			this.stage = stage;
			this.cache = cache;
			this.type = type;
			this.target = target;
			this.variable = variable;
		}

	}

	public static class PtxStNode extends PtxCmdNode {

		private final boolean vol;

		private final boolean vec;

		private final Optional<StageSpaceType> stage;

		private final Optional<CacheOperationType> cache;

		private final PtxValueType type;

		private final PtxValueNode target;

		private final PtxValueNode source;

		public PtxStNode(boolean vol, boolean vec, Optional<StageSpaceType> stage, Optional<CacheOperationType> cache,
				PtxValueType type, PtxValueNode target, PtxValueNode source) {
			this.vol = vol;
			this.vec = vec;
			this.stage = stage;
			this.cache = cache;
			this.type = type;
			this.target = target;
			this.source = source;
		}

	}

	public static class PtxExitNode extends PtxCmdNode {

	}

}

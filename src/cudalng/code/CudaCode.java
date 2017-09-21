package cudalng.code;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class CudaCode {

	private String name;

	private Set<Object> params;

	public CudaCode(String name, Object... params) {
		this.name = name;
		this.params = new HashSet<>(Arrays.asList(params));
	}

	public String getName() {
		return name;
	}

	protected CudaValueNode $(Object array, Object index) {
		return null;
	}

	protected CudaValueNode $(Object array) {
		return null;
	}

	protected CudaEndNode $def(String name, Object value) {
		return null;
	}

	protected CudaValueNode $tid() {
		return null;
	}

	public static class CudaNode implements CudaEndNode {

	}

	public static interface CudaEndNode {

		public default void $end() {
		}

	}

	public static class CudaValueNode extends CudaNode {

		public CudaValueNode $assign(Object value) {
			return null;
		}

		public CudaValueNode $sum(Object value) {
			return null;
		}

		public CudaValueNode $() {
			return null;
		}

		public CudaValueNode $val(Object value) {
			return null;
		}
	}

	public static class CudaCommandNode extends CudaNode {

	}

}

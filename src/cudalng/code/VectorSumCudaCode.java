package cudalng.code;

public class VectorSumCudaCode extends CudaCode {

	private static final String I = "i";

	public VectorSumCudaCode(int[] a, int[] b, int[] c) {
		super("sumVector", a, b, c);
		$def(I, $tid()).$end();
		$(c, I).$assign($(a, I).$sum($(b, I))).$end();
	}

}

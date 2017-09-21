package cudalng;

public class CudaNative {

	static {
		System.loadLibrary("jcuda"); 
	}

	public static native int cudaGetDeviceCount();
	
	public static void main(String[] args) {
		System.out.println(CudaNative.cudaGetDeviceCount());
	}

}

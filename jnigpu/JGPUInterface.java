package jnigpu;
import java.lang.*;
import java.util.*;
import java.io.*;

public class JGPUInterface {
	static {
		try {
	        System.load("/home/tomslavens/Programming/Java/JNIGPUInterface/libJGPUMatrixOperations.so");    // load libJGPUMatrixOperations.so
		} catch(Exception E) {
			System.out.println("Error loading library file!");
			System.out.println(E.toString());
		}
	}
	
    private native float[] JGPUAddVector(float[] a, float[] b, int size);
    private native float[][] JGPUAddMatrix(float[][] a, float[][] b);
    private native float[][] JGPUScalarMultMatrix(float[][] a, float s);
    private native float[][] JGPUMultiplyMatrix(float[][] a, float[][] b);
    
    public double[][] addMatrix(double[][] a, double[][] b) {
    	// first lets check the size of each of the matrices and see if they are equal
    	int arows = a.length;
    	int acols = a[0].length;
    	int brows = b.length;
    	int bcols = b[0].length;
    	
    	float[][] fc = new float[arows][acols];
    	if(arows == brows && acols == bcols) {
    		//all GPU calculations are done with single precision, so step 1 is convert to single precision
    		float[][] fa = new float[arows][acols];
    		float[][] fb = new float[brows][bcols];
    		for(int i=0;i<arows;i++) {
    			for(int j=0;j<acols;i++) {
    				fa[i][j] = (float) a[i][j];
    				fb[i][j] = (float) b[i][j];
    			}
    		}
    		fc = JGPUAddMatrix(fa,fb);    		
    	} else {
    		System.out.println("Matrices are not the same size!!!");
    	}
    	// convert fc to a double array
    	double[][] c = new double[arows][acols];
    	for(int i=0;i<arows;i++) {
			for(int j=0;j<acols;i++) {
				c[i][j] = fc[i][j];
			}
    	}
    	return c;
    }
    public double[][] multMatrix(double[][] a, double[][] b) {
    	// first lets check the size of each of the matrices and see if they are equal
    	int arows = a.length;
    	int acols = a[0].length;
    	int brows = b.length;
    	int bcols = b[0].length;
    	
    	double[][] c = null;
    	if(acols == brows) {
    		//all GPU calculations are done with single precision, so step 1 is convert to single precision
    		float[][] fa = new float[arows][acols];
    		for(int i=0;i<arows;i++) {
    			for(int j=0;j<acols;i++) {
    				fa[i][j] = (float) a[i][j];
    			}
    		}    		
    		float[][] fb = new float[brows][bcols];
    		for(int i=0;i<brows;i++) {
    			for(int j=0;j<bcols;i++) {
    				fb[i][j] = (float) b[i][j];
    			}
    		}
    		
    		float[][] fc = JGPUMultiplyMatrix(fa,fb);
    		// convert fc to a double array
        	c = new double[arows][bcols];
        	for(int i=0;i<arows;i++) {
    			for(int j=0;j<bcols;i++) {
    				c[i][j] = fc[i][j];
    			}
        	}
    	} else {
    		System.out.println("Matrices are not the same size!!!");
    	}
    	return c;
    }    
    
    private static void printVector(float[] v) {
    	String output = "=[";
    	int rows = v.length;
    	for(int i=0;i<rows;i++) {
   			output = output + Float.toString(v[i]) + ", ";
    	}
		output = output + "]\n";    	
    	System.out.println(output);
    }
    private static void printMatrix(float[][] m) {
    	String output = "=[";
    	int rows = m.length;
    	int cols = m[0].length;
    	for(int i=0;i<rows;i++) {
    		for(int j=0;j<cols;j++) {
    			output = output + Float.toString(m[i][j]) + ", ";
    		}
    		output = output + "\n";
    	}
    	output = output + "]\n";
    	System.out.println(output);
    }
    private static void writeMatrix(float[][] m, String fname) {
    	try {
    		BufferedWriter bw = new BufferedWriter(new FileWriter(fname));
    		int rows = m.length;
        	int cols = m[0].length;
        	for(int i=0;i<rows;i++) {
        		String output = " ";
        		for(int j=0;j<cols;j++) {
        			output = output + Float.toString(m[i][j]) + " ";
        		}
        		bw.write(output);
        		bw.newLine();
        		bw.flush();
        	}
        	bw.close();
    	} catch(Exception E) {
    		System.out.println(E.toString());
    	}
    }    

}

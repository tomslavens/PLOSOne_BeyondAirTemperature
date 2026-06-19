package linear_math;

import java.text.NumberFormat;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DecimalFormat;

public class Matrix {
	
    private int M;             // number of rows
    private int N;             // number of columns
    public double[][] data;   // M-by-N array

    // create M-by-N matrix of 0's
    public Matrix(int M, int N) {
        this.M = M;
        this.N = N;
        data = new double[M][N];
    }

    // create matrix based on 2d array
    public Matrix(double[][] data) {
        M = data.length;
        N = data[0].length;
        this.data = new double[M][N];
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                    this.data[i][j] = data[i][j];
    }

    // copy constructor
    private Matrix(Matrix A) { this(A.data); }
    
    //set data to value
    public void setValue(double val, int m, int n) {
    	data[m][n] = val;
    }
    
    //get data at index
    public double getValue(int m, int n) {
    	return data[m][n];
    }

    // create and return a random M-by-N matrix with values between 0 and 1
    public static Matrix random(int M, int N) {
        Matrix A = new Matrix(M, N);
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                A.data[i][j] = Math.random();
        return A;
    }

    // create and return the N-by-N identity matrix
    public static Matrix identity(int N) {
        Matrix I = new Matrix(N, N);
        for (int i = 0; i < N; i++)
            I.data[i][i] = 1;
        return I;
    }

    // swap rows i and j
    private void swap(int i, int j) {
        double[] temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

    // create and return the transpose of the invoking matrix
    public Matrix transpose() {
        Matrix A = new Matrix(N, M);
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                A.data[j][i] = this.data[i][j];
        return A;
    }

    // return C = A + B
    public Matrix plus(Matrix B) {
        Matrix A = this;
        if (B.M != A.M || B.N != A.N) throw new RuntimeException("Illegal matrix dimensions.");
        Matrix C = new Matrix(M, N);
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                C.data[i][j] = A.data[i][j] + B.data[i][j];
        return C;
    }


    // return C = A - B
    public Matrix minus(Matrix B) {
        Matrix A = this;
        if (B.M != A.M || B.N != A.N) throw new RuntimeException("Illegal matrix dimensions.");
        Matrix C = new Matrix(M, N);
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                C.data[i][j] = A.data[i][j] - B.data[i][j];
        return C;
    }

    // does A = B exactly?
    public boolean eq(Matrix B) {
        Matrix A = this;
        if (B.M != A.M || B.N != A.N) throw new RuntimeException("Illegal matrix dimensions.");
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                if (A.data[i][j] != B.data[i][j]) return false;
        return true;
    }

    // return C = A * B
    public Matrix times(Matrix B) {
        Matrix A = this;
        if (A.N != B.M) throw new RuntimeException("Illegal matrix dimensions.");
        Matrix C = new Matrix(A.M, B.N);
        for (int i = 0; i < C.M; i++)
            for (int j = 0; j < C.N; j++)
                for (int k = 0; k < A.N; k++)
                    C.data[i][j] += (A.data[i][k] * B.data[k][j]);
        return C;
    }
    // return scalar multiply A = this*s
    public Matrix times(double s) {
        Matrix A = new Matrix(this);
        for (int i = 0; i < A.M; i++)
            for (int j = 0; j < A.N; j++)
                for (int k = 0; k < A.N; k++)
                    A.data[i][j] = A.data[i][j]*s;
        return A;
    }    


    // return x = A^-1 b, assuming A is square and has full rank
    public Matrix solve(Matrix rhs) {
        if (M != N || rhs.M != N || rhs.N != 1)
            throw new RuntimeException("Illegal matrix dimensions.");

        // create copies of the data
        Matrix A = new Matrix(this);
        Matrix b = new Matrix(rhs);

        // Gaussian elimination with partial pivoting
        for (int i = 0; i < N; i++) {

            // find pivot row and swap
            int max = i;
            for (int j = i + 1; j < N; j++)
                if (Math.abs(A.data[j][i]) > Math.abs(A.data[max][i]))
                    max = j;
            A.swap(i, max);
            b.swap(i, max);

            // singular
            if (A.data[i][i] == 0.0) throw new RuntimeException("Matrix is singular.");

            // pivot within b
            for (int j = i + 1; j < N; j++)
                b.data[j][0] -= b.data[i][0] * A.data[j][i] / A.data[i][i];

            // pivot within A
            for (int j = i + 1; j < N; j++) {
                double m = A.data[j][i] / A.data[i][i];
                for (int k = i+1; k < N; k++) {
                    A.data[j][k] -= A.data[i][k] * m;
                }
                A.data[j][i] = 0.0;
            }
        }

        // back substitution
        Matrix x = new Matrix(N, 1);
        for (int j = N - 1; j >= 0; j--) {
            double t = 0.0;
            for (int k = j + 1; k < N; k++)
                t += A.data[j][k] * x.data[k][0];
            x.data[j][0] = (b.data[j][0] - t) / A.data[j][j];
        }
        return x;
    }
    // return x = A^-1 b, assuming A is square and has full rank
    public Matrix inverse() {
        // create copies of the data
        Matrix A = new Matrix(this);
        Matrix I = Matrix.identity(N);

        // Gaussian elimination with partial pivoting for upper matrix U
        double m = 0.0;        
        for (int i = 0; i < N; i++) {

            // find pivot row and swap
            /*int max = i;
            for (int j = i + 1; j < N; j++)
                if (Math.abs(A.data[j][i]) > Math.abs(A.data[max][i]))
                    max = j;
            A.swap(i, max);
            I.swap(i, max);*/

            // singular
            if (A.data[i][i] == 0.0) throw new RuntimeException("Matrix is singular.");

            // pivot within A
            for (int j = i + 1; j < N; j++) {
                m = A.data[j][i] / A.data[i][i];
                for (int k = i+1; k < N; k++) {
                    A.data[j][k] = A.data[j][k] - A.data[i][k] * m;
                }
                A.data[j][i] = 0.0;
                for(int k=0;k<N;k++) {
                    I.data[j][k] = I.data[j][k] - I.data[i][k] * m;
                }
            }
        }
        
        // divide the diagonals
        m = 0.0;
        for (int i=0; i<N; i++) {
        	m = A.data[i][i];
        	for(int j=0;j<N;j++) {
        		I.data[i][j] = I.data[i][j]/m;        		
        		A.data[i][j] = A.data[i][j]/m;
        	}
        }   
        
        // back substitution; from bottom row up
        for(int i=N-1;i>0;i--) {
        	for(int j=i-1;j>=0;j--) {
            	m = A.data[j][i];
        		for(int k=0;k<N;k++) {
        			I.data[j][k] = I.data[j][k] - I.data[i][k]*m;
        		}
        	}
        }
        return I;
    }
    // returns LU decomposition of this matrix
    public Matrix lu() {
        // create copies of the data
        Matrix LU = new Matrix(this);

        // Gaussian elimination with partial pivoting for upper matrix U
        for (int i = 0; i < N; i++) {

            // find pivot row and swap
            int max = i;
            for (int j = i + 1; j < N; j++)
                if (Math.abs(LU.data[j][i]) > Math.abs(LU.data[max][i]))
                    max = j;
            LU.swap(i, max);  

            // singular
            if (LU.data[i][i] == 0.0) throw new RuntimeException("Matrix is singular.");

            // pivot within A
            for (int j = i + 1; j < N; j++) {
                double m = LU.data[j][i] / LU.data[i][i];
                for (int k = i+1; k < N; k++) {
                    LU.data[j][k] = LU.data[j][k] - LU.data[i][k] * m;
                }
                LU.data[j][i] = 0.0;
                LU.data[j][i] = m;
            }
        }
        LU.show();
        System.out.println("--------");

        return LU;
    }
    
    /**
     * Performs 2D convolution on an input image with a given kernel.
     *
     * @param input The 2D double array representing the input image.
     * @param kernel The 2D double array representing the convolution kernel.
     * @return A new 2D double array representing the convolved output.
     * 
     * AI GENERATED!!! I think it works?
     */
    public static double[][] convolve(double[][] input, double[][] kernel) {
        int inputHeight = input.length;
        int inputWidth = input[0].length;
        int kernelHeight = kernel.length;
        int kernelWidth = kernel[0].length;

        // Calculate output dimensions (assuming 'valid' convolution - no padding)
        int outputHeight = inputHeight - kernelHeight + 1;
        int outputWidth = inputWidth - kernelWidth + 1;

        if (outputHeight <= 0 || outputWidth <= 0) {
            throw new IllegalArgumentException("Kernel is too large for the input image.");
        }

        double[][] output = new double[outputHeight][outputWidth];

        // Iterate through each pixel in the output image
        for (int i = 0; i < outputHeight; i++) {
            for (int j = 0; j < outputWidth; j++) {
                // Perform the convolution for the current output pixel
                output[i][j] = singlePixelConvolution(input, i, j, kernel, kernelWidth, kernelHeight);
            }
        }
        return output;
    }

    /**
     * Performs convolution for a single pixel in the output image.
     *
     * @param input The 2D double array representing the input image.
     * @param x The row index of the current output pixel.
     * @param y The column index of the current output pixel.
     * @param kernel The 2D double array representing the convolution kernel.
     * @param kernelWidth The width of the kernel.
     * @param kernelHeight The height of the kernel.
     * @return The convolved value for the single pixel.
     */
    private static double singlePixelConvolution(double[][] input, int x, int y,
                                                 double[][] kernel, int kernelWidth, int kernelHeight) {
        double sum = 0.0;
        for (int i = 0; i < kernelHeight; i++) {
            for (int j = 0; j < kernelWidth; j++) {
                // Multiply corresponding elements and add to sum
                sum += input[x + i][y + j] * kernel[i][j];
            }
        }
        return sum;
    }

    // print matrix to standard output
    public void show() {
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                System.out.printf("%9.4f ", data[i][j]);
            }
            System.out.println();
        }
    }

    // print matrix to standard output
    public String toString() {
        String output = "[";
        NumberFormat formatter = new DecimalFormat("0.0000");
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                output = output + formatter.format(data[i][j]) + " ";
            }
            output = output + "\n";
        }
        output = output + "]";
        return output;
    }
    public static void writeMatrix(double[][] m, String fname) {
    	try {
    		BufferedWriter bw = new BufferedWriter(new FileWriter(fname));
    		int rows = m.length;
        	int cols = m[0].length;
        	for(int i=0;i<rows;i++) {
        		String output = " ";
        		for(int j=0;j<cols;j++) {
        			output = output + Double.toString(m[i][j]) + " ";
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


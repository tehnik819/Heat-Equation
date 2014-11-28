package schemes;

import data.InitialData;

public abstract class Scheme {

    protected InitialData data;
    protected double[][] res;
    protected int Nt;
    protected int Nx;
    protected boolean isCalcResidual = false;
    protected double residual;

    public Scheme(InitialData initialData) {
        this.data = initialData;
        Nt = (int)((data.t_r - data.t_l)/data.tau);
        Nx = (int)((data.x_r - data.x_l)/data.h);
    }

    public abstract double[][] calculate();

    public double getNormOfResidual() {
        double T = 0;
        double X = 0;
        double x;
        double t;
        long timeout = System.currentTimeMillis();
        if(!isCalcResidual) {
            double residual = 0;
            for (int i = 0; i <= Nt; i++) {
                t = data.t_l + i * data.tau;
                for (int j = 0; j <= Nx; j++) {
                    x = data.x_l + j * data.h;
                    double sub = Math.abs(exactSolution(t, x) - res[i][j]); //i,j
                    if (residual < sub) {
                        residual = sub;
                        T = t;
                        X = x;
                    }
                }
            }
            System.out.println("Максимальная ошибка в точке t = " + T + "; x = " + X);
            this.residual = residual;
            isCalcResidual = true;
        }
        System.out.println("Время подсчета ошибки = " + (System.currentTimeMillis() - timeout));
        return residual;
    }

    public static double exactSolution(double t, double x) {
        return (-x*x*x*x + x*x + t*x + t*t - t*Math.exp(x));
    }

    public double[][] getResult() {
        return res;
    }

    public int getNx() {
        return Nx;
    }

    public int getNt() {
        return Nt;
    }

    public static double[] tridiagonalMatrixAlgorithm(double[][] matrix, double[] b) {
        int dim = matrix.length;
        double[] x = new double[dim];
        double[][] koef = new double[2][dim - 1];
        //Forward
        koef[0][0] = -matrix[0][1]/matrix[0][0]; //Альфа нулевое
        koef[1][0] = b[0]/matrix[0][0]; //Бэта нулевое
        for(int i = 1;i < dim - 1;i++)
        {
            koef[0][i] = -matrix[i][i + 1]/(matrix[i][i] + koef[0][i - 1]*matrix[i][i - 1]);
            koef[1][i] = (b[i] - koef[1][i - 1]*matrix[i][i - 1])/(matrix[i][i] + koef[0][i - 1]*matrix[i][i - 1]);
        }
        //Backward
        x[dim - 1] = (b[dim - 1] - koef[1][dim - 2]*matrix[dim - 1][dim - 2])/(matrix[dim - 1][dim - 1] + koef[0][dim - 2]*matrix[dim - 1][dim - 2]);
        for(int i = dim - 2;i >= 0;i--) {
            x[i] = koef[0][i] * x[i + 1] + koef[1][i];
        }
        return x;
    }

    public static void print(double[][] matrix) {
        for(int i = 0; i < matrix.length;i++) {
            for(int j = 0;j < matrix[i].length;j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void print(double[] vector) {
        for(int i = 0; i < vector.length;i++) {
            System.out.print(vector[i] + " ");
        }
        System.out.println();
    }

    public static double[][] transposeMatrix(double[][] matrix) {
        if(matrix.length == 0) {
            throw new IllegalArgumentException("Empty array");
        }
        int rowLength = matrix[0].length;
        for (double[] ai:matrix) {
            if (rowLength != ai.length) {
                throw new IllegalArgumentException("Non-equal rows");
            }
        }

        double [][] tMatrix = new double[rowLength][];
        for (int i = 0; i < rowLength; i++) {
            tMatrix[i] = new double[matrix.length];
        }
        for (int i = 0; i < matrix.length; i++) {
            double[] tArr = matrix[i];
            for (int j = 0; j < rowLength; j++) {
                tMatrix[j][i] = tArr[j];
            }
        }
        return tMatrix;
    }
}

package schemes;


import data.InitialData;

public class ImplicitScheme extends Scheme {

    public ImplicitScheme(InitialData initialData) {
        super(initialData);
    }

    @Override
    public double[][] calculate() {
        //Засекаем время
        long timeout = System.currentTimeMillis();
        System.out.println("Nt = " + Nt);
        System.out.println("Nx = " + Nx);
        double[][] result = new double[Nt + 1][Nx + 1]; ///////////
        //Заполняем граничные условия
        for(int i = 0; i <= Nt;i++) {              //////////////
            double t = data.t_l + i*data.tau;
            result[i][0] = data.mu1(t);
        }
        for(int i = 0; i <= Nt;i++) {                 ///////////////
            double t = data.t_l + i*data.tau;
            result[i][Nx] = data.mu2(t);
        }
        for(int i = 0; i <= Nx;i++) {                  /////////////////
            double x = data.x_l + i*data.h;
            result[0][i] = data.mu(x);
        }
        //Матрица разностной схемы
        double[][] matrix = new double[Nx + 1][Nx + 1];
        matrix[0][0] = matrix[Nx][Nx] = 1.0;
        matrix[1][0] = matrix[Nx - 1][Nx] = -data.a*data.tau/(data.h*data.h);
        for(int i = 1;i < Nx;i++) {
            matrix[i][i] = 1.0 + 2*data.a*data.tau/(data.h*data.h);
            if(i < Nx) {
                matrix[i][i + 1] = -data.a*data.tau/(data.h*data.h);
            }
            if(i > 0) {
                matrix[i][i - 1] = -data.a*data.tau/(data.h*data.h);
            }
        }
        //Решение систем линейных уравнений
        double[] b = new double[Nx + 1];
        for(int i = 0;i < Nt;i++) {
            b[0] = data.mu1(data.t_l + data.tau*(i + 1)); //Tau
            b[Nx] = data.mu2(data.t_l + data.tau*(i + 1)); //Tau
            for(int j = 1;j < Nx;j++) {
                b[j] = result[i][j] + data.tau*data.f(data.x_l + data.h*j, data.t_l + data.tau*(i + 1));
            }
            double[] tmp = tridiagonalMatrixAlgorithm(matrix,b);
            for(int j = 1;j < Nx;j++) {
                result[i + 1][j] = tmp[j];
            }
        }
        long doingTime = System.currentTimeMillis() - timeout;
        System.out.println("Время выполнения метода в миллисекундах = " + doingTime);
        System.out.println("Время выполнения метода в секундах = " + doingTime/1000L);
        res = result;
        return result;
    }
}

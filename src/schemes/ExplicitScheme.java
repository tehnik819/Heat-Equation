package schemes;

import data.InitialData;
import schemes.Scheme;

public class ExplicitScheme extends Scheme {

    public ExplicitScheme(InitialData initialData) {
        super(initialData);
    }

    @Override
    public double[][] calculate() {
        //Засекаем время
        long timeout = System.currentTimeMillis();
        System.out.println("Nt = " + Nt);
        System.out.println("Nx = " + Nx);
        double [][] result = new double[Nt + 1][Nx + 1]; ///////////
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
        //Считаем по разностной схеме
        for(int j = 0; j <= Nt - 1;j++) { ////////////////////////////
            for(int k = 1;k <= Nx - 1;k++) {    //////////////////////
                result[j + 1][k] = data.a*data.tau/(data.h*data.h)*result[j][k + 1] + (1 - 2*data.a*data.tau/(data.h*data.h))*result[j][k]
                        + data.a*data.tau/(data.h*data.h)*result[j][k - 1] + data.tau*data.f(data.x_l + k*data.h, data.t_l + j*data.tau);
            }
        }
        long doingTime = System.currentTimeMillis() - timeout;
        System.out.println("Время выполнения метода в миллисекундах = " + doingTime);
        System.out.println("Время выполнения метода в секундах = " + doingTime/1000L);
        res = result;
        return result;
    }
}

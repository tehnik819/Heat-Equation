import data.InitialData;
import schemes.*;
import utils.ChartWindow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static final int MAX = 1000;

    public static void main(String[] args) {
        long heapsize=Runtime.getRuntime().totalMemory();
        System.out.println("heapsize is::"+heapsize);
        File input = new File("input.txt");
        List<String> list;
        try {
            Scanner in = new Scanner(input);
            list = new ArrayList<String>();
            while (in.hasNextLine()) {
                list.add(in.nextLine());
            }
            for(int i = 0;i < list.size();i++) {
                System.out.println(list.get(i));
            }
            double[] d = new double[7];
            for(int i = 0;i < 5;i++) {
                d[i] = Double.valueOf(list.get(i));
            }
            d[5] = Double.valueOf(list.get(9));
            d[6] = Double.valueOf(list.get(10));
            InitialData initialData = new InitialData(d[0], d[1], d[2], d[3], d[4], list.get(5),list.get(6),list.get(7), list.get(8), d[5], d[6]);
            //ExplicitScheme scheme = new schemes.ExplicitScheme(initialData);
            //ImplicitScheme scheme = new ImplicitScheme(initialData);
            //KrankNikolsonScheme scheme = new KrankNikolsonScheme(initialData);
            HighOrderScheme scheme = new HighOrderScheme(initialData);
            double res[][] = scheme.calculate();
            System.out.println("Норма ошибки = " + scheme.getNormOfResidual());
            System.out.println("Пишем точки в файл");
            writeSurface(initialData, res);
            ChartWindow window = new ChartWindow(Scheme.transposeMatrix(res), initialData);
            window.show();
        } catch (FileNotFoundException e) {
            e.getMessage();
        }
    }

    private static void writeByTime(InitialData d, double[][] res) {
        try {
            PrintStream out = new PrintStream(new File("output.txt"));
            int Nx = (int)((d.x_r - d.x_l)/d.h);
            int Nt = (int)((d.t_r - d.t_l)/d.tau);
            for(int j = 0;j <= Nt;j++) {           //////////////////////
                double t = d.t_l + j*d.tau;
                out.println("t = " + t);
                for (int i = 0; i <= Nx; i++) {      ////////////////////
                    out.print("{" + (d.x_l + i * d.h) + "," + res[j][i] + "},");
                }
                out.println();
            }
        } catch (FileNotFoundException e) {
            e.getMessage();
        }
    }

    private static void writeSurface(InitialData d, double[][] res) {
        int Nx = (int)((d.x_r - d.x_l)/d.h);
        int Nt = (int)((d.t_r - d.t_l)/d.tau);
        try {
            PrintStream out = new PrintStream(new File("output.txt"));
            out.print("Show[{Plot3D[-x^4 + x^2 + t*x + t^2 - t*Exp[x], {t," + d.t_l + "," + d.t_r + "},{x," + d.x_l + "," + d.x_r + "}, PlotStyle -> Red]},{ListPlot3D[{");
            int k = 1;
            if(Nx*Nt > MAX) {
                k = (int)Math.ceil(Math.sqrt(Nx*Nt/MAX));
            }
            for(int j = 0; j <= Nt;j += k) {
                for (int i = 0; i <= Nx; i += k) {
                    out.print("{" + String.format("%.5f", (d.t_l + j * d.tau)).replace(',', '.') + " , " + String.format("%.5f", (d.x_l + i * d.h)).replace(',', '.') + " , " + String.format("%.5f", res[j][i]).replace(',','.') + "}");
                    if((j != Nt || i != Nx) || (j + k) < Nt || (i + k) < Nx) {
                        out.print(",");
                    }
                }
            }
            out.print("}, PlotStyle -> Green]}]");
        } catch (FileNotFoundException e) {
            e.getMessage();
        }
    }

    private static void writeGNUPLOTsurface(InitialData d, double[][] res) {
        int Nx = (int)((d.x_r - d.x_l)/d.h);
        int Nt = (int)((d.t_r - d.t_l)/d.tau);
        try {
            PrintStream out = new PrintStream(new File("output.txt"));
            for(int j = 0; j <= Nt;j++) {
                for (int i = 0; i <= Nx; i++) {
                    out.println((d.t_l + j*d.tau) + " " + (d.x_l + i*d.h) + " " + res[j][i]);
                }
                out.println();
            }
        } catch (FileNotFoundException e) {
            e.getMessage();
        }
    }
}

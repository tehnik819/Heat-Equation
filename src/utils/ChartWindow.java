package utils;

import data.InitialData;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import schemes.ExplicitScheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;

public class ChartWindow {

    private final JFrame frame;
    private JFreeChart chart;
    private ChartPanel chartPanel;
    private XYPlot plot;

    private double[][] result;
    private InitialData data;
    private int currentPosition = 0;

    public ChartWindow(double[][] result, InitialData d) {
        this.result = result;
        this.data = d;
        frame = new JFrame("Chart for laboratory task");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if(inFrame()) {
                    if(!e.isConsumed()) {
                        e.consume();
                    }
                    formMouseWheel(e);
                }
            }

            private boolean inFrame() {
                Point p = frame.getMousePosition();
                if(p == null) {
                    return false;
                }
                double mouse_position_x = frame.getMousePosition().getX();
                double mouse_position_y = frame.getMousePosition().getY();
                if(frame.getBounds().contains(mouse_position_x,mouse_position_y)) {
                    return true;
                }
                return false;
            }
        });
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        // Помещаем график на фрейм
        chartPanel = new ChartPanel(chart);
        frame.getContentPane().add(chartPanel);
        frame.setVisible(true);
    }

    public void show() {
        showGraph(0);
    }

    private void initPlot(){
        plot.setDomainPannable(true);
        plot.setRangePannable(true);

        //Axis in the middle
        plot.setDomainZeroBaselineVisible(true);
        plot.setRangeZeroBaselineVisible(true);

        plot.getDomainAxis().setRange(data.t_l,data.t_r);
        double max = 0;
        double min = 0;
        for(int i = 0; i < result.length;i++) {
            for(int j = 0;j < result[i].length;j++) {
                if(result[i][j] > max) {
                    max = result[i][j];
                }
                if(result[i][j] < min) {
                    min = result[i][j];
                }
            }
        }
        plot.getRangeAxis().setRange(min,max);
    }

    private void createChart(double[] dots, int nx) {
        /*
        Expression e_sin = new ExpressionBuilder("sin(x)")
                .variable("x")
                .build();
        Expression e_cos = new ExpressionBuilder("cos(x^2)")
                .variable("x")
                .build();

        XYSeries series1 = new XYSeries("sin(x)");
        XYSeries series2 = new XYSeries("cos(x^2)");

        for(double i =  - Math.PI; i <= Math.PI; i+=0.01){
            e_sin.setVariable("x", i);
            e_cos.setVariable("x", i);
            series1.add(i, e_sin.evaluate());
            series2.add(i, e_cos.evaluate());
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);

        chart = ChartFactory
                .createXYLineChart("y = sin(x) & y = cos(x^2)", "X", "Y",
                        dataset,
                        PlotOrientation.VERTICAL,
                        true, true, false);
        */
        XYSeries series1 = new XYSeries("Результат решения разностной задачи");
        for(int i =  0; i < dots.length; i++){
            series1.add(data.t_l + i*data.tau, dots[i]);
        }
        XYSeries series2 = new XYSeries("Точное решение");
        for(int i = 0;i < dots.length;i++) {
            double x = data.x_l + nx*data.h;
            double t = data.t_l + i*data.tau;
            series2.add(t, ExplicitScheme.exactSolution(t, x));
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        chart = ChartFactory
                .createXYLineChart("График", "t", "U",
                        dataset,
                        PlotOrientation.VERTICAL,
                        true, true, false);
        plot = chart.getXYPlot();
        initPlot();
    }

    private void showGraph(int n) {
        int Nt = (int)((data.t_r - data.t_l)/data.tau);
        int Nx = (int)((data.x_r - data.x_l)/data.h);
        if(n >= 0 && n < Nx + 1) {
            frame.getContentPane().remove(chartPanel);
            double[] res = new double[Nt + 1];
            for(int i = 0; i < Nt + 1;i++) {
                res[i] = result[n][i];
            }
            createChart(res, n);
            chartPanel = new ChartPanel(chart);
            frame.getContentPane().add(chartPanel);
            frame.getContentPane().validate();
        }
    }

    private void formMouseWheel(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        ChartPanel chartPanel = new ChartPanel(chart);
        Rectangle2D rectangle2D = chartPanel.getChartRenderingInfo().getPlotInfo().getDataArea();
        double centerX = rectangle2D.getCenterX();
        double centerY = rectangle2D.getCenterY();
        if(notches < 0) {
            chartPanel.zoomInBoth(centerX, centerY);
        }
        else if(notches > 0) {
            chartPanel.zoomOutBoth(centerX, centerY);
        }
    }

    private void formKeyPressed(KeyEvent evt) {
        switch (evt.getKeyCode()) {

            case KeyEvent.VK_RIGHT:
                System.out.println("Left button pressed");
                if(currentPosition < result.length - 1) {
                    currentPosition++;
                }
                showGraph(currentPosition);
                break;

            case KeyEvent.VK_LEFT:
                System.out.println("Right button pressed");
                if(currentPosition > 0) {
                    currentPosition--;
                }
                showGraph(currentPosition);
                break;

            default:
        }
    }

}

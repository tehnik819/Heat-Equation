package data;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.interfaces.IExpr;

public class InitialData {
    public final double a;
    public final double x_l;
    public final double x_r;
    public final double t_l;
    public final double t_r;
    public final double h;
    public final double tau;

    private Expression f;
    private Expression d2fwx;
    private Expression mu;
    private Expression mu1;
    private Expression mu2;

    public InitialData(double a, double x_l, double x_r, double t_l, double t_r, String f, String mu, String mu1, String mu2,
                       double h, double tau) {
        this.a = a;
        this.x_l = x_l;
        this.x_r = x_r;
        this.t_l = t_l;
        this.t_r = t_r;
        EvalUtilities util = new EvalUtilities(false, true);
        IExpr result = util.evaluate("diff(diff(" + f + ",x),x)");
        this.d2fwx = new ExpressionBuilder(result.toString().replace("E^x", "exp(x)")).variables("x","t","a").build();
        this.f = new ExpressionBuilder(f).variables("x","t","a").build();
        this.mu = new ExpressionBuilder(mu).variable("x").build();
        this.mu1 = new ExpressionBuilder(mu1).variable("t").build();
        this.mu2 = new ExpressionBuilder(mu2).variable("t").variable("z").build();
        this.h = h;
        this.tau = tau;
    }

    public double f(double x, double t) {
        return f.setVariable("x", x).setVariable("t",t).setVariable("a",a).evaluate();
    }

    public double d2fWithRespectX(double x, double t) {
        return d2fwx.setVariable("x", x).setVariable("t",t).setVariable("a",a).evaluate();
    }

    public double mu(double x) {
        return mu.setVariable("x",x).evaluate();
    }

    public double mu1(double t) {
        return mu1.setVariable("t",t).evaluate();
    }

    public double mu2(double t) {
        return mu2.setVariable("t",t).setVariable("z", 1).evaluate();
    }
}

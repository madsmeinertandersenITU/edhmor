package modules.individual;

public class CPG {
    // CPG state variables
    private double x, y;
    // CPG parameters
    private final double alpha, mu, omega;

    public CPG(double alpha, double mu, double omega) {
        this.alpha = alpha;
        this.mu = mu;
        this.omega = omega;
        this.x = 1.0; // Initial condition
        this.y = 0.0; // Initial condition
    }

    // The system of differential equations for the CPG model
    private double[] cpgDynamics(double x, double y) {
        double rSquared = x * x + y * y;
        return new double[] {
                alpha * (mu - rSquared) * x - omega * y,
                alpha * (mu - rSquared) * y + omega * x
        };
    }

    // Runge-Kutta 4th order method to update the CPG state
    public void update(double dt) {
        double[] k1 = cpgDynamics(x, y);
        double[] k2 = cpgDynamics(x + dt / 2.0 * k1[0], y + dt / 2.0 * k1[1]);
        double[] k3 = cpgDynamics(x + dt / 2.0 * k2[0], y + dt / 2.0 * k2[1]);
        double[] k4 = cpgDynamics(x + dt * k3[0], y + dt * k3[1]);

        x += dt / 6.0 * (k1[0] + 2 * k2[0] + 2 * k3[0] + k4[0]);
        y += dt / 6.0 * (k1[1] + 2 * k2[1] + 2 * k3[1] + k4[1]);
    }

    // Get the current state of the CPG as the joint position
    public double getJointPosition() {
        return x; // or some function of x and y depending on your model
    }
}

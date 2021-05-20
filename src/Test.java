
import java.util.Random;

class Test {
    public boolean performSingleEstimate() {
        int rand1;
        int rand2;
        boolean in_circle;
        int squareDist;

        rand1 = (new Random()).nextInt(200) - 100;
        rand2 = (new Random()).nextInt(200) - 100;

        squareDist = (rand1 * rand1 + rand2 * rand2) / 100;
        in_circle = squareDist < 100;

        return in_circle;
    }

    public int estimatePi100(int n) {
        int samples_in_circle;
        int samples_so_far;
        int pi_estimate;

        samples_so_far = 0;
        samples_in_circle = 0;

        while (samples_so_far < n) {
            if (this.performSingleEstimate()) {
                samples_in_circle = samples_in_circle + 1;
            }
            samples_so_far = samples_so_far + 1;
        }

        pi_estimate = 400 * samples_in_circle / n;
        return pi_estimate;
    }

    public static void main(String[] args) {
        int pi_estimate_times_100;
        int num_samples;

        num_samples = 100;
        pi_estimate_times_100 = new Test().estimatePi100(num_samples);

        System.out.println(pi_estimate_times_100);
    }
}

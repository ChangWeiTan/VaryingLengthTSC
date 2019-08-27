/* Copyright (C) 2019 Chang Wei Tan, Francois Petitjean, Geoff Webb
 This file is part of Varying length TSC.
 Varying length TSC is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, version 3 of the License.
 Varying length TSC is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 You should have received a copy of the GNU General Public License
 along with Varying length TSC.  If not, see <http://www.gnu.org/licenses/>. */
package distances;

import data.Sequence;

/**
 * This is a class to compute Uniform Scaling distance with Euclidean
 *
 * @author Chang Wei
 */
public class UniformScalingEuclidean {
    private Euclidean distComputer = new Euclidean();
    public double[] scaledSeries;

    public double distance(final Sequence a, final Sequence b) {
        final int n = a.length(); // shorter
        final int m = b.length(); // longer

        if (n > m)
            return distance(b, a);

        final double[] bSeries = b.getData();
        double bsfDistance = Double.POSITIVE_INFINITY;
        int start = n;
        if (n == m) {
            start = (int) (0.7 * n);
        }

        for (int p = start; p <= m; p++) {
            double[] firstSeries = new double[p];

            for (int j = 0; j < p; j++) {
                final int scalingFactor = (int) (1.0 * j * n / p);
                firstSeries[j] = a.value(scalingFactor);
            }
            final double dist = distComputer.distance(firstSeries, bSeries, bsfDistance);
            if (dist < bsfDistance) {
                bsfDistance = dist;
                scaledSeries = firstSeries;
            }
        }

        return bsfDistance;
    }

    public double distance(final Sequence a, final Sequence b, final double cutOffValue) {
        final int n = a.length(); // shorter
        final int m = b.length(); // longer

        if (n > m)
            return distance(b, a, cutOffValue);

        final double[] bSeries = b.getData();
        double bsfDistance = cutOffValue;

        int start = n;
        if (n == m) {
            start = (int) (0.7 * n);
        }

        for (int p = start; p <= m; p++) {
            double[] firstSeries = new double[p];

            for (int j = 0; j < p; j++) {
                final int scalingFactor = (int) (1.0 * j * n / p);
                firstSeries[j] = a.value(scalingFactor);
            }
            final double dist = distComputer.distance(firstSeries, bSeries, bsfDistance);
            if (dist < bsfDistance) {
                bsfDistance = dist;
            }
        }

        return bsfDistance;
    }

}

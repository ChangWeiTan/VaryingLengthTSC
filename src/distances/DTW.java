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
import data.Sequences;
import utilities.DataLoader;
import utilities.GenericTools;

/**
 * This is a class to compute DTW distance
 *
 * @author Chang Wei
 */
public class DTW {
    private final static int MAX_SEQ_LENGTH = 4000;         // maximum sequence length possible
    private final static double[][] matrixD = new double[MAX_SEQ_LENGTH][MAX_SEQ_LENGTH];
    private final static int[][] pathD = new int[MAX_SEQ_LENGTH][MAX_SEQ_LENGTH];

    public static void main(String[] args) {
        System.out.println("[DTW] Test begins");
        testDistance();
    }

    private static void testDistance() {
        final DataLoader dataLoader = new DataLoader();
        final DTW distComputer = new DTW();
        final String problem = "ArrowHead";
        final Sequences train = dataLoader.loadTrainPadNorm(problem);
        final Sequences test = dataLoader.loadTestPadNorm(problem);
        final int window = train.minLength();

        System.out.println(String.format("[DTW] Loading %s problem", problem));

        for (int i = 0; i < test.size(); i++) {
            final Sequence query = test.get(i);
            for (int j = 0; j < train.size(); j++) {
                final Sequence candidate = train.get(j);
                final double dist = distComputer.distance(query, candidate, window);
                System.out.println(String.format("[DTW] %d---%d ----> DTW: %.3f", i, j, dist));
            }
        }
    }

    public double distance(final Sequence first, final Sequence second) {
        final int n = first.length();
        final int m = second.length();

        double diff;
        int i, j;

        diff = first.value(0) - second.value(0);
        matrixD[0][0] = diff * diff;
        for (i = 1; i < n; i++) {
            diff = first.value(i) - second.value(0);
            matrixD[i][0] = matrixD[i - 1][0] + diff * diff;
            pathD[i][0] = 1;
        }

        for (j = 1; j < m; j++) {
            diff = first.value(0) - second.value(j);
            matrixD[0][j] = matrixD[0][j - 1] + diff * diff;
            pathD[0][j] = 2;
        }

        for (i = 1; i < n; i++) {
            for (j = 1; j < m; j++) {
                diff = first.value(i) - second.value(j);
                double minRes = matrixD[i - 1][j - 1];
                if (matrixD[i - 1][j] < minRes) {
                    minRes = matrixD[i - 1][j];
                    pathD[i][j] = 1;
                }

                if (matrixD[i][j - 1] < minRes) {
                    minRes = matrixD[i][j - 1];
                    pathD[i][j] = 2;
                }

                matrixD[i][j] = GenericTools.min3(matrixD[i - 1][j - 1], matrixD[i][j - 1], matrixD[i - 1][j]) + diff * diff;
            }
        }

        return matrixD[n - 1][m - 1];
    }

    public double distance(final Sequence first, final Sequence second, final int windowSize) {
        final int n = first.length();
        final int m = second.length();

        final int winPlus1 = windowSize + 1;
        double diff;
        int i, j, jStart, jEnd, indexInfyLeft;

        diff = first.value(0) - second.value(0);
        matrixD[0][0] = diff * diff;
        for (i = 1; i < Math.min(n, winPlus1); i++) {
            diff = first.value(i) - second.value(0);
            matrixD[i][0] = matrixD[i - 1][0] + diff * diff;
        }

        for (j = 1; j < Math.min(m, winPlus1); j++) {
            diff = first.value(0) - second.value(j);
            matrixD[0][j] = matrixD[0][j - 1] + diff * diff;
        }
        if (j < m)
            matrixD[0][j] = Double.POSITIVE_INFINITY;

        for (i = 1; i < n; i++) {
            jStart = Math.max(1, i - windowSize);
            jEnd = Math.min(m, i + winPlus1);
            indexInfyLeft = i - windowSize - 1;
            if (indexInfyLeft >= 0)
                matrixD[i][indexInfyLeft] = Double.POSITIVE_INFINITY;

            for (j = jStart; j < jEnd; j++) {
                diff = first.value(i) - second.value(j);
                matrixD[i][j] = GenericTools.min3(matrixD[i - 1][j - 1], matrixD[i][j - 1], matrixD[i - 1][j]) + diff * diff;
            }
            if (j < m)
                matrixD[i][j] = Double.POSITIVE_INFINITY;
        }

        return matrixD[n - 1][m - 1];
    }

    public double distance(final Sequence first, final Sequence second, final double cutOffValue) {
        boolean tooBig;
        final int n = first.length();
        final int m = second.length();

        double diff;
        int i, j;

        diff = first.value(0) - second.value(0);
        matrixD[0][0] = diff * diff;
        for (i = 1; i < n; i++) {
            diff = first.value(i) - second.value(0);
            matrixD[i][0] = matrixD[i - 1][0] + diff * diff;
        }

        for (j = 1; j < m; j++) {
            diff = first.value(0) - second.value(j);
            matrixD[0][j] = matrixD[0][j - 1] + diff * diff;
        }

        for (i = 1; i < n; i++) {
            tooBig = true;

            for (j = 1; j < m; j++) {
                diff = first.value(i) - second.value(j);
                matrixD[i][j] = GenericTools.min3(matrixD[i - 1][j - 1], matrixD[i][j - 1], matrixD[i - 1][j]) + diff * diff;
                if (tooBig && matrixD[i][j] < cutOffValue)
                    tooBig = false;
            }
            //Early abandon
            if (tooBig)
                return Double.POSITIVE_INFINITY;
        }

        return matrixD[n - 1][m - 1];
    }

    public double distance(final double[] first, final double[] second, final double cutOffValue) {
        boolean tooBig;
        final int n = first.length;
        final int m = second.length;

        double diff;
        int i, j;

        diff = first[0] - second[0];
        matrixD[0][0] = diff * diff;
        for (i = 1; i < n; i++) {
            diff = first[i] - second[0];
            matrixD[i][0] = matrixD[i - 1][0] + diff * diff;
        }

        for (j = 1; j < m; j++) {
            diff = first[0] - second[j];
            matrixD[0][j] = matrixD[0][j - 1] + diff * diff;
        }

        for (i = 1; i < n; i++) {
            tooBig = true;

            for (j = 1; j < m; j++) {
                diff = first[i] - second[j];
                matrixD[i][j] = GenericTools.min3(matrixD[i - 1][j - 1], matrixD[i][j - 1], matrixD[i - 1][j]) + diff * diff;
                if (tooBig && matrixD[i][j] < cutOffValue)
                    tooBig = false;
            }
            //Early abandon
            if (tooBig)
                return Double.POSITIVE_INFINITY;
        }

        return matrixD[n - 1][m - 1];
    }

    public double distance(final Sequence first, final Sequence second, final int windowSize, final double cutOffValue) {
        boolean tooBig;
        final int n = first.length();
        final int m = second.length();

        double diff;
        int i, j, jStart, jEnd, indexInfyLeft;

        diff = first.value(0) - second.value(0);
        matrixD[0][0] = diff * diff;
        for (i = 1; i < Math.min(n, 1 + windowSize); i++) {
            diff = first.value(i) - second.value(0);
            matrixD[i][0] = matrixD[i - 1][0] + diff * diff;
        }

        for (j = 1; j < Math.min(m, 1 + windowSize); j++) {
            diff = first.value(0) - second.value(j);
            matrixD[0][j] = matrixD[0][j - 1] + diff * diff;
        }
        if (j < m)
            matrixD[0][j] = Double.POSITIVE_INFINITY;

        for (i = 1; i < n; i++) {
            tooBig = true;
            jStart = Math.max(1, i - windowSize);
            jEnd = Math.min(m, i + windowSize + 1);
            indexInfyLeft = i - windowSize - 1;
            if (indexInfyLeft >= 0)
                matrixD[i][indexInfyLeft] = Double.POSITIVE_INFINITY;

            for (j = jStart; j < jEnd; j++) {
                diff = first.value(i) - second.value(j);
                matrixD[i][j] = GenericTools.min3(matrixD[i - 1][j - 1], matrixD[i][j - 1], matrixD[i - 1][j]) + diff * diff;
                if (tooBig && matrixD[i][j] < cutOffValue)
                    tooBig = false;
            }
            //Early abandon
            if (tooBig)
                return Double.POSITIVE_INFINITY;

            if (j < m)
                matrixD[i][j] = Double.POSITIVE_INFINITY;
        }

        return matrixD[n - 1][m - 1];
    }

    public static int findPathLen(int n, int m) {
        int i = n;
        int j = m;
        int count = 0;
        while (i > 0 || j > 0) {
            if (pathD[i][j] == 0) {
                i = i - 1;
                j = j - 1;
            } else if (pathD[i][j] == 1)
                i = i - 1;
            else if (pathD[i][j] == 2)
                j = j - 1;
            count++;
        }

        return count;
    }
}

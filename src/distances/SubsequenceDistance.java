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
 * This is a class to compute Subsequence distance
 *
 * @author Chang Wei
 */
public class SubsequenceDistance {
    public final double distance(final Sequence first, final Sequence second) {
        final int n = first.length();
        final int m = second.length();

        if (n < m) return distance(second, first);

        final int diffLen = n - m;
        double bsfDist = Double.POSITIVE_INFINITY;
        for (int j = 0; j <= diffLen; j++) {
            double dist = 0;
            for (int i = 0; i < m; i++) {
                final double diff = first.value(i + j) - second.value(i);
                dist += diff * diff;
            }
            if (dist < bsfDist) bsfDist = dist;
        }
        return bsfDist;
    }

    public final double distance(final Sequence first, final Sequence second, final double cutOffValue) {
        final int n = first.length();
        final int m = second.length();

        if (n < m) return distance(second, first, cutOffValue);

        final int diffLen = n - m;
        double bsfDist = Double.POSITIVE_INFINITY;
        for (int j = 0; j <= diffLen; j++) {
            double dist = 0;
            for (int i = 0; i < m; i++) {
                final double diff = first.value(i + j) - second.value(i);
                dist += diff * diff;
                if (dist > cutOffValue)
                    break;
            }
            if (dist < bsfDist) bsfDist = dist;
        }
        return bsfDist;
    }

    public final double distance(final double[] first, final double[] second) {
        final int n = first.length;
        final int m = second.length;
        if (n < m) return distance(second, first);

        final int diffLen = n - m;
        double bsfDist = Double.POSITIVE_INFINITY;
        for (int j = 0; j <= diffLen; j++) {
            double dist = 0;
            for (int i = 0; i < m; i++) {
                final double diff = first[i + j] - second[i];
                dist += diff * diff;
            }
            if (dist < bsfDist) bsfDist = dist;
        }
        return bsfDist;
    }

    public final double distance(final double[] first, final double[] second, final double cutOffValue) {
        final int n = first.length;
        final int m = second.length;
        if (n < m) return distance(second, first);

        final int diffLen = n - m;
        double bsfDist = Double.POSITIVE_INFINITY;
        for (int j = 0; j <= diffLen; j++) {
            double dist = 0;
            for (int i = 0; i < m; i++) {
                final double diff = first[i + j] - second[i];
                dist += diff * diff;
                if (dist > cutOffValue)
                    break;
            }
            if (dist < bsfDist) bsfDist = dist;
        }
        return bsfDist;
    }
}

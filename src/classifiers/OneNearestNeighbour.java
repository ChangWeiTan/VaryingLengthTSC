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
package classifiers;

import data.Sequence;

/**
 * This is a super class for 1NN
 *
 * @author Chang Wei
 */
public abstract class OneNearestNeighbour extends TimeseriesClassifier {
    public abstract double distance(final Sequence first, final Sequence second);

    public abstract double distance(final Sequence first, final Sequence second, final double cutOffValue);

    @Override
    public int classifyInstance(final Sequence query) {
        int[] classCounts = new int[this.trainData.numClasses()];

        double dist;

        Sequence candidate = trainData.get(0);
        double bsfDistance = distance(query, candidate);
        classCounts[candidate.getLabel()]++;

        for (int candidateIndex = 1; candidateIndex < trainData.size(); candidateIndex++) {
            candidate = trainData.get(candidateIndex);
            dist = distance(query, candidate);
            if (dist < bsfDistance) {
                bsfDistance = dist;
                classCounts = new int[trainData.numClasses()];
                classCounts[candidate.getLabel()]++;
            } else if (dist == bsfDistance) {
                classCounts[candidate.getLabel()]++;
            }
        }

        int bsfClass = -1;
        double bsfCount = -1;
        for (int i = 0; i < classCounts.length; i++) {
            if (classCounts[i] > bsfCount) {
                bsfCount = classCounts[i];
                bsfClass = i;
            }
        }
        return bsfClass;
    }

}

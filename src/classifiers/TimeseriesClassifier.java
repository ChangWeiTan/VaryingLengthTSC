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
import data.Sequences;

/**
 * This is a super class for time series classifier
 *
 * @author Chang Wei
 */
public abstract class TimeseriesClassifier {
    Sequences trainData;

    public abstract void summary();

    public abstract void setParamsFromParamId(final int paramId);

    public void buildClassifier(final Sequences trainData) throws Exception {
        this.trainData = trainData;
    }

    public double accuracy(final Sequences testData) {
        final int testSize = testData.size();
        int nCorrect = 0;

        for (int i = 0; i < testSize; i++) {
            final Sequence query = testData.get(i);
            final int predictClass = classifyInstance(query);
            if (predictClass == query.getLabel()) nCorrect++;
        }

        return 1.0 * nCorrect / testSize;
    }

    public abstract int classifyInstance(final Sequence sequence);
}

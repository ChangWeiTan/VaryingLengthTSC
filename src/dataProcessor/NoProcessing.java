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
package dataProcessor;

import data.Sequence;
import data.Sequences;
import normalization.NoNormalizer;
import normalization.Normalizer;
import normalization.ZNormalizer;
import utilities.DataLoader;

/**
 * This is a class to not pre-process the time series
 *
 * @author Chang Wei
 */
public class NoProcessing extends DataProcessor {
    public static void main(String[] args) {
        final DataProcessor dataProcessor = new NoProcessing();
        final Normalizer zNormalizer = new ZNormalizer();
        final Normalizer noNormalizer = new NoNormalizer();
        final DataLoader dataLoader = new DataLoader();
        final String problem = "PLAID";
        final int method = 0;

        System.out.println(String.format("[NO-PROCESSING] Loading %s problem", problem));
        Sequences train = dataLoader.loadTrainData(problem, dataProcessor, noNormalizer, method);
        Sequences test = dataLoader.loadTestData(problem, dataProcessor, noNormalizer, method);
        train.summary();
        test.summary();

        System.out.println(String.format("[NO-PROCESSING] Loading Z-Norm %s problem", problem));
        Sequences trainZNorm = dataLoader.loadTrainData(problem, dataProcessor, zNormalizer, method);
        Sequences testZNorm = dataLoader.loadTestData(problem, dataProcessor, zNormalizer, method);
        trainZNorm.summary();
        testZNorm.summary();

        for (int i = 0; i < trainZNorm.size(); i++) {
            Sequence sequence = train.get(i);
            Sequence normSequence = trainZNorm.get(i);

            System.out.print("t" + i + "=[" + sequence.value(0));
            for (int j = 1; j < sequence.length(); j++) {
                System.out.print("," + sequence.value(j));
            }
            System.out.println("];");

            System.out.print("tNorm" + i + "=[" + normSequence.value(0));
            for (int j = 1; j < normSequence.length(); j++) {
                System.out.print("," + normSequence.value(j));
            }
            System.out.println("];");
        }
    }

    @Override
    public double[] process(final double[] data, final int maxLen) {
        return data;
    }
}

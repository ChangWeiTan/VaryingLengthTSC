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
 * This is a class to uniformly rescale the time series to the same length
 *
 * @author Chang Wei
 */
public class SameLengthRescaler extends DataProcessor {
    public static void main(String[] args) {
        final DataProcessor noProcessing = new NoProcessing();
        final DataProcessor dataProcessor = new SameLengthRescaler();
        final Normalizer zNormalizer = new ZNormalizer();
        final Normalizer noNormalizer = new NoNormalizer();
        final DataLoader dataLoader = new DataLoader();
        final String problem = "PLAID";
        final int method = 0;

        System.out.println(String.format("[RESCALER] Loading Original %s problem", problem));
        Sequences trainOri = dataLoader.loadTrainData(problem, noProcessing, noNormalizer, method);
        Sequences testOri = dataLoader.loadTestData(problem, noProcessing, noNormalizer, method);
        trainOri.summary();
        testOri.summary();

        System.out.println(String.format("[RESCALER] Loading %s problem", problem));
        Sequences train = dataLoader.loadTrainData(problem, dataProcessor, noNormalizer, method);
        Sequences test = dataLoader.loadTestData(problem, dataProcessor, noNormalizer, method);
        train.summary();
        test.summary();

        System.out.println(String.format("[RESCALER] Loading Z-Norm %s problem", problem));
        Sequences trainZNorm = dataLoader.loadTrainData(problem, dataProcessor, zNormalizer, method);
        Sequences testZNorm = dataLoader.loadTestData(problem, dataProcessor, zNormalizer, method);
        trainZNorm.summary();
        testZNorm.summary();

        for (int i = 0; i < trainZNorm.size(); i++) {
            Sequence oriSequence = trainOri.get(i);
            Sequence sequence = train.get(i);
            Sequence normSequence = trainZNorm.get(i);

            System.out.print("tOri" + i + "=[" + oriSequence.value(0));
            for (int j = 1; j < oriSequence.length(); j++) {
                System.out.print("," + oriSequence.value(j));
            }
            System.out.println("];");

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
        final int seqLen = data.length;
        final double[] scaledData = new double[maxLen];

        for (int j = 0; j < maxLen; j++) {
            final int scalingFactor = (int) (1.0 * j * seqLen / maxLen);
            scaledData[j] = data[scalingFactor];
        }

        return scaledData;
    }
}

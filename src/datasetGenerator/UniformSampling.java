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
package datasetGenerator;

import FileIO.OutFile;
import data.Sequence;
import data.Sequences;
import utilities.DataLoader;
import utilities.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * This is a class to generate uniform time series
 *
 * @author Chang Wei
 */
public class UniformSampling extends DataGenerator {
    private final Random random = new Random();

    private UniformSampling(final long seed) {
        random.setSeed(seed);
    }

    public static void main(String[] args) {
        String problem = "ItalyPowerDemand";
        String datasetPath = Path.datasetPath;
        String outputPath = "C:/Users/" + System.getProperty("user.name") + "/workspace/Dataset/";
        double percentageTransformed = 0.85;
        int seed = 1234567;

        if (args.length > 0) outputPath = args[0];
        if (args.length > 1) datasetPath = args[1];
        if (args.length > 2) problem = args[2];
        if (args.length > 3) percentageTransformed = Double.parseDouble(args[3]);
        if (args.length > 4) seed = Integer.parseInt(args[4]);

        System.out.println(String.format("[UNIFORM-SAMPLING] Output path:    %s", outputPath));
        System.out.println(String.format("[UNIFORM-SAMPLING] Dataset path:   %s", datasetPath));
        System.out.println(String.format("[UNIFORM-SAMPLING] Problem:        %s", problem));
        System.out.println(String.format("[UNIFORM-SAMPLING] Percentage:     %s", percentageTransformed));
        System.out.println(String.format("[UNIFORM-SAMPLING] Seed:           %s", seed));

        outputPath += "UCRArchive_2018_Uniform_Sampling" + problem;

        Path.setOutputPath(outputPath);
        Path.setDatasetPath(datasetPath);

        final UniformSampling dataTransformer = new UniformSampling(seed);
        final DataLoader dataLoader = new DataLoader();
        final Sequences trainData = dataLoader.loadTrain(datasetPath, problem);
        final Sequences testData = dataLoader.loadTest(datasetPath, problem);
        trainData.summary();
        testData.summary();
        final int maxTrainLength = trainData.maxLength();
        final int maxTestLength = testData.maxLength();

        System.out.println("[UNIFORM-SAMPLING] Transforming data");
        final Sequences transformedTrain = dataTransformer.transform(trainData, percentageTransformed);
        final Sequences transformedTest = dataTransformer.transform(testData, percentageTransformed);
        transformedTrain.summary();
        transformedTest.summary();

        System.out.println("[UNIFORM-SAMPLING] Saving data");
        OutFile outFile = new OutFile(outputPath, problem + "_TRAIN.tsv");
        for (int i = 0; i < transformedTrain.size(); i++) {
            final Sequence instance = transformedTrain.get(i);
            final int diff = maxTrainLength - instance.length();
            StringBuilder output = new StringBuilder((instance.getLabel() + trainData.getMinClass()) + "");
            for (int j = 0; j < instance.length(); j++) {
                output.append("\t").append(instance.value(j));
            }
            for (int j = 0; j < diff; j++) {
                output.append("\t").append("NaN");
            }
            outFile.writeLine(output.toString());
        }
        outFile.closeFile();

        outFile = new OutFile(outputPath, problem + "_TEST.tsv");
        for (int i = 0; i < transformedTest.size(); i++) {
            final Sequence instance = transformedTest.get(i);
            final int diff = maxTestLength - instance.length();
            StringBuilder output = new StringBuilder((instance.getLabel() + testData.getMinClass()) + "");
            for (int j = 0; j < instance.length(); j++) {
                output.append("\t").append(instance.value(j));
            }
            for (int j = 0; j < diff; j++) {
                output.append("\t").append("NaN");
            }
            outFile.writeLine(output.toString());
        }
        outFile.closeFile();
    }


    @Override
    public Sequences transform(final Sequences data, final double percentageTransformed) {
        final Sequences transformedData = new Sequences(data);
        final int dataSize = data.size();
        final int numberTransformed = (int) (percentageTransformed * dataSize);
        final int numberToRemove = dataSize - numberTransformed;
        final ArrayList<Integer> candidates = new ArrayList<>(dataSize);
        final ArrayList<Integer> classVals = new ArrayList<>();
        final ArrayList<Integer> lengths = new ArrayList<>();
        int maxlen = Integer.MIN_VALUE;
        int minlen = Integer.MAX_VALUE;
        int minClass = Integer.MAX_VALUE;

        for (int i = 0; i < dataSize; i++) {
            candidates.add(i);
        }
        Collections.shuffle(candidates);

        for (int i = 0; i < numberToRemove; i++) {
            final int remove = random.nextInt(candidates.size());
            final int index = candidates.get(remove);
            final Sequence candidate = transformedData.get(index);
            final int candidateLength = candidate.length();
            final int candidateClass = candidate.getLabel();

            maxlen = Math.max(maxlen, candidateLength);
            minlen = Math.min(minlen, candidateLength);
            if (!lengths.contains(candidateLength)) lengths.add(candidateLength);
            if (!classVals.contains(candidateClass)) classVals.add(candidateClass);
            if (candidateClass < minClass) minClass = candidateClass;

            candidates.remove(remove);
        }
        Collections.sort(candidates);

        for (int i = 0; i < numberTransformed; i++) {
            final int index = candidates.get(i);
            final Sequence candidate = transformedData.get(index);
            final int candidateLength = candidate.length();
            final int tsClass = candidate.getLabel();
            final double randNum = random.nextDouble();
            final int newLength = (int) (randNum * (candidateLength - minLen)) + minLen;
            final double rate = 1.0 * candidateLength / newLength;
            final double[] newTs = new double[newLength];

            int j = 0;
            for (double pos = 0; Math.round(pos) < candidateLength; pos += rate) {
                final int ceilIndex = (int) Math.min(candidateLength - 1, Math.max(1, Math.ceil(pos)));
                final int floorIndex = ceilIndex - 1;
                newTs[j] = linearInterp(pos, candidate.value(floorIndex), floorIndex, candidate.value(ceilIndex), ceilIndex);
                j++;
            }


            maxlen = Math.max(maxlen, newLength);
            minlen = Math.min(minlen, newLength);

            if (!lengths.contains(newLength)) lengths.add(newLength);
            if (!classVals.contains(tsClass)) classVals.add(tsClass);
            if (tsClass < minClass) minClass = tsClass;
            final Sequence sequence = new Sequence(newTs, candidate.getLabel());

            transformedData.set(index, sequence);
        }
        transformedData.setLengths(lengths, maxlen, minlen);
        transformedData.updateClass(classVals);

        return transformedData;
    }

    private double linearInterp(final double t, final double y0, final int t0, final double y1, final int t1) {
        return y0 + (t - t0) * (y1 - y0) / (t1 - t0);
    }
}

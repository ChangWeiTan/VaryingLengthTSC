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
import dataProcessor.*;
import distances.DTW;
import normalization.NoNormalizer;
import normalization.Normalizer;
import utilities.DataLoader;

/**
 * This is a class for 1NN DTW
 *
 * @author Chang Wei
 */
public class DTW1NN extends OneNearestNeighbour {
    private DTW distComputer = new DTW();
    private double r = 1;
    private int window;

    public void summary() {
        System.out.println("[CLASSIFIER SUMMARY] Classifier: DTW1NN" +
                "\n[CLASSIFIER SUMMARY] r: " + r +
                "\n[CLASSIFIER SUMMARY] window: " + window);
    }

    public static void main(String[] args) throws Exception {
        final DataProcessor dataProcessor = new NoProcessing();
        final Normalizer normalizer = new NoNormalizer();
        final DataLoader dataLoader = new DataLoader();
        final int method = 0;
        final String datasetPath = "C:/Users/" + System.getProperty("user.name") + "/workspace/Dataset/UCRArchive_2018_Uniform_Sampling/";
        final String problem = "ECGFiveDays";
        final Sequences trainData = dataLoader.loadTrainData(datasetPath, problem, dataProcessor, normalizer, method);
        final Sequences testData = dataLoader.loadTestData(datasetPath, problem, dataProcessor, normalizer, method);
        trainData.summary();
        testData.summary();

        final DTW1NN classifier = new DTW1NN();
        classifier.buildClassifier(trainData);
        classifier.setParamsFromParamId(100);

        classifier.summary();
        System.out.println("[DTW1NN] Start Classifying");
        final double accuracy = classifier.accuracy(testData);
        final double accuracyLong = classifier.accuracyLong(testData);
        final double accuracyShort = classifier.accuracyShort(testData);
        final double accuracyOptimalPath = classifier.accuracyPathLen(testData);
        System.out.println(String.format("[DTW1NN] Accuracy: %.4f", accuracy));
        System.out.println(String.format("[DTW1NN] Accuracy Long: %.4f", accuracyLong));
        System.out.println(String.format("[DTW1NN] Accuracy Short: %.4f", accuracyShort));
        System.out.println(String.format("[DTW1NN] Accuracy Path Length: %.4f", accuracyOptimalPath));
    }

    public double accuracyPathLen(final Sequences testData) {
        final int testSize = testData.size();
        int nCorrect = 0;

        for (int i = 0; i < testSize; i++) {
            final Sequence query = testData.get(i);
            final int predictClass = classifyPathLen(query);
            if (predictClass == query.getLabel()) nCorrect++;
        }

        return 1.0 * nCorrect / testSize;
    }

    public double accuracyShort(final Sequences testData) {
        final int testSize = testData.size();
        int nCorrect = 0;

        for (int i = 0; i < testSize; i++) {
            final Sequence query = testData.get(i);
            final int predictClass = classifyShort(query);
            if (predictClass == query.getLabel()) nCorrect++;
        }

        return 1.0 * nCorrect / testSize;
    }

    public double accuracyLong(final Sequences testData) {
        final int testSize = testData.size();
        int nCorrect = 0;

        for (int i = 0; i < testSize; i++) {
            final Sequence query = testData.get(i);
            final int predictClass = classifyLong(query);
            if (predictClass == query.getLabel()) nCorrect++;
        }

        return 1.0 * nCorrect / testSize;
    }

    private int classifyShort(final Sequence query) {
        final int queryLen = query.length();
        int[] classCounts = new int[this.trainData.numClasses()];

        double dist;

        Sequence candidate = trainData.get(0);
        double bsfDistance = distance(query, candidate);
        int normFactor = Math.min(candidate.length(), queryLen);
        double bsfDistanceNorm = bsfDistance / normFactor;
        classCounts[candidate.getLabel()]++;

        for (int candidateIndex = 1; candidateIndex < trainData.size(); candidateIndex++) {
            candidate = trainData.get(candidateIndex);
            normFactor = Math.min(candidate.length(), queryLen);
            dist = distance(query, candidate);
            final double distNorm = dist / normFactor;
            if (distNorm < bsfDistanceNorm) {
                bsfDistance = dist;
                bsfDistanceNorm = distNorm;
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

    private int classifyLong(final Sequence query) {
        final int queryLen = query.length();
        int[] classCounts = new int[this.trainData.numClasses()];

        double dist;

        Sequence candidate = trainData.get(0);
        double bsfDistance = distance(query, candidate);
        int normFactor = Math.max(candidate.length(), queryLen);
        double bsfDistanceNorm = bsfDistance / normFactor;
        classCounts[candidate.getLabel()]++;

        for (int candidateIndex = 1; candidateIndex < trainData.size(); candidateIndex++) {
            candidate = trainData.get(candidateIndex);
            normFactor = Math.max(candidate.length(), queryLen);
            dist = distance(query, candidate);
            final double distNorm = dist / normFactor;
            if (distNorm < bsfDistanceNorm) {
                bsfDistance = dist;
                bsfDistanceNorm = distNorm;
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

    private int classifyPathLen(final Sequence query) {
        int[] classCounts = new int[this.trainData.numClasses()];

        double dist;

        Sequence candidate = trainData.get(0);
        double bsfDistance = distance(query, candidate);
        int pathLen = DTW.findPathLen(query.length(), candidate.length());
        bsfDistance = bsfDistance / pathLen;
        classCounts[candidate.getLabel()]++;

        for (int candidateIndex = 1; candidateIndex < trainData.size(); candidateIndex++) {
            candidate = trainData.get(candidateIndex);
            dist = distance(query, candidate);
            pathLen = DTW.findPathLen(query.length(), candidate.length());
            dist = dist / pathLen;
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

    @Override
    public double distance(final Sequence first, final Sequence second) {
        if (r < 1) {
//            window = (int) (r * Math.max(first.length(), second.length()));
            return distComputer.distance(first, second, window);
        }
        return distComputer.distance(first, second);
    }

    @Override
    public double distance(final Sequence first, final Sequence second, final double cutOffValue) {
        if (r < 1) {
//            window = (int) (r * Math.max(first.length(), second.length()));
            return distComputer.distance(first, second, window, cutOffValue);
        }
        return distComputer.distance(first, second, cutOffValue);
    }

    @Override
    public void setParamsFromParamId(final int paramId) {
        r = 1.0 * paramId / 100;
        window = (int) (r * trainData.maxLength());
    }
}

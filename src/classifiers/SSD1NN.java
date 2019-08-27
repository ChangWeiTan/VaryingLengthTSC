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
import dataProcessor.DataProcessor;
import dataProcessor.SuffixNoisePadder;
import distances.SubsequenceDistance;
import normalization.Normalizer;
import normalization.ZNormalizer;
import utilities.DataLoader;

/**
 * This is a class for 1NN Subsequence Distance
 *
 * @author Chang Wei
 */
public class SSD1NN extends OneNearestNeighbour {
    private SubsequenceDistance distComputer = new SubsequenceDistance();

    public void summary() {
        System.out.println("[CLASSIFIER SUMMARY] Classifier: SSD1NN");
    }

    public static void main(String[] args) throws Exception {
        final DataProcessor dataProcessor = new SuffixNoisePadder(100);
        final Normalizer normalizer = new ZNormalizer();
        final DataLoader dataLoader = new DataLoader();
        final int method = 1;
        final String datasetPath = "C:/Users/" + System.getProperty("user.name") + "/workspace/Dataset/UCRArchive_2018_Subsequence/";
        final String problem = "ItalyPowerDemand";
        final Sequences trainData = dataLoader.loadTrainData(datasetPath, problem, dataProcessor, normalizer, method);
        final Sequences testData = dataLoader.loadTestData(datasetPath, problem, dataProcessor, normalizer, method);
        trainData.summary();
        testData.summary();

        final SSD1NN classifier = new SSD1NN();
        classifier.buildClassifier(trainData);

        classifier.summary();
        System.out.println("[Euclidean1NN] Start Classifying");
        final double accuracy = classifier.accuracy(testData);
        System.out.println(String.format("[Euclidean1NN] Accuracy: %.4f", accuracy));
        System.out.println(String.format("[Euclidean1NN] Loss: %.4f", 1 - accuracy));
    }

    @Override
    public double distance(final Sequence first, final Sequence second) {
        return distComputer.distance(first, second);
    }

    @Override
    public double distance(final Sequence first, final Sequence second, final double cutOffValue) {
        return distComputer.distance(first, second, cutOffValue);
    }

    @Override
    public void setParamsFromParamId(final int paramId) {

    }
}

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
import distances.UniformScalingEuclidean;
import normalization.*;
import utilities.DataLoader;

/**
 * This is a class for 1NN with Uniform Scaling Euclidean Distance
 *
 * @author Chang Wei
 */
public class USEuclidean1NN extends OneNearestNeighbour {
    private UniformScalingEuclidean distComputer = new UniformScalingEuclidean();

    public void summary() {
        System.out.println("[CLASSIFIER SUMMARY] Classifier: USEuclidean1NN");
    }

    public static void main(String[] args) throws Exception {
        final DataProcessor dataProcessor = new SameLengthRescaler();
        final Normalizer normalizer = new NoNormalizer();
        final DataLoader dataLoader = new DataLoader();
        final int method = 1;
        final String datasetPath = "C:/Users/" + System.getProperty("user.name") + "/workspace/Dataset/UCRArchive_2018_Uniform_Sampling/";
        final String problem = "ArrowHead";
        final Sequences trainData = dataLoader.loadTrainData(datasetPath, problem, dataProcessor, normalizer, method);
        final Sequences testData = dataLoader.loadTestData(datasetPath, problem, dataProcessor, normalizer, method);
        trainData.summary();
        testData.summary();

        final USEuclidean1NN classifier = new USEuclidean1NN();
        classifier.buildClassifier(trainData);

        classifier.summary();
        System.out.println("[USEuclidean1NN] Start Classifying");
        final double accuracy = classifier.accuracy(testData);
        System.out.println(String.format("[USEuclidean1NN] Accuracy: %.4f", accuracy));
        System.out.println(String.format("[USEuclidean1NN] Loss: %.4f", 1 - accuracy));
    }

    public double[] getScaledSeries() {
        return distComputer.scaledSeries;
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

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
package experiments;

import FileIO.OutFile;
import classifiers.DTW1NN;
import data.Sequences;
import dataProcessor.*;
import normalization.NoNormalizer;
import normalization.Normalizer;
import normalization.ZNormalizer;
import utilities.DataLoader;
import utilities.Path;

/**
 * This is a class to run experiments using 1NN DTW
 *
 * @author Chang Wei
 */
public class BaselineDTW1NN {
    public static void main(String[] args) throws Exception {
        final DataLoader dataLoader = new DataLoader();
        int method = 0;
        int process = 2;
        String norm = "NoNorm";

        String problem = "ArrowHead";
        String datasetPath = "C:/Users/" + System.getProperty("user.name") + "/workspace/Dataset/UCRArchive_2018_Uniform_Sampling/";
        String outputPath = Path.setOutputPath();
        int paramId = 100;

        if (args.length > 0) outputPath = args[0];
        if (args.length > 1) datasetPath = args[1];
        if (args.length > 2) problem = args[2];
        if (args.length > 3) paramId = Integer.parseInt(args[3]);
        if (args.length > 4) norm = args[4];
        if (args.length > 5) process = Integer.parseInt(args[5]);
        if (args.length > 6) method = Integer.parseInt(args[6]);

        DataProcessor dataProcessor;
        switch (process) {
            case 0: // no processing
                dataProcessor = new NoProcessing();
                outputPath += "No-Processing/";
                break;
            case 1: // prefix suffix noise
                dataProcessor = new PrefixSuffixNoisePadder();
                outputPath += "Prefix-Suffix-Noise/";
                break;
            case 2: // prefix suffix zero
                dataProcessor = new PrefixSuffixZeroPadder();
                outputPath += "Prefix-Suffix-Zero/";
                break;
            case 3: // rescale
                dataProcessor = new SameLengthRescaler();
                outputPath += "Same-Length-Rescaler/";
                break;
            case 4: // suffix noise
                dataProcessor = new SuffixNoisePadder();
                outputPath += "Suffix-Noise/";
                break;
            default:
                dataProcessor = new SuffixNoisePadder();
        }
        outputPath += "DTW1NN/";
        System.out.println(String.format("[BASELINE-DTW1NN] Output path:    %s", outputPath));
        System.out.println(String.format("[BASELINE-DTW1NN] Dataset path:   %s", datasetPath));
        System.out.println(String.format("[BASELINE-DTW1NN] Problem:        %s", problem));
        System.out.println(String.format("[BASELINE-DTW1NN] ParamID:        %s", paramId));
        System.out.println(String.format("[BASELINE-DTW1NN] Norm:           %s", norm));
        System.out.println(String.format("[BASELINE-DTW1NN] Process:        %d", process));
        System.out.println(String.format("[BASELINE-DTW1NN] Method:         %d", method));

        Normalizer normalizer;
        if (norm.equals("NoNorm")) normalizer = new NoNormalizer();
        else normalizer = new ZNormalizer();

        Path.setOutputPath(outputPath);
        Path.setDatasetPath(datasetPath);

        final Sequences trainData = dataLoader.loadTrainData(datasetPath, problem, dataProcessor, normalizer, method);
        final Sequences testData = dataLoader.loadTestData(datasetPath, problem, dataProcessor, normalizer, method);
        trainData.summary();
        testData.summary();

        final DTW1NN classifier = new DTW1NN();
        classifier.buildClassifier(trainData);
        classifier.setParamsFromParamId(paramId);
        classifier.summary();

        System.out.println("[BASELINE-DTW1NN] Start Classifying");
        final double accuracy = classifier.accuracy(testData);
        System.out.println(String.format("[BASELINE-DTW1NN] Accuracy: %.4f", accuracy));
        System.out.println(String.format("[BASELINE-DTW1NN] Loss: %.4f", 1 - accuracy));

        OutFile outFile = new OutFile(outputPath, problem + "_" + norm + "_" + method + "_DTW1NN.csv");
        outFile.writeLine(problem + "," +
                accuracy + "," +
                (1 - accuracy));
        outFile.closeFile();
    }
}

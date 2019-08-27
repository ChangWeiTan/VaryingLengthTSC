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
import core.AppContext;
import core.ProximityForestResult;
import dataProcessor.*;
import datasets.ListDataset;
import normalization.NoNormalizer;
import normalization.Normalizer;
import normalization.ZNormalizer;
import trees.ProximityForest;
import utilities.DataLoader;
import utilities.Path;

/**
 * This is a class to run experiments using Proximity Forest
 *
 * @author Chang Wei
 */
public class BaselineProximityForest {
    public static void main(String[] args) throws Exception {
        final DataLoader dataLoader = new DataLoader();
        int method = 0;
        int process = 1;
        String norm = "ZNorm";

        String problem = "AllGestureWiimoteY";
        String datasetPath = "C:/Users/" + System.getProperty("user.name") + "/workspace/Dataset/UCRArchive_2018/";
        String outputPath = Path.setOutputPath();
        int paramId = 100;

        if (args.length > 0) outputPath = args[0];
        if (args.length > 1) datasetPath = args[1];
        if (args.length > 2) problem = args[2];
        if (args.length > 3) paramId = Integer.parseInt(args[3]);
        if (args.length > 4) norm = args[4];
        if (args.length > 5) process = Integer.parseInt(args[5]);
        if (args.length > 6) method = Integer.parseInt(args[6]);

        AppContext.training_file = datasetPath + problem + "/" + problem + "_TRAIN.tsv";
        AppContext.testing_file = datasetPath + problem + "/" + problem + "_TEST.tsv";
        AppContext.output_dir = outputPath;
        AppContext.num_repeats = 1;
        AppContext.num_trees = 100;
        AppContext.num_candidates_per_split = 5;
        AppContext.random_dm_per_node = true;
        AppContext.shuffle_dataset = true;
        AppContext.csv_has_header = false;
        AppContext.target_column_is_first = true;
        AppContext.verbosity = 0;
        AppContext.export_level = 0;
        System.out.println("[BASELINE-PROXIMITY-FOREST] Parameters for Proximity Forest: " +
                "repeats=" + AppContext.num_repeats +
                ", trees=" + AppContext.num_trees +
                ", r=" + AppContext.num_candidates_per_split);

        DataProcessor dataProcessor;
        switch (process) {
            case 0: // no processing
                System.out.println("[BASELINE-PROXIMITY-FOREST] No-Processing not applicable. Time series needs to have same length.");
                return;
            case 1: // prefix suffix noise
                dataProcessor = new PrefixSuffixNoisePadder();
                outputPath += "Prefix-Suffix-Noise/";
                break;
            case 2: // prefix suffix zero
                System.out.println("[BASELINE-PROXIMITY-FOREST] Prefix-Suffix-Zero not applicable. Time series needs to have same length.");
                return;
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
        outputPath += "ProximityForest/";
        System.out.println(String.format("[BASELINE-PROXIMITY-FOREST] Output path:    %s", outputPath));
        System.out.println(String.format("[BASELINE-PROXIMITY-FOREST] Dataset path:   %s", datasetPath));
        System.out.println(String.format("[BASELINE-PROXIMITY-FOREST] Problem:        %s", problem));
        System.out.println(String.format("[BASELINE-PROXIMITY-FOREST] ParamID:        %s", paramId));
        System.out.println(String.format("[BASELINE-PROXIMITY-FOREST] Norm:           %s", norm));
        System.out.println(String.format("[BASELINE-PROXIMITY-FOREST] Process:        %d", process));
        System.out.println(String.format("[BASELINE-PROXIMITY-FOREST] Method:         %d", method));

        Normalizer normalizer;
        if (norm.equals("NoNorm")) normalizer = new NoNormalizer();
        else normalizer = new ZNormalizer();

        Path.setOutputPath(outputPath);
        Path.setDatasetPath(datasetPath);

        ListDataset trainData = dataLoader.loadPFTrainData(datasetPath, problem, dataProcessor, normalizer, method);
        ListDataset testData = dataLoader.loadPFTestData(datasetPath, problem, dataProcessor, normalizer, method);
        trainData = trainData.reorder_class_labels(null);
        testData = testData.reorder_class_labels(trainData._get_initial_class_labels());
        trainData.shuffle();

        double accuracy = 0;
        for (int i = 0; i < AppContext.num_repeats; i++) {
            final ProximityForest pf = new ProximityForest(i);
            System.out.println("[BASELINE-PROXIMITY-FOREST] Training in progress, " + i);
            pf.train(trainData);
            System.out.println("[BASELINE-PROXIMITY-FOREST] Training completed");
            System.out.println("[BASELINE-PROXIMITY-FOREST] Testing in progress");
            ProximityForestResult result = pf.test(testData);
            System.out.println("[BASELINE-PROXIMITY-FOREST] Repetition, Dataset, Accuracy, TrainingTime(ms), TestingTime(ms), MeanDepthPerTree");
            result.printResults(problem, i, "");
            accuracy += result.accuracy;
        }

        accuracy /= AppContext.num_repeats;

        System.out.println(String.format("[BASELINE-PROXIMITY-FOREST] Accuracy: %.4f", accuracy));
        System.out.println(String.format("[BASELINE-PROXIMITY-FOREST] Loss: %.4f", 1 - accuracy));

        OutFile outFile = new OutFile(outputPath, problem + "_" + norm + "_" + method + "_ProximityForest.csv");
        outFile.writeLine(problem + "," +
                accuracy + "," +
                (1 - accuracy));
        outFile.closeFile();
    }
}

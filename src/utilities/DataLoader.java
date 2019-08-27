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
package utilities;

import data.Sequence;
import data.Sequences;
import dataProcessor.DataProcessor;
import dataProcessor.NoProcessing;
import dataProcessor.SuffixNoisePadder;
import datasets.ListDataset;
import normalization.Normalizer;
import normalization.ZNormalizer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * This is a class to load time series datasets
 *
 * @author Chang Wei
 */
public class DataLoader {
    private static String datasetPath = Path.datasetPath;
    private static final String delimiter = "\t";
    private static final String nan = "NaN";
    private boolean PRINT = true;

    public DataLoader() {

    }

    public DataLoader(boolean print) {
        PRINT = print;
    }

    public static void main(String[] args) {
        System.out.println("[DATALOADER] Test begins");
        testDataLoader();
    }

    private static void testDataLoader() {
        final DataProcessor dataProcessor = new NoProcessing();
        final Normalizer normalizer = new ZNormalizer();
        final DataLoader dataLoader = new DataLoader();
        final String problem = "PLAID";
        System.out.println(String.format("[DATALOADER] Loading %s problem", problem));
        Sequences train = dataLoader.loadTrainData(problem, dataProcessor, normalizer, 1);
        Sequences test = dataLoader.loadTestData(problem, dataProcessor, normalizer, 1);

        final int trainSize = train.size();
        final int trainClasses = train.numClasses();
        final int trainMinLen = train.minLength();
        final int trainMaxLen = train.maxLength();

        final int testSize = test.size();
        final int testClasses = test.numClasses();
        final int testMinLen = test.minLength();
        final int testMaxLen = test.maxLength();

        System.out.println(String.format("[DATALOADER] Train Size: %d", trainSize));
        System.out.println(String.format("[DATALOADER] Train Classes: %d", trainClasses));
        System.out.println(String.format("[DATALOADER] Train Min Length: %d", trainMinLen));
        System.out.println(String.format("[DATALOADER] Train Max Length: %d", trainMaxLen));

        System.out.println(String.format("[DATALOADER] Test Size: %d", testSize));
        System.out.println(String.format("[DATALOADER] Test Classes: %d", testClasses));
        System.out.println(String.format("[DATALOADER] Test Min Length: %d", testMinLen));
        System.out.println(String.format("[DATALOADER] Test Max Length: %d", testMaxLen));
    }

    private static void testLoad() {
        final DataLoader dataLoader = new DataLoader();
        final String problem = "AllGestureWiimoteX";
        System.out.println(String.format("[DATALOADER] Loading %s problem", problem));
        Sequences train = dataLoader.loadTrainPadNorm(problem);
        Sequences test = dataLoader.loadTestPadNorm(problem);

        final int trainSize = train.size();
        final int trainClasses = train.numClasses();
        final int trainMinLen = train.minLength();
        final int trainMaxLen = train.maxLength();

        final int testSize = test.size();
        final int testClasses = test.numClasses();
        final int testMinLen = test.minLength();
        final int testMaxLen = test.maxLength();

        System.out.println(String.format("[DATALOADER] Train Size: %d", trainSize));
        System.out.println(String.format("[DATALOADER] Train Classes: %d", trainClasses));
        System.out.println(String.format("[DATALOADER] Train Min Length: %d", trainMinLen));
        System.out.println(String.format("[DATALOADER] Train Max Length: %d", trainMaxLen));

        System.out.println(String.format("[DATALOADER] Test Size: %d", testSize));
        System.out.println(String.format("[DATALOADER] Test Classes: %d", testClasses));
        System.out.println(String.format("[DATALOADER] Test Min Length: %d", testMinLen));
        System.out.println(String.format("[DATALOADER] Test Max Length: %d", testMaxLen));
    }

    public final Sequences loadTrain(final String datasetPath, final String problem) {
        final String filename = datasetPath + problem + "/" + problem + "_TRAIN.tsv";
        final Sequences dataset = readData(filename);
        dataset.setName(problem);
        dataset.setTrainTest("train");
        return dataset;
    }

    public final Sequences loadTest(final String datasetPath, final String problem) {
        final String filename = datasetPath + problem + "/" + problem + "_TEST.tsv";
        final Sequences dataset = readData(filename);
        dataset.setName(problem);
        dataset.setTrainTest("test");
        return dataset;
    }

    public final Sequences loadTrainPadNorm(final String problem) {
        final String filename = datasetPath + problem + "/" + problem + "_TRAIN.tsv";
        final Sequences dataset = readPadNorm(filename);
        dataset.setName(problem);
        dataset.setTrainTest("train");
        return dataset;
    }

    public final Sequences loadTestPadNorm(final String problem) {
        final String filename = datasetPath + problem + "/" + problem + "_TEST.tsv";
        final Sequences dataset = readPadNorm(filename);
        dataset.setName(problem);
        dataset.setTrainTest("test");
        return dataset;
    }

    private Sequences readPadNorm(final String filename) {
        final Sequences dataset = new Sequences();
        final ArrayList<Integer> classVals = new ArrayList<>();
        final ArrayList<Integer> lengths = new ArrayList<>();
        final String nan = "NaN";

        BufferedReader br = null;
        String line;

        int maxlen = Integer.MIN_VALUE;
        int minlen = Integer.MAX_VALUE;
        int minClass = Integer.MAX_VALUE;
        Random random = new Random(100);

        if (PRINT) System.out.println("[DATALOADER] Reading " + filename);
        try {
            br = new BufferedReader(new FileReader(filename));
            while ((line = br.readLine()) != null) {
                final String[] data = line.split(delimiter);
                int seqLen = data.length - 1;
                final double[] tmp = new double[seqLen];

                final int tsClass = Integer.parseInt(data[0]);
                if (!classVals.contains(tsClass)) classVals.add(tsClass);
                if (tsClass < minClass) minClass = tsClass;


                double sum = 0;
                double sum2 = 0;
                for (int i = 1; i < data.length; i++) {
                    if (data[i].equals(nan)) {
                        final double val = random.nextDouble() / 1000;
                        tmp[i - 1] = val;
                        sum += val;
                        sum2 += val * val;
                        continue;
                    }
                    final double val = Double.parseDouble(data[i]);
                    tmp[i - 1] = val;
                    sum += val;
                    sum2 += val * val;
                }
                final double mean = sum / seqLen;
                final double sd = Math.sqrt(sum2 / (seqLen) - mean * mean);
                for (int i = 0; i < seqLen; i++) {
                    tmp[i] = (tmp[i] - mean) / sd;
                }
                final double[] ts = new double[seqLen];
                System.arraycopy(tmp, 0, ts, 0, seqLen);

                maxlen = Math.max(maxlen, seqLen);
                minlen = Math.min(minlen, seqLen);
                if (!lengths.contains(seqLen)) lengths.add(seqLen);

                final Sequence sequence = new Sequence(ts, tsClass);
                dataset.add(sequence);
            }
            dataset.setLengths(lengths, maxlen, minlen);
            dataset.updateClass(classVals);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return dataset;
    }

    private Sequences readData(final String filename) {
        final Sequences dataset = new Sequences();
        final ArrayList<Integer> classVals = new ArrayList<>();
        final ArrayList<Integer> lengths = new ArrayList<>();

        BufferedReader br = null;
        String line;

        int maxlen = Integer.MIN_VALUE;
        int minlen = Integer.MAX_VALUE;
        int minClass = Integer.MAX_VALUE;

        if (PRINT) System.out.println("[DATALOADER] Reading " + filename);
        try {
            br = new BufferedReader(new FileReader(filename));
            while ((line = br.readLine()) != null) {
                final String[] data = line.split(delimiter);
                int seqLen = data.length - 1;
                final double[] ts = ucr2series(data);

                final int tsClass = Integer.parseInt(data[0]);
                if (!classVals.contains(tsClass)) classVals.add(tsClass);
                if (tsClass < minClass) minClass = tsClass;

                maxlen = Math.max(maxlen, seqLen);
                minlen = Math.min(minlen, seqLen);
                if (!lengths.contains(seqLen)) lengths.add(seqLen);

                final Sequence sequence = new Sequence(ts, tsClass);
                dataset.add(sequence);
            }
            dataset.setLengths(lengths, maxlen, minlen);
            dataset.updateClass(classVals);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return dataset;
    }

    public final Sequences loadTrainData(final String problem, final DataProcessor dataProcessor, final Normalizer normalizer, final int method) {
        final String filename = datasetPath + problem + "/" + problem + "_TRAIN.tsv";
        final Sequences dataset = readData(filename, dataProcessor, normalizer, method);
        dataset.setName(problem);
        dataset.setTrainTest("train");
        return dataset;
    }

    public final Sequences loadTestData(final String problem, final DataProcessor dataProcessor, final Normalizer normalizer, final int method) {
        final String filename = datasetPath + problem + "/" + problem + "_TEST.tsv";
        final Sequences dataset = readData(filename, dataProcessor, normalizer, method);
        dataset.setName(problem);
        dataset.setTrainTest("test");
        return dataset;
    }

    public final Sequences loadTrainData(final String datasetPath, final String problem,
                                         final DataProcessor dataProcessor, final Normalizer normalizer, final int method) {
        final String filename = datasetPath + problem + "/" + problem + "_TRAIN.tsv";
        final Sequences dataset = readData(filename, dataProcessor, normalizer, method);
        dataset.setName(problem);
        dataset.setTrainTest("train");
        return dataset;
    }

    public final ListDataset loadPFTrainData(final String datasetPath, final String problem,
                                             final DataProcessor dataProcessor, final Normalizer normalizer, final int method) {
        final String filename = datasetPath + problem + "/" + problem + "_TRAIN.tsv";
        final Sequences dataset = readData(filename, dataProcessor, normalizer, method);
        dataset.setName(problem);
        dataset.setTrainTest("train");

        final ListDataset pfDataset = new ListDataset(dataset.size(), dataset.maxLength());
        for (int i = 0; i < dataset.size(); i++) {
            Sequence s = dataset.get(i);
            pfDataset.add(s.getLabel(), s.getData());
        }
        return pfDataset;
    }

    public final Sequences loadTestData(final String datasetPath, final String problem,
                                        final DataProcessor dataProcessor, final Normalizer normalizer, final int method) {
        final String filename = datasetPath + problem + "/" + problem + "_TEST.tsv";
        final Sequences dataset = readData(filename, dataProcessor, normalizer, method);
        dataset.setName(problem);
        dataset.setTrainTest("test");
        return dataset;
    }

    public final ListDataset loadPFTestData(final String datasetPath, final String problem,
                                            final DataProcessor dataProcessor, final Normalizer normalizer, final int method) {
        final String filename = datasetPath + problem + "/" + problem + "_TEST.tsv";
        final Sequences dataset = readData(filename, dataProcessor, normalizer, method);
        dataset.setName(problem);
        dataset.setTrainTest("train");

        final ListDataset pfDataset = new ListDataset(dataset.size(), dataset.maxLength());
        for (int i = 0; i < dataset.size(); i++) {
            Sequence s = dataset.get(i);
            pfDataset.add(s.getLabel(), s.getData());
        }
        return pfDataset;
    }

    /**
     * A method to read UCR dataset file.
     *
     * @param filename      UCR dataset file
     * @param dataProcessor processor to pre-process the data before it can be used
     * @param normalizer    normaliser for the data
     * @param method        indicate whether the data is pre-processed first (0) or normalised first (1)
     * @return UCR dataset
     */
    private Sequences readData(final String filename, final DataProcessor dataProcessor, final Normalizer normalizer, final int method) {
        final Sequences dataset = new Sequences();
        final ArrayList<Integer> classVals = new ArrayList<>();
        final ArrayList<Integer> lengths = new ArrayList<>();

        BufferedReader br = null;
        String line;

        int maxlen = Integer.MIN_VALUE;
        int minlen = Integer.MAX_VALUE;
        int minClass = Integer.MAX_VALUE;

        if (PRINT) System.out.println("[DATALOADER] Reading " + filename);
        try {
            br = new BufferedReader(new FileReader(filename));
            while ((line = br.readLine()) != null) {
                final String[] data = line.split(delimiter);
                final int maxLen = data.length - 1;
                final int tsClass = Integer.parseInt(data[0]);
                if (!classVals.contains(tsClass)) classVals.add(tsClass);
                if (tsClass < minClass) minClass = tsClass;

                final double[] series = ucr2series(data);
                double[] ts;
                if (method == 0) {
                    ts = dataProcessor.process(series, maxLen);
                    ts = normalizer.normalize(ts);
                } else {
                    ts = normalizer.normalize(series);
                    ts = dataProcessor.process(ts, maxLen);
                }
                final int seqLen = ts.length;

                maxlen = Math.max(maxlen, seqLen);
                minlen = Math.min(minlen, seqLen);
                if (!lengths.contains(seqLen)) lengths.add(seqLen);

                final Sequence sequence = new Sequence(ts, tsClass);
                dataset.add(sequence);
            }
            dataset.setLengths(lengths, maxlen, minlen);
            dataset.updateClass(classVals);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return dataset;
    }

    /**
     * A method to convert string data to double.
     *
     * @param data String data in UCR format. data[0] is class, so start with data[1]
     * @return time series data
     */
    private double[] ucr2series(String[] data) {
        int seqLen = data.length - 1;
        final double[] arr = new double[seqLen];
        for (int i = 1; i < data.length; i++) {
            if (data[i].equals(nan)) {
                seqLen = i - 1;
                break;
            }
            arr[i - 1] = Double.parseDouble(data[i]);
        }
        final double[] ts = new double[seqLen];
        System.arraycopy(arr, 0, ts, 0, seqLen);
        return ts;
    }
}

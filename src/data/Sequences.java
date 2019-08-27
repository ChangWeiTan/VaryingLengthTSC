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

package data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * This class describes the class for a set of time series (sequences)
 *
 * @author Chang Wei
 */
public class Sequences {
    private String name;                // name of the series if any
    private String trainTest;           // whether it is part of train or test set
    private ArrayList<Sequence> data;   // actual data of the series
    private ArrayList<Integer> lengths; // lengths of the series (can vary)
    private int numClass;               // number of classes in the set
    private int maxLen, minLen;         // max and min lengths
    private int minClass;               // smallest class in the set

    public Sequences() {
        this.data = new ArrayList<>();
    }

    public Sequences(final String name) {
        setName(name);
        this.data = new ArrayList<>();
    }

    public Sequences(final Sequences sequences) {
        // clone a sequence
        this.name = sequences.getName();
        this.data = (ArrayList<Sequence>) sequences.getData().clone();
        this.numClass = sequences.getNumClass();
        this.lengths = (ArrayList<Integer>) sequences.getLengths().clone();
        this.maxLen = sequences.maxLength();
        this.minLen = sequences.minLength();
        this.trainTest = sequences.getTrainTest();
        this.minClass = sequences.getMinClass();
    }

    public Sequences(final Sequences sequences, final int capacity) {
        initialize(sequences, capacity);
    }

    protected void initialize(Sequences dataset, int capacity) {
        if (capacity < 0)
            capacity = 0;

        this.data = new ArrayList<Sequence>(capacity);
    }

    public final void setTrainTest(final String trainTest) {
        this.trainTest = trainTest.toUpperCase();
    }

    public final void setName(final String name) {
        this.name = name;
    }

    public final String getName() {
        return this.name;
    }

    public final void updateClass(final ArrayList<Integer> classVal) {
        // update the class labels for easier processing
        numClass = classVal.size();
        Collections.sort(classVal);
        minClass = classVal.get(0);
        for (Sequence datum : data) {
            final int label = datum.getLabel();
            datum.setLabel(Math.min(numClass - 1, label - minClass));
        }
    }

    public final void setLengths(final ArrayList<Integer> lengths, final int maxLen, final int minLen) {
        // update the lengths of the series
        this.lengths = lengths;
        this.maxLen = maxLen;
        this.minLen = minLen;
    }

    public final Sequence get(final int i) {
        return this.data.get(i);
    }

    public final void add(final Sequence instance) {
        this.data.add(instance);
    }

    public final void set(final int index, final Sequence instance) {
        this.data.set(index, instance);
    }

    public final void remove(final int i) {
        this.data.remove(i);
    }

    public final void remove(final Sequence instance) {
        this.data.remove(instance);
    }

    public final String getTrainTest() {
        return trainTest;
    }

    public final int getNumClass() {
        return numClass;
    }

    public final int getMinClass() {
        return minClass;
    }

    public final ArrayList<Sequence> getData() {
        return data;
    }

    public final ArrayList<Integer> getLengths() {
        return lengths;
    }

    public final int size() {
        return this.data.size();
    }

    public final int minLength() {
        return this.minLen;
    }

    public final int maxLength() {
        return this.maxLen;
    }

    public final int numClasses() {
        return this.numClass;
    }

    public Sequence instance(final int index) {
        return data.get(index);
    }

    public final void summary() {
        System.out.println("[DATA SUMMARY] Problem: " + getName() + "(" + trainTest + ")" +
                "\n[DATA SUMMARY] Size: " + size() +
                "\n[DATA SUMMARY] Num Classes: " + numClasses() +
                "\n[DATA SUMMARY] MinLen: " + minLen +
                "\n[DATA SUMMARY] MaxLen: " + maxLen);
    }

    @Override
    public String toString() {
        return "Problem: " + getName() +
                "\tSize: " + size() +
                "\tNum Classes: " + numClasses() +
                "\tMinLen: " + minLen +
                "\tMaxLen: " + maxLen;
    }
}

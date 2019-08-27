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

/**
 * This class describes the class for a time series (sequence)
 *
 * @author Chang Wei
 */
public class Sequence {
    private int label;
    private double[] data;

    public Sequence(final double[] data, final int label) {
        setData(data);
        setLabel(label);
    }

    public final void setLabel(final int label) {
        this.label = label;
    }

    public final void setData(final double[] data) {
        this.data = data;
    }

    public final int getLabel() {
        return this.label;
    }

    public final double[] getData() {
        return this.data;
    }

    public final int length() {
        return this.data.length;
    }

    public final double value(final int i) {
        return this.data[i];
    }

    public final void printMatlab(final String name) {
        // print the series in vector form for Matlab
        System.out.println(getMatlabVector(name));
    }

    public final String getMatlabVector(final String name) {
        // get the series in Matlab vector
        StringBuilder str = new StringBuilder(name + "=[" + data[0]);
        for (int i = 1; i < data.length; i++) {
            str.append(",").append(data[i]);
        }
        str.append("];");
        return str.toString();
    }

    public final String getUCRVector(final int maxLen, final String delimiter) {
        // get the series in UCR archive format
        StringBuilder str = new StringBuilder(label + delimiter + data[0]);
        for (int i = 1; i < data.length; i++) {
            str.append(delimiter).append(data[i]);
        }
        for (int i = data.length; i < maxLen; i++) {
            str.append(delimiter).append("NaN");
        }
        return str.toString();
    }

    public final String toString() {
        StringBuilder str = new StringBuilder("s=[" + data[0]);
        for (int i = 1; i < data.length; i++) {
            str.append(",").append(data[i]);
        }
        str.append("];");
        return str.toString();
    }
}

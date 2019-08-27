/*
 *  Copyright 2006-2007 Columbia University.
 *
 *  This file is part of MEAPsoft.
 *
 *  MEAPsoft is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2 as
 *  published by the Free Software Foundation.
 *
 *  MEAPsoft is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MEAPsoft; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA
 *
 *  See the file "COPYING" for the text of the license.
 */
package signalProcessing;

import java.util.Arrays;
/*
 * Library of basic DSP algorithms.  Most of these have analogs in
 * Matlab with the same name.
 *
 * This code only operates on real valued data.
 *
 * @author Ron Weiss (ronw@ee.columbia.edu)
 */
public class DSP {
    public static double[] conv(double[] a, double[] b) {
        double[] y = new double[a.length + b.length - 1];

        // make sure that a is the shorter sequence
        if (a.length > b.length) {
            double[] tmp = a;
            a = b;
            b = tmp;
        }

        for (int lag = 0; lag < y.length; lag++) {
            y[lag] = 0;

            // where do the two signals overlap?
            int start = 0;
            // we can't go past the left end of (time reversed) a
            if (lag > a.length - 1)
                start = lag - a.length + 1;

            int end = lag;
            // we can't go past the right end of b
            if (end > b.length - 1)
                end = b.length - 1;

            //System.out.println("lag = " + lag +": "+ start+" to " + end);
            for (int n = start; n <= end; n++) {
                //System.out.println("  ai = " + (lag-n) + ", bi = " + n);
                y[lag] += b[n] * a[lag - n];
            }
        }

        return (y);
    }

    public static double[] xcorr(double[] a, double[] b) {
        int len = a.length;
        if (b.length > a.length)
            len = b.length;

        return xcorr(a, b, len - 1);

        // // reverse b in time
        // double[] brev = new double[b.length];
        // for(int x = 0; x < b.length; x++)
        //     brev[x] = b[b.length-x-1];
        //
        // return conv(a, brev);
    }

    public static double[] xcorr(double[] a) {
        return xcorr(a, a);
    }

    public static double[] xcorr(double[] a, double[] b, int maxlag) {
        double[] y = new double[2 * maxlag + 1];
        Arrays.fill(y, 0);

        for (int lag = b.length - 1, idx = maxlag - b.length + 1;
             lag > -a.length;
             lag--, idx++) {
            if (idx < 0)
                continue;

            if (idx >= y.length)
                break;

            // where do the two signals overlap?
            int start = 0;
            // we can't start past the left end of b
            if (lag < 0) {
                //System.out.println("b");
                start = -lag;

            }

            int end = a.length - 1;
            // we can't go past the right end of b
            if (end > b.length - lag - 1) {
                end = b.length - lag - 1;
                //System.out.println("a "+end);

            }

            //System.out.println("lag = " + lag +": "+ start+" to " + end+"   idx = "+idx);
            for (int n = start; n <= end; n++) {
                //System.out.println("  bi = " + (lag+n) + ", ai = " + n);
                y[idx] += a[n] * b[lag + n];

            }
            //System.out.println(y[idx]);
        }

        return (y);
    }


    public static double[] filter(double[] b, double[] a, double[] x) {
        double[] y = new double[x.length];

        // factor out a[0]
        if (a[0] != 1) {
            for (int ia = 1; ia < a.length; ia++)
                a[ia] = a[ia] / a[0];

            for (int ib = 0; ib < b.length; ib++)
                b[ib] = b[ib] / a[0];
        }

        for (int t = 0; t < x.length; t++) {
            y[t] = 0;

            // input terms
            int len = b.length - 1 < t ? b.length - 1 : t;
            for (int ib = 0; ib <= len; ib++)
                y[t] += b[ib] * x[t - ib];

            // output terms
            len = a.length - 1 < t ? a.length - 1 : t;
            for (int ia = 1; ia <= len; ia++)
                y[t] -= a[ia] * y[t - ia];
        }

        return y;
    }


    public static double[] hanning(int n) {
        double[] wind = new double[n];

        if (n == 1)
            wind[0] = 1;
        else
            for (int x = 1; x < n + 1; x++)
                wind[x - 1] = 0.5 * (1 - Math.cos(2 * Math.PI * x / (n + 1)));

        return wind;

    }


    public static double dot(double[] a, double[] b) {
        double y = 0;

        for (int x = 0; x < a.length; x++)
            y += a[x] * b[x];

        return y;

    }


    public static double[] times(double[] a, double[] b) {
        double[] y = new double[a.length];

        for (int x = 0; x < y.length; x++)
            y[x] = a[x] * b[x];

        return y;
    }

    public static double[] times(double[] a, double b) {
        double[] y = new double[a.length];

        for (int x = 0; x < y.length; x++)
            y[x] = a[x] * b;

        return y;
    }

    public static double[] rdivide(double[] a, double[] b) {
        double[] y = new double[a.length];

        for (int x = 0; x < y.length; x++)
            y[x] = a[x] / b[x];

        return y;
    }

    public static double[] rdivide(double[] a, double b) {
        double[] y = new double[a.length];

        for (int x = 0; x < y.length; x++)
            y[x] = a[x] / b;

        return y;

    }

    public static double[] plus(double[] a, double[] b) {
        double[] y = new double[a.length];

        for (int x = 0; x < y.length; x++)
            y[x] = a[x] + b[x];

        return y;
    }

    public static double[] plus(double[] a, double b) {
        double[] y = new double[a.length];

        for (int x = 0; x < y.length; x++)
            y[x] = a[x] + b;

        return y;
    }

    public static double[] minus(double[] a, double[] b) {
        double[] y = new double[a.length];

        for (int x = 0; x < y.length; x++)
            y[x] = a[x] - b[x];

        return y;
    }


    public static double[] minus(double[] a, double b) {
        double[] y = new double[a.length];

        for (int x = 0; x < y.length; x++)
            y[x] = a[x] - b;

        return y;
    }

    public static double[][] minus(double[][] A, double b) {
        double[][] Y = new double[A.length][A[0].length];

        for (int x = 0; x < Y.length; x++)
            for (int y = 0; x < Y[x].length; y++)
                Y[x][y] = A[x][y] - b;

        return Y;
    }

    public static double[] minus(double a, double[] b) {
        double[] y = new double[b.length];

        for (int x = 0; x < y.length; x++)
            y[x] = a - b[x];

        return y;
    }


    public static double sum(double[] a) {
        double y = 0;

        for (int x = 0; x < a.length; x++)
            y += a[x];

        return y;
    }

    public static double[] cumsum(double[] a) {
        double[] A = new double[a.length];

        A[0] = a[0];
        for (int x = 1; x < a.length; x++)
            A[x] = A[x - 1] + a[x];

        return A;
    }

    public static Pair maxWithIndex(double[] a) {
        Pair y = new Pair();
        double yy = Double.MIN_VALUE;
        int index = -1;

        for (int x = 0; x < a.length; x++)
            if (a[x] > yy) {
                yy = a[x];
                index = x;
            }

        y.value = yy;
        y.index = index;
        return y;
    }

    public static Pair minWithIndex(double[] a) {
        Pair y = new Pair();
        double yy = Double.MAX_VALUE;
        int index = -1;

        for (int x = 0; x < a.length; x++)
            if (a[x] > yy) {
                yy = a[x];
                index = x;
            }

        y.value = yy;
        y.index = index;
        return y;
    }

    public static double max(double[] a) {
        double y = Double.MIN_VALUE;

        for (int x = 0; x < a.length; x++)
            if (a[x] > y)
                y = a[x];

        return y;
    }

    public static double[] max(double[] a, double[] b) {
        double[] y = new double[a.length];

        for (int x = 0; x < a.length; x++) {
            if (a[x] > b[x])
                y[x] = a[x];
            else
                y[x] = b[x];

        }

        return y;

    }


    public static double[] max(double[] a, double b) {
        double[] y = new double[a.length];

        for (int x = 0; x < a.length; x++) {
            if (a[x] > b)
                y[x] = a[x];
            else
                y[x] = b;

        }

        return y;
    }

    public static double[][] max(double[][] a, double b) {
        double[][] y = new double[a.length][a[0].length];

        for (int r = 0; r < a.length; r++) {
            for (int c = 0; c < a[r].length; c++) {
                if (a[r][c] > b)
                    y[r][c] = a[r][c];
                else
                    y[r][c] = b;
            }
        }

        return y;
    }

    public static double[] max(double[][] a) {
        double[] y = new double[a.length];
        Arrays.fill(y, Double.MIN_VALUE);

        for (int r = 0; r < a.length; r++)
            for (int c = 0; c < a[r].length; c++)
                if (a[r][c] > y[r])
                    y[r] = a[r][c];

        return y;
    }

    public static double min(double[] a) {
        double y = Double.MAX_VALUE;

        for (int x = 0; x < a.length; x++)
            if (a[x] < y)
                y = a[x];

        return y;
    }

    public static double[] min(double[] a, double[] b) {
        double[] y = new double[a.length];

        for (int x = 0; x < a.length; x++) {
            if (a[x] < b[x])
                y[x] = a[x];
            else
                y[x] = b[x];

        }

        return y;

    }

    public static double[] min(double[] a, double b) {
        double[] y = new double[a.length];

        for (int x = 0; x < a.length; x++) {
            if (a[x] < b)
                y[x] = a[x];
            else
                y[x] = b;

        }

        return y;

    }


    public static double[][] min(double[][] a, double b) {
        double[][] y = new double[a.length][a[0].length];

        for (int r = 0; r < a.length; r++) {
            for (int c = 0; c < a[r].length; c++) {
                if (a[r][c] < b)
                    y[r][c] = a[r][c];
                else
                    y[r][c] = b;

            }

        }

        return y;
    }

    public static double[] min(double[][] a) {
        double[] y = new double[a.length];
        Arrays.fill(y, Double.MAX_VALUE);

        for (int r = 0; r < a.length; r++)
            for (int c = 0; c < a[r].length; c++)
                if (a[r][c] < y[r])
                    y[r] = a[r][c];

        return y;

    }

    public static int argmax(double[] a) {
        double y = Double.MIN_VALUE;
        int idx = -1;

        for (int x = 0; x < a.length; x++) {
            if (a[x] > y) {
                y = a[x];
                idx = x;

            }
        }

        return idx;
    }


    public static int argmin(double[] a) {
        double y = Double.MAX_VALUE;
        int idx = -1;

        for (int x = 0; x < a.length; x++) {
            if (a[x] < y) {
                y = a[x];
                idx = x;

            }

        }

        return idx;
    }

    public static double[] slice(double[] a, int start, int end) {
        start = Math.max(start, 0);
        end = Math.min(end, a.length - 1);

        double[] y = new double[end - start + 1];

        for (int x = start, iy = 0; x <= end; x++, iy++)
            y[iy] = a[x];

        return y;
    }

    public static double[] range(int start, int end) {
        return range(start, end, 1);

        // double[] y = new double[end-start+1];
        //
        // for(int x = 0, num = start; x < y.length; x++, num++)
        //     y[x] = num;
        //
        // return y;

    }

    public static double[] range(int start, int end, int increment) {
        double[] y = new double[1 + (end - start) / increment];

        for (int x = 0, num = start; x < y.length; x++, num += increment)
            y[x] = num;

        return y;

    }


    public static int[] irange(int start, int end, int increment) {
        int[] y = new int[1 + (end - start) / increment];

        for (int x = 0, num = start; x < y.length; x++, num += increment)
            y[x] = num;

        return y;
    }

    public static int[] irange(int start, int end) {
        return irange(start, end, 1);
    }

    public static double[] subsref(double[] a, int[] idx) {
        double[] y = new double[idx.length];
        for (int x = 0; x < idx.length; x++)
            y[x] = a[idx[x]];

        return y;
    }

    public static double[] subsref(double[] a, byte[] idx) {
        return subsref(a, find(idx));

    }

    public static int[] find(byte[] a) {
        int[] v = new int[20];

        int idx = 0;
        for (int x = 0; x < a.length; x++) {
            if (a[x] == 1) {
                //System.out.println(x+" "+a[x]+" "+v.length);

                v[idx++] = x;

                if (idx == v.length) {
                    int[] tmp = new int[2 * v.length];

                    for (int i = 0; i < v.length; i++)
                        tmp[i] = v[i];

                    v = tmp;

                }
            }
        }

        // but v might be too big:
        int[] tmp = new int[idx];
        for (int i = 0; i < tmp.length; i++)
            tmp[i] = v[i];

        //System.out.println(tmp.length);

        return tmp;
    }

    public static byte[] lt(double[] a, double[] b) {
        byte[] y = new byte[a.length];

        for (int x = 0; x < y.length; x++) {
            if (a[x] < b[x])
                y[x] = 1;
            else
                y[x] = 0;

        }

        return y;

    }


    public static byte[] lt(double[] a, double b) {
        byte[] y = new byte[a.length];

        for (int x = 0; x < y.length; x++) {
            if (a[x] < b)
                y[x] = 1;
            else
                y[x] = 0;

        }

        return y;
    }


    public static byte[] le(double[] a, double[] b) {
        byte[] y = new byte[a.length];

        for (int x = 0; x < y.length; x++) {
            if (a[x] <= b[x])
                y[x] = 1;
            else
                y[x] = 0;
        }
        return y;
    }


    public static byte[] le(double[] a, double b) {
        byte[] y = new byte[a.length];

        for (int x = 0; x < y.length; x++) {
            if (a[x] <= b)
                y[x] = 1;
            else
                y[x] = 0;
        }

        return y;
    }

    public static byte[] gt(double[] a, double[] b) {
        byte[] y = new byte[a.length];

        for (int x = 0; x < y.length; x++) {
            if (a[x] > b[x])
                y[x] = 1;
            else
                y[x] = 0;
        }

        return y;
    }

    public static byte[] gt(double[] a, double b) {
        byte[] y = new byte[a.length];

        for (int x = 0; x < y.length; x++) {
            if (a[x] > b)
                y[x] = 1;
            else
                y[x] = 0;

        }

        return y;

    }


    public static byte[] ge(double[] a, double[] b) {
        byte[] y = new byte[a.length];

        for (int x = 0; x < y.length; x++) {
            if (a[x] >= b[x])
                y[x] = 1;
            else
                y[x] = 0;

        }

        return y;
    }

    public static byte[] ge(double[] a, double b) {
        byte[] y = new byte[a.length];

        for (int x = 0; x < y.length; x++) {
            if (a[x] >= b)
                y[x] = 1;
            else
                y[x] = 0;
        }
        return y;
    }

    public static double median(double[] a) {
        // I don't want to sort the original array...  DSP.slice will
        // copy it.
        double[] tmp = slice(a, 0, a.length - 1);

        Arrays.sort(tmp);

        return tmp[(int) tmp.length / 2];
    }

    public static double mean(double[] a) {
        return sum(a) / a.length;

    }

    public static double[] mean(double[][] A) {
        double[] m = new double[A.length];
        int ncols = A[0].length;

        for (int x = 0; x < A.length; x++)
            for (int y = 0; y < A[x].length; y++)
                m[x] += A[x][y] / ncols;

        return m;
    }

    public static double[][] transpose(double[][] a) {
        double[][] y = new double[a[0].length][a.length];

        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < a[i].length; j++)
                y[j][i] = a[i][j];

        return y;
    }

    public static double[] round(double[] a) {
        double[] y = new double[a.length];

        for (int x = 0; x < y.length; x++)
            y[x] = Math.round(a[x]);

        return y;
    }

    public static double[] abs(double[] a) {
        double[] y = new double[a.length];

        for (int x = 0; x < y.length; x++)
            y[x] = Math.abs(a[x]);

        return y;

    }

    public static double[] cos(double[] a) {
        double[] y = new double[a.length];

        for (int x = 0; x < y.length; x++)
            y[x] = Math.cos(a[x]);

        return y;
    }

    public static double[] sin(double[] a) {
        double[] y = new double[a.length];

        for (int x = 0; x < y.length; x++)
            y[x] = Math.sin(a[x]);

        return y;
    }

    public static double[] power(double[] a, double b) {
        double[] y = new double[a.length];

        for (int x = 0; x < y.length; x++)
            y[x] = Math.pow(a[x], b);

        return y;
    }


    public static double[] log(double[] a) {
        double[] y = new double[a.length];

        for (int x = 0; x < y.length; x++)
            y[x] = Math.log(a[x]);

        return y;
    }

    public static double[] log10(double[] a) {
        double[] y = new double[a.length];
        double log10 = Math.log(10);

        for (int x = 0; x < y.length; x++)
            y[x] = Math.log(a[x]) / log10;

        return y;
    }

    public static double[] exp(double[] a) {
        double[] y = new double[a.length];

        for (int x = 0; x < y.length; x++)
            y[x] = Math.exp(a[x]);

        return y;

    }

    public static double[] todouble(byte[] a) {
        double[] y = new double[a.length];

        for (int x = 0; x < y.length; x++)
            y[x] = (double) a[x];

        return y;
    }

    public static double[] todouble(int[] a) {
        double[] y = new double[a.length];

        for (int x = 0; x < y.length; x++)
            y[x] = (double) a[x];

        return y;
    }

    public static void setColumn(double[][] A, int n, double[] b) {
        for (int i = 0; i < b.length; i++)
            A[i][n] = b[i];

    }


    public static double[] getColumn(double[][] A, int n) {
        double[] y = new double[A.length];

        for (int i = 0; i < A.length; i++)
            y[i] = A[i][n];

        return y;

    }

    public static void printArray(double[] a) {
        for (int x = 0; x < a.length; x++)
            System.out.print(a[x] + " ");
        System.out.println("");
    }


    public static void printArray(double[] a, String name) {
        System.out.print(name + " = ");

        printArray(a);

    }


    public static void main(String[] args) {
        double[] y = null;

        double[] a = {0.5157, 0.2720, 0.2316, 0.8995, 0.9087};
        //double[] a = {-31, 1, 0, 1, 0, 1, 0, 1, 0, 1};
        //double[] b = {1, 331};
        double[] b = {0.0873, 0.5390, 0.4284, 0.6172, 0.5589};

        int n = 10;

        //         java.util.Random rand = new java.util.Random();
        //         double[] a = new double[rand.nextInt(100)+1];
        //         double[] b = new double[rand.nextInt(100)+1];

        //         System.out.println("a.length = "+a.length);
        //         System.out.println("b.length = "+b.length);

        //         //java.util.Arrays.fill(a, 1);
        //         //java.util.Arrays.fill(b, 1);

        //         for(int x = 0; x < a.length; x++)
        //             a[x] = rand.nextDouble();
        //         for(int x = 0; x < b.length; x++)
        //             b[x] = rand.nextDouble();

//        String cmd = args[0];
        String cmd = "xcorr";

        printArray(a, "a");
        printArray(b, "b");
        if (cmd.equals("conv") || cmd.equals("xcorr")) {
            if (cmd.equals("conv"))
                y = DSP.conv(a, b);
            else {
//                if (args.length == 1)
//                    y = DSP.xcorr(b, a);
//                else
//                    y = DSP.xcorr(b, a, Integer.parseInt(args[1]));
                y = DSP.xcorr(b, a);

                System.out.print("xcorr(b,a) = ");
                for (int x = 0; x < y.length; x++)
                    System.out.print(y[x] + " ");
                System.out.println("");

//                if (args.length == 1)
//                    y = DSP.xcorr(a, b);
//                else
//                    y = DSP.xcorr(a, b, Integer.parseInt(args[1]));
                y = DSP.xcorr(a, b);
                System.out.print("xcorr(a,b) = ");
            }

        } else if (cmd.equals("filter")) {
            //double[] b = {1, -1};
            //double[] a = {1, -.99};
            double[] x = DSP.hanning(20);

            y = DSP.filter(b, a, x);
        } else if (cmd.equals("hanning")) {
            if (args.length > 1)
                n = Integer.parseInt(args[1]);

            y = DSP.hanning(n);
        }

        for (int x = 0; x < y.length; x++)
            System.out.print(y[x] + " ");

        System.out.println("");
    }


    public static class Pair {
        public double value;
        public int index;
    }
}

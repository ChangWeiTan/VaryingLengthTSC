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
package normalization;

/**
 * This is a class that does no normalisation on the time series
 *
 * @author Chang Wei
 */
public class NoNormalizer implements Normalizer {
    @Override
    public double[] normalize(double[] values) {
        return values;
    }
}

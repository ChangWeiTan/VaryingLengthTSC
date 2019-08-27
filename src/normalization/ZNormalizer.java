/*
 * Copyright 2015 Octavian Hasna
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package normalization;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class ZNormalizer implements Normalizer {
    private static final long serialVersionUID = 6446811014325682141L;
    private final Mean mean;
    private final StandardDeviation standardDeviation;

    public ZNormalizer() {
        this(new Mean(), new StandardDeviation(false));
    }

    public ZNormalizer(final Mean mean, final StandardDeviation standardDeviation) {
        this.mean = mean;
        this.standardDeviation = standardDeviation;
    }

    @Override
    public double[] normalize(double[] values) {
        double m = mean.evaluate(values);
        double sd = standardDeviation.evaluate(values, m);

        int length = values.length;
        double[] normalizedValues = new double[length];
        for (int i = 0; i < length; i++) {
            normalizedValues[i] = (values[i] - m) / sd;
        }
        return normalizedValues;
    }
}

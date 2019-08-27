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

import java.io.File;

/**
 * This is a super class for the required paths for the experiments
 *
 * @author Chang Wei
 */
public class Path {
    private final static String osName = System.getProperty("os.name");
    private final static String userName = System.getProperty("user.name");

    public static String datasetPath = setDatasetPath();

    public static String setOutputPath() {
        String outputPath = System.getProperty("user.dir") + "/output/";

        File dir = new File(outputPath);
        if (!dir.exists()) dir.mkdirs();
        return outputPath;
    }

    public static String setOutputPath(String outputPath) {
        File dir = new File(outputPath);
        if (!dir.exists()) dir.mkdirs();
        return outputPath;
    }

    private static String setDatasetPath() {
        if (osName.contains("Window")) {
            datasetPath = "C:/Users/" + userName + "/workspace/Dataset/UCRArchive_2018/";
        } else {
            datasetPath = "/home/" + userName + "/workspace/Dataset/UCRArchive_2018/";
        }

        return datasetPath;
    }

    public static String setDatasetPath(String dpath) {
        datasetPath = dpath;
        return datasetPath;
    }
}

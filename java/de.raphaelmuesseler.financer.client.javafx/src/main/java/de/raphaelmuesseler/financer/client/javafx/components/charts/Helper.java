/*
 * Copyright (c) 2017 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.raphaelmuesseler.financer.client.javafx.components.charts;

import javafx.geometry.Point2D;


/**
 * User: hansolo
 * Date: 03.11.17
 * Time: 04:45
 */
class Helper {

    private Helper() {
    }

    static int clamp(final int min, final int max, final int value) {
        if (value < min) return min;
        return Math.min(value, max);
    }

    static double clamp(final double min, final double max, final double value) {
        if (value < min) return min;
        return Math.min(value, max);
    }

    static int roundDoubleToInt(final double VALUE) {
        double dAbs = Math.abs(VALUE);
        int i = (int) dAbs;
        double result = dAbs - (double) i;
        if (result < 0.5) {
            return VALUE < 0 ? -i : i;
        } else {
            return VALUE < 0 ? -(i + 1) : i + 1;
        }
    }

    static Point2D[] subdividePoints(final Point2D[] points, final int subDevisions) {
        assert points != null;
        assert points.length >= 3;

        int noOfPoints = points.length;

        Point2D[] subdividedPoints = new Point2D[((noOfPoints - 1) * subDevisions) + 1];

        double increments = 1.0 / (double) subDevisions;

        for (int i = 0; i < noOfPoints - 1; i++) {
            Point2D p0 = i == 0 ? points[i] : points[i - 1];
            Point2D p1 = points[i];
            Point2D p2 = points[i + 1];
            Point2D p3 = (i + 2 == noOfPoints) ? points[i + 1] : points[i + 2];

            CatmullRom crs = new CatmullRom(p0, p1, p2, p3);

            for (int j = 0; j <= subDevisions; j++) {
                subdividedPoints[(i * subDevisions) + j] = crs.q(j * increments);
            }
        }
        return subdividedPoints;
    }
}
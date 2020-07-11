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
package org.financer.client.javafx.components.charts

import javafx.geometry.Point2D

/**
 * User: hansolo Date: 03.11.17 Time: 04:45
 */
internal object Helper {
    fun clamp(min: Int, max: Int, value: Int): Int {
        return if (value < min) min else Math.min(value, max)
    }

    fun clamp(min: Double, max: Double, value: Double): Double {
        return if (value < min) min else Math.min(value, max)
    }

    fun roundDoubleToInt(VALUE: Double): Int {
        val dAbs = Math.abs(VALUE)
        val i = dAbs.toInt()
        val result = dAbs - i.toDouble()
        return if (result < 0.5) {
            if (VALUE < 0) -i else i
        } else {
            if (VALUE < 0) -(i + 1) else i + 1
        }
    }

    fun subdividePoints(points: Array<Point2D?>?, subDevisions: Int): Array<Point2D?> {
        assert(points != null)
        assert(points!!.size >= 3)
        val noOfPoints = points.size
        val subdividedPoints = arrayOfNulls<Point2D>((noOfPoints - 1) * subDevisions + 1)
        val increments = 1.0 / subDevisions.toDouble()
        for (i in 0 until noOfPoints - 1) {
            val p0 = if (i == 0) points[i] else points[i - 1]
            val p1 = points[i]
            val p2 = points[i + 1]
            val p3 = if (i + 2 == noOfPoints) points[i + 1] else points[i + 2]
            val crs = CatmullRom(p0, p1, p2, p3)
            for (j in 0..subDevisions) {
                subdividedPoints[i * subDevisions + j] = crs.q(j * increments)
            }
        }
        return subdividedPoints
    }
}
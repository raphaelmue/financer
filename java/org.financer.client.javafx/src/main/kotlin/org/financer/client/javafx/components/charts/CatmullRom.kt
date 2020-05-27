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
 * User: hansolo Date: 03.11.17 Time: 04:47
 */
internal class CatmullRom(p0: Point2D?, p1: Point2D?, p2: Point2D?, p3: Point2D?) {
    private val splineXValues: CatmullRomSpline
    private val splineYValues: CatmullRomSpline

    // ******************** Methods *******************************************
    fun q(T: Double): Point2D {
        return Point2D(splineXValues.q(T), splineYValues.q(T))
    }

    // ******************** Inner Classes *************************************
    internal class CatmullRomSpline // ******************** Constructors **************************************
    (private val p0: Double, private val p1: Double, private val p2: Double, private val p3: Double) {

        // ******************** Methods *******************************************
        fun q(T: Double): Double {
            return 0.5 * (2 * p1 + (p2 - p0) * T + (2 * p0 - 5 * p1 + 4 * p2 - p3) * T * T + (3 * p1 - p0 - 3 * p2 + p3) * T * T * T)
        }

    }

    // ******************** Constructors **************************************
    init {
        assert(p0 != null) { "p0 cannot be null" }
        assert(p1 != null) { "p1 cannot be null" }
        assert(p2 != null) { "p2 cannot be null" }
        assert(p3 != null) { "p3 cannot be null" }
        splineXValues = CatmullRomSpline(p0!!.x, p1!!.x, p2!!.x, p3!!.x)
        splineYValues = CatmullRomSpline(p0.y, p1.y, p2.y, p3.y)
    }
}
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

package org.financer.client.javafx.components.charts;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;


/**
 * User: hansolo Date: 03.11.17 Time: 04:57
 */
public class SmoothedChartEvent extends Event {
    static final EventType<SmoothedChartEvent> DATA_SELECTED = new EventType<>(ANY, "DATA_SELECTED");
    private final double yValue;

    SmoothedChartEvent(final Object src, final EventTarget target, final EventType<SmoothedChartEvent> type, final double Y_VALUE) {
        super(src, target, type);
        yValue = Y_VALUE;
    }


    // ******************** Methods *******************************************
    public double getyValue() {
        return yValue;
    }
}
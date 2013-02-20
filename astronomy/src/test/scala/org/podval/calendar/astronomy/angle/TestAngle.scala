/*
 * Copyright 2011-2013 Podval Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.podval.calendar.astronomy.angle

import org.junit.Test
import org.junit.Assert


class TestAngle {

    @Test
    def construction() {
        construction(5, 34)
        construction(54, 34)
        construction(154, 59)
        construction(254, 0)
    }


    private def construction(degrees: Int, minutes: Int) {
        val angle = Angle(degrees, minutes)
        Assert.assertEquals(degrees, angle.degrees)
        Assert.assertEquals(minutes, angle.minutes)
    }


    @Test
    def conversion() {
        conversion(5, 34)
        conversion(54, 34)
        conversion(154, 59)
        conversion(254, 0)
    }


    private def conversion(degrees: Int, minutes: Int) {
        val angle = Angle(degrees, minutes)
        val value = angle.toDegrees
        val angle_ = Angle.fromDegrees(value, 2)
        Assert.assertEquals(angle, angle_)
    }


    @Test
    def rounding() {
        Assert.assertEquals(Angle(104,58,50), Angle.roundToSeconds(Angle(104,58,50,16,39,59,43)))
    }
}

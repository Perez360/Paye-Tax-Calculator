package com.generis.util

import java.math.RoundingMode
import java.text.DecimalFormat


fun doubleToBankersValue(value: Double): Double {

    val df = DecimalFormat("#.##")
    df.roundingMode =
        RoundingMode.HALF_EVEN // floor because its money so its better we gain than loose, I don't know if you barb
    return df.format(value).toDouble();
}



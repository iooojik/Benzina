package com.kirovcompany.bensina

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter


/**
 * This formatter is used for passing an array of x-axis labels, on whole x steps.
 */
class IndexAxisValueFormatter : ValueFormatter {
    private var mValues = arrayOf<String>()
    private var mValueCount = 0

    /**
     * An empty constructor.
     * Use `setValues` to set the axis labels.
     */
    constructor() {}

    /**
     * Constructor that specifies axis labels.
     *
     * @param values The values string array
     */
    constructor(values: Array<String>?) {
        if (values != null) this.values = values
    }

    /**
     * Constructor that specifies axis labels.
     *
     * @param values The values string array
     */
    constructor(values: Collection<String>?) {
        if (values != null) this.values = values.toTypedArray()
    }

    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        val index = Math.round(value)
        return if (index < 0 || index >= mValueCount || index != value.toInt()) "" else mValues[index]
    }

    var values: Array<String>?
        get() = mValues
        set(values) {
            var values = values
            if (values == null) values = arrayOf()
            mValues = values
            mValueCount = values.size
        }
}
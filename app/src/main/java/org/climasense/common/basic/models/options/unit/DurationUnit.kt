package org.climasense.common.basic.models.options.unit

import android.content.Context
import org.climasense.R
import org.climasense.common.basic.models.options._basic.UnitEnum
import org.climasense.common.basic.models.options._basic.Utils
import org.climasense.common.extensions.isRtl

// actual duration = duration(h) * factor.
enum class DurationUnit(
    override val id: String,
    override val unitFactor: Float
): UnitEnum<Float> {
    H("h", 1f);

    override val valueArrayId = R.array.duration_unit_values
    override val nameArrayId = R.array.duration_units
    override val voiceArrayId = R.array.duration_unit_voices

    override fun getName(context: Context) = Utils.getName(context, this)

    override fun getVoice(context: Context) = Utils.getVoice(context, this)

    override fun getValueWithoutUnit(valueInDefaultUnit: Float) = valueInDefaultUnit * unitFactor

    override fun getValueInDefaultUnit(valueInCurrentUnit: Float) = valueInCurrentUnit / unitFactor

    override fun getValueTextWithoutUnit(
        valueInDefaultUnit: Float
    ) = Utils.getValueTextWithoutUnit(this, valueInDefaultUnit, 2)!!

    override fun getValueText(
        context: Context,
        valueInDefaultUnit: Float
    ) = getValueText(context, valueInDefaultUnit, context.isRtl)

    override fun getValueText(
        context: Context,
        valueInDefaultUnit: Float,
        rtl: Boolean
    ) = Utils.getValueText(
        context = context,
        enum = this,
        valueInDefaultUnit = valueInDefaultUnit,
        decimalNumber = 2,
        rtl = rtl
    )

    override fun getValueVoice(
        context: Context,
        valueInDefaultUnit: Float
    ) = getValueVoice(context, valueInDefaultUnit, context.isRtl)

    override fun getValueVoice(
        context: Context,
        valueInDefaultUnit: Float,
        rtl: Boolean
    ) = Utils.getVoiceText(
        context = context,
        enum = this,
        valueInDefaultUnit = valueInDefaultUnit,
        decimalNumber = 2,
        rtl = rtl
    )
}
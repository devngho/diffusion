// SPDX-License-Identifier: MIT

package com.github.devngho.diffusion.core.environment

sealed interface Climate{
    val id: String

    class IceAgeImpl private constructor(): Climate{
        override val id = "ice_age"
        companion object {
            val i = IceAgeImpl()
        }
    }

    class LittleIceAgeImpl private constructor(): Climate{
        override val id = "little_ice_age"
        companion object {
            val i = LittleIceAgeImpl()
        }
    }

    class WarmAgeImpl private constructor(): Climate{
        override val id = "warm_age"
        companion object {
            val i = WarmAgeImpl()
        }
    }

    class HotAgeImpl private constructor(): Climate{
        override val id = "hot_age"
        companion object {
            val i = HotAgeImpl()
        }
    }

    companion object {
        var temp: Double = 25.0
        val IceAge = IceAgeImpl.i
        val LittleIceAge = LittleIceAgeImpl.i
        val WarmAge = WarmAgeImpl.i
        val HotAge = HotAgeImpl.i

        val climate: Climate
        get() {
            return if (this.temp < 15) IceAge
            else if (this.temp < 20) LittleIceAge
            else if (this.temp < 30) WarmAge
            else HotAge
        }
    }
}
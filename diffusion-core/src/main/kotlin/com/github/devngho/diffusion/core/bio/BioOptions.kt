// SPDX-License-Identifier: MIT

package com.github.devngho.diffusion.core.bio

sealed interface BioOptions {
    val id: String

    class HillFriendlyImpl private constructor() : BioOptions{
        override val id: String = "hill_friendly"
        companion object{
            val i = HillFriendlyImpl()
        }
    }

    class WaterFriendlyImpl private constructor(): BioOptions{
        override val id: String = "water_friendly"
        companion object{
            val i = WaterFriendlyImpl()
        }
    }

    companion object {
        val HillFriendly = HillFriendlyImpl.i
        val WaterFriendly = WaterFriendlyImpl.i
    }
}
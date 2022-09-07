// SPDX-License-Identifier: MIT

package com.github.devngho.diffusion.core.bio

import com.github.devngho.diffusion.core.util.Color
import java.util.*

class SmallAnimal(override val uuid: UUID) : Animal {
    override val options: MutableList<BioOptions> = mutableListOf(BioOptions.HillFriendly)
    override var diffusion: Int = 6
    override var power: Int = 1
    override val color: Color = Color.values().random()

    override fun clone(): BioProperty {
        val new = SmallAnimal(uuid)
        new.power = this.power
        new.diffusion = this.diffusion
        return new
    }
}
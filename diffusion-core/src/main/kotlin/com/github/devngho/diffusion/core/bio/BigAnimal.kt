// SPDX-License-Identifier: MIT

package com.github.devngho.diffusion.core.bio

import com.github.devngho.diffusion.core.util.Color
import java.util.*

class BigAnimal(override val uuid: UUID) : Animal {
    override val options: MutableList<BioOptions> = mutableListOf(BioOptions.HillFriendly)
    override var diffusion: Int = 3
    override var power: Int = 3
    override val color: Color = Color.values().random()

    override fun clone(): BioProperty {
        val new = BigAnimal(uuid)
        new.power = this.power
        new.diffusion = this.diffusion
        return new
    }
}
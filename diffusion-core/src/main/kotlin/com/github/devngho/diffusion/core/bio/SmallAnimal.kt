// SPDX-License-Identifier: MIT

package com.github.devngho.diffusion.core.bio

import com.github.devngho.diffusion.core.game.Player
import com.github.devngho.diffusion.core.util.Color
import java.util.*

class SmallAnimal(override val uuid: UUID, override val owner: Player) : Animal {
    override val options: MutableList<BioOptions> = mutableListOf(BioOptions.HillFriendly)
    override var diffusion: Int = 5
    override var power: Int = 1
    override var color: Color = Color.values().random()
    override var canDiffusion: Boolean = true

    override fun clone(): BioProperty {
        val new = SmallAnimal(uuid, owner)
        new.power = this.power
        new.diffusion = this.diffusion
        new.color = this.color
        return new
    }
}
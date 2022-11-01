// SPDX-License-Identifier: MIT

package com.github.devngho.diffusion.core.map

import com.github.devngho.diffusion.core.bio.BioOptions
import com.github.devngho.diffusion.core.bio.BioProperty

class Water : Tile {
    override val id: String = "water"

    override fun apply(bio: BioProperty): BioProperty {
        val b = bio.clone()
        if (!bio.options.contains(BioOptions.WaterFriendly)) b.diffusion /= 2
        return b
    }
}
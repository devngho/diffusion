// SPDX-License-Identifier: MIT

package com.github.devngho.diffusion.core.map

import com.github.devngho.diffusion.core.bio.BioProperty

class Mountain : Tile {
    override val id: String = "mountain"

    override fun BioProperty.apply(): BioProperty {
        val b = this.clone()
        b.diffusion = 0
        b.power = 0
        return b
    }
}
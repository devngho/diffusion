// SPDX-License-Identifier: MIT

package com.github.devngho.diffusion.core.map

import com.github.devngho.diffusion.core.bio.BioOptions
import com.github.devngho.diffusion.core.bio.BioProperty

class Hill : Tile {
    override val id: String = "hill"

    override fun BioProperty.apply(): BioProperty {
        val b = this.clone()
        if (!this.options.contains(BioOptions.HillFriendly)) b.diffusion /= 2
        return b
    }
}
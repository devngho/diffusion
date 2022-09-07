// SPDX-License-Identifier: MIT

package com.github.devngho.diffusion.core.map

import com.github.devngho.diffusion.core.bio.BioProperty

class Normal : Tile{
    override val id: String = "normal"

    override fun BioProperty.apply(): BioProperty {
        return this
    }
}
// SPDX-License-Identifier: MIT

package com.github.devngho.diffusion.core.map

import com.github.devngho.diffusion.core.bio.BioOptions
import com.github.devngho.diffusion.core.bio.BioProperty

class Normal : Tile{
    override val id: String = "normal"

    override fun apply(bio: BioProperty): BioProperty {
        return bio.clone()
    }
}
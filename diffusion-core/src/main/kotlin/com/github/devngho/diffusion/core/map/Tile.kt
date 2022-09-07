// SPDX-License-Identifier: MIT

package com.github.devngho.diffusion.core.map

import com.github.devngho.diffusion.core.bio.BioModifiers

interface Tile : Cloneable, BioModifiers{
    val id: String

    companion object {
        val hill = Hill()
        val mountain = Mountain()
        val normal = Normal()
        // val water = Water()
    }
}
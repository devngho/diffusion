// SPDX-License-Identifier: MIT

package com.github.devngho.diffusion.core.bio

interface BioProperty : Cloneable{
    var diffusion: Int
    var power: Int
    val fightPower: Int
    get() {
        return diffusion * power
    }

    val options: MutableList<BioOptions>

    public override fun clone(): BioProperty
}
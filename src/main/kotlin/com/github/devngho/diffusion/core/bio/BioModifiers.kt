// SPDX-License-Identifier: MIT

package com.github.devngho.diffusion.core.bio

interface BioModifiers {
    fun apply(bio: BioProperty): BioProperty
}
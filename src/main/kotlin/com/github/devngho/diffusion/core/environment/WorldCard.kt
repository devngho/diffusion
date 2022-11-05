// SPDX-License-Identifier: MIT

package com.github.devngho.diffusion.core.environment

import com.github.devngho.diffusion.core.bio.BioModifiers
import com.github.devngho.diffusion.core.bio.BioProperty

sealed interface WorldCard : BioModifiers {
    val id: String

    class SuitableDiffusion : WorldCard{
        override val id: String = "suitable_diffusion"

        override fun apply(bio: BioProperty): BioProperty {
            return bio.clone().apply {
                this.diffusion *= (3/2)
            }
        }
    }

    class UnsuitableDiffusion : WorldCard{
        override val id: String = "unsuitable_diffusion"

        override fun apply(bio: BioProperty): BioProperty {
            return bio.clone().apply {
                this.diffusion *= (3/4)
            }
        }
    }

    companion object {
        val applied = mutableListOf<WorldCard>()
    }
}
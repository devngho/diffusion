// SPDX-License-Identifier: MIT

package com.github.devngho.diffusion.core.bio

import com.github.devngho.diffusion.core.game.Player
import com.github.devngho.diffusion.core.util.Color
import java.util.UUID

interface Animal : BioProperty {
    val uuid: UUID
    val color: Color
    val owner: Player
    var canDiffusion: Boolean
}
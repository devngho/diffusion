// SPDX-License-Identifier: MIT

package com.github.devngho.diffusion.core.game

import com.github.devngho.diffusion.core.bio.Animal

data class AnimalTile(var animal: Animal?, var health: Int?)

data class AnimalMap(val map: MutableList<MutableList<AnimalTile>>)
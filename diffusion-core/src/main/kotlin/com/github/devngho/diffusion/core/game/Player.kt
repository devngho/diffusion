// SPDX-License-Identifier: MIT

package com.github.devngho.diffusion.core.game

import com.github.devngho.diffusion.core.bio.Animal

abstract class Player constructor(val name: String) {
    val animals = mutableListOf<Animal>()

    abstract suspend fun ask(ask: Ask<out Ask.AskData>)
}
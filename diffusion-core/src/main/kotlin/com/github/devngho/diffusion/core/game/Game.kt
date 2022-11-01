// SPDX-License-Identifier: MIT

package com.github.devngho.diffusion.core.game

import com.github.devngho.diffusion.core.bio.Animal
import com.github.devngho.diffusion.core.map.Generator
import kotlinx.coroutines.sync.Mutex

open class Game constructor(val player: List<Player>) {
    var map: com.github.devngho.diffusion.core.map.Map = Generator.generate()
    var animalMap = AnimalMap(MutableList(64) { MutableList(64) { AnimalTile(null, 15) } })
    var animals = mutableListOf<Animal>()
    var turns: Int = 0
    val turnMutex: Mutex = Mutex(false)
    var turn: Turn? = null

    open fun finalizeTurn(){

    }
}
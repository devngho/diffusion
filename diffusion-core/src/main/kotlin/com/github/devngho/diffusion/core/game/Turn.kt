// SPDX-License-Identifier: MIT

package com.github.devngho.diffusion.core.game

import com.github.devngho.diffusion.core.bio.Animal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class Turn(val game: Game) {
    suspend fun processTurn(scope: CoroutineScope){
        scope.launch {
            game.turnMutex.lock()
            game.turn = this@Turn

            val animalAsk = Ask.AskAnimal()
            val askResult = mutableListOf<Ask.AskAnimal.AskAnimalData>()

            val recv = game.player.map { async {  askResult += animalAsk.data.receive() } }

            val asks = game.player.map {
                async { it.ask(animalAsk) }
            }

            asks.awaitAll()
            recv.awaitAll()

            processTurn(askResult)
            processDiffusion()

            game.finalizeTurn()

            game.turn = null
            game.turnMutex.unlock()
        }
    }

    private fun processTurn(asks: MutableList<Ask.AskAnimal.AskAnimalData>){
        asks.forEach {
            if (it is Ask.AskAnimal.AskAnimalData.AddNew){
                game.animalMap.map[it.loc.x][it.loc.y].run {
                    animal = it.animal
                    health = it.animal.power
                }
                it.player.animals.add(it.animal)
                game.animals.add(it.animal)
            }else if (it is Ask.AskAnimal.AskAnimalData.Modify) {
                game.animals.find { f -> f.uuid == it.uuid }?.options?.let {opt ->
                    opt.clear()
                    opt.addAll(it.options)
                }
            }
        }
    }

    private fun processDiffusion(){
        val access = listOf(Pair(1, 0), Pair(0, 1), Pair(-1, 0), Pair(0, -1))
        game.animalMap.map.forEachIndexed { x, xTile ->
            xTile.forEachIndexed { y, animal ->
                if (animal.animal != null) {
                    access.forEach {
                        val nx = x + it.first
                        val ny = y + it.second
                        if (!(nx < 0 || ny < 0 || nx > 63 || ny > 63)) {
                            val tile = game.animalMap.map[nx][ny]

                            val p = game.map.map[nx][ny].apply(animal.animal!!.clone())

                            if (animal.animal?.canDiffusion == true) {
                                if (tile.animal == null) {
                                    tile.health -= p.diffusion
                                    if (tile.health < 1) {
                                        tile.animal = (animal.animal!!.clone() as Animal).apply {
                                            this.canDiffusion = false
                                        }
                                        tile.health = p.fightPower
                                    }
                                } else if (tile.animal?.uuid != animal.animal?.uuid) {
                                    tile.health -= p.power
                                    if (tile.health < 1) {
                                        tile.animal = null
                                        tile.health = 15
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        game.animalMap.map.flatten().forEach {
            it.animal?.canDiffusion = true
        }
    }
}
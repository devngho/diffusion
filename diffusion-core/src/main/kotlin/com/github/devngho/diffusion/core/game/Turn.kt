// SPDX-License-Identifier: MIT

package com.github.devngho.diffusion.core.game

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class Turn(val game: Game) {
    suspend fun processTurn(scope: CoroutineScope){
        withContext(scope.coroutineContext) {
            val animalAsk = Ask.AskAnimal()
            val askResult = mutableListOf<Ask.AskAnimal.AskAnimalData>()

            val recv = async { repeat(game.player.size) { askResult += animalAsk.data.receive() } }

            game.player.forEach {
                it.ask(animalAsk)
            }

            recv.await()

            processDiffusion(askResult)
        }
    }

    private fun processDiffusion(asks: MutableList<Ask.AskAnimal.AskAnimalData>){
        asks.forEach {
            if (it is Ask.AskAnimal.AskAnimalData.AddNew){
                game.animalMap.map[it.loc.x][it.loc.y].run {
                    animal = it.animal
                    health = it.animal.power
                }
            }else if (it is Ask.AskAnimal.AskAnimalData.Modify) {
                game.animals.find { f -> f.uuid == it.uuid }?.options?.let {opt ->
                    opt.clear()
                    opt.addAll(it.options)
                }
            }
        }
    }
}
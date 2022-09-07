package com.github.devngho.diffusion.core.game

import com.github.devngho.diffusion.core.bio.BigAnimal
import com.github.devngho.diffusion.core.map.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class TurnTest {

    class PlayerTestImpl(name: String, val askF: suspend (player: Player, ask: Ask<out Ask.AskData>) -> Unit) : Player(name) {
        override suspend fun ask(ask: Ask<out Ask.AskData>) {
            this@PlayerTestImpl.askF(this@PlayerTestImpl, ask)
        }
    }

    @Test
    fun processTurn() {
        runBlocking {
            val p1 = PlayerTestImpl("p1") { p, it ->
                when(it){
                    is Ask.AskAnimal -> {
                        it.data.send(Ask.AskAnimal.AskAnimalData.AddNew(p, Point(1, 1), BigAnimal(UUID.randomUUID())))
                    }
                }
            }

            val p2 = PlayerTestImpl("p2") { p, it ->
                when(it){
                    is Ask.AskAnimal -> {
                        it.data.send(Ask.AskAnimal.AskAnimalData.Modify(p, UUID.randomUUID(), mutableListOf()))
                    }
                }
            }

            val p = mutableListOf(p1, p2)
            val g = Game(p)

            Turn(g).processTurn(this)
        }
    }

    @Test
    fun generateMap(){
        assertEquals(Generator.generate().map.flatten().size, 64*64)
        assert(
            Generator
                .generate()
                .map
                .flatten()
                .filterNot { it is Normal || it is Mountain || it is Water || it is Hill }
                .isEmpty()
        )
    }
}
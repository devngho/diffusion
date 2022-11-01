// SPDX-License-Identifier: MIT

package com.github.devngho.diffusion.core.game

import com.github.devngho.diffusion.core.bio.Animal
import com.github.devngho.diffusion.core.bio.BioOptions
import kotlinx.coroutines.channels.Channel
import java.util.*

sealed interface Ask<T> {
    sealed interface AskData
    val id: String
    val data: Channel<T>

    class AskAnimal : Ask<AskAnimal.AskAnimalData> {
        sealed interface AskAnimalData : AskData {
            /**
             * 새로운 [Animal] [animal]을 맵의 [loc]에 추가하도록 요청하는 AskData.
             */
            class AddNew constructor(val player: Player, val loc: Point, val animal: Animal) : AskAnimalData

            /**
             * [uuid]를 가진 동물의 [Animal.options]를 [options]로 변경하는 AskData.
             */
            class Modify constructor(val player: Player, val uuid: UUID, val options: MutableList<BioOptions>) : AskAnimalData
            class Skip : AskAnimalData
        }

        override val id: String = "ask_animal"
        override val data: Channel<AskAnimalData> = Channel()

        override fun toString(): String {
            return "${this.id} ${this.data::class.simpleName}"
        }
    }
}
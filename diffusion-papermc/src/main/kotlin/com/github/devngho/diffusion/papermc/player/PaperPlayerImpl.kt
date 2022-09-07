package com.github.devngho.diffusion.papermc.player

import com.github.devngho.diffusion.core.bio.SmallAnimal
import com.github.devngho.diffusion.core.game.Ask
import com.github.devngho.diffusion.core.game.Player
import com.github.devngho.diffusion.core.game.Point
import com.github.devngho.diffusion.papermc.Plugin
import com.github.devngho.diffusion.papermc.game.PaperGame
import com.github.devngho.nplug.api.entity.FallingBlock
import com.github.devngho.nplug.impl.entity.FallingBlockImpl
import kotlinx.coroutines.delay
import org.bukkit.Material
import java.util.*

class PaperPlayerImpl(val player: org.bukkit.entity.Player) : Player(player.name) {
    lateinit var game: PaperGame

    override suspend fun ask(ask: Ask<out Ask.AskData>) {
        when(ask){
            is Ask.AskAnimal -> {
                val fallingBlock = FallingBlock.createFallingBlock(Material.JIGSAW, player.location, Plugin.plugin, false, mutableListOf(player))
                (fallingBlock as FallingBlockImpl).bukkitFallingEntity.run {
                    isGlowing = true
                }
                fallingBlock.updateMeta()
                val taskId: Int = player.server.scheduler.scheduleSyncRepeatingTask(Plugin.plugin, {
                    player.rayTraceBlocks(64.0)?.run {
                        hitBlock?.location?.run {
                            fallingBlock.position.set(x + .5, game.mapPosition.blockY.toDouble() - .1, z + .5)
                        }
                    }
                }, 0, 2)
                player.server.scheduler.scheduleSyncDelayedTask(Plugin.plugin, {
                    fallingBlock.remove()
                    player.server.scheduler.cancelTask(taskId)
                }, 50)
                delay(50)
                ask.data.send(Ask.AskAnimal.AskAnimalData.AddNew(this, Point(20, 20), SmallAnimal(UUID.randomUUID())))
            }

            else -> {}
        }
    }
}
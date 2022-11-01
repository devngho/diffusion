package com.github.devngho.diffusion.papermc.player

import com.github.devngho.diffusion.core.bio.BigAnimal
import com.github.devngho.diffusion.core.bio.SmallAnimal
import com.github.devngho.diffusion.core.game.Ask
import com.github.devngho.diffusion.core.game.Player
import com.github.devngho.diffusion.core.game.Turn
import com.github.devngho.diffusion.core.util.Color
import com.github.devngho.diffusion.papermc.Plugin
import com.github.devngho.diffusion.papermc.game.PaperGame
import com.github.devngho.diffusion.papermc.item.DiffusionItem
import com.github.devngho.diffusion.papermc.item.DiffusionItem.*
import com.github.devngho.nplug.api.entity.Armorstand
import com.github.devngho.nplug.api.entity.FallingBlock
import com.github.devngho.nplug.impl.entity.ArmorstandImpl
import com.github.devngho.nplug.impl.entity.FallingBlockImpl
import com.github.shynixn.mccoroutine.bukkit.launch
import kotlinx.coroutines.channels.Channel
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent
import java.util.*

class PaperPlayerImpl(val player: org.bukkit.entity.Player) : Player(player.name) {
    lateinit var game: PaperGame
    var ask: Ask<out Ask.AskData>? = null
    var res = Channel<Boolean>(0)
    var skip = false

    suspend fun onDiffusionItem(item: DiffusionItem, event: PlayerInteractEvent){
        fun baseItem(){
            player.inventory.run {
                setItem(6, SkipItem.item)
                if (event.player == game.owner) setItem(7, TurnItem.item)
                setItem(8, ResetItem.item)
            }
        }
        fun initItem(){
            player.inventory.run {
                setItem(0, AnimalAddNewItem.item)
                setItem(1, AnimalModifyItem.item)
                baseItem()
            }
        }

        when(item){
            AnimalAddNewItem -> {
                player.inventory.run {
                    clear()
                    setItem(0, AnimalAddNewBigItem.item)
                    setItem(1, AnimalAddNewSmallItem.item)
                    baseItem()
                }
            }
            AnimalModifyItem -> {
                player.inventory.run {
                    clear()
                    setItem(0, AnimalAddNewBigItem.item)
                    setItem(1, AnimalAddNewSmallItem.item)
                    baseItem()
                }
            }

            AnimalAddNewSmallItem -> {
                val a = ask
                if (a != null) {
                    if (a is Ask.AskAnimal) {
                        player.rayTraceBlocks(64.0)?.run {
                            hitBlock?.location?.run {
                                a.data.send(
                                    Ask.AskAnimal.AskAnimalData.AddNew(
                                        this@PaperPlayerImpl,
                                        game.getPointByWorld(this),
                                        SmallAnimal(UUID.randomUUID()).apply {
                                            color = Color.values().random()
                                        }
                                    )
                                )
                            }
                        }
                        res.send(true)
                    }
                }else {
                    player.sendMessage(Component.text("현재 턴과 맞지 않습니다!").color(TextColor.color(255, 0, 0)))
                }

                player.inventory.run {
                    clear()
                    initItem()
                }
            }
            AnimalAddNewBigItem -> {
                val a = ask
                if (a != null) {
                    if (a is Ask.AskAnimal) {
                        player.rayTraceBlocks(64.0)?.run {
                            hitBlock?.location?.run {
                                a.data.send(
                                    Ask.AskAnimal.AskAnimalData.AddNew(
                                        this@PaperPlayerImpl,
                                        game.getPointByWorld(this),
                                        BigAnimal(UUID.randomUUID()).apply {
                                            this.color = Color.values().random()
                                        }
                                    )
                                )
                            }
                        }
                        res.send(true)
                    }
                }else {
                    player.sendMessage(Component.text("현재 턴과 맞지 않습니다!").color(TextColor.color(255, 0, 0)))
                }

                player.inventory.run {
                    clear()
                    initItem()
                }
            }
            ResetItem -> {
                player.inventory.run {
                    clear()
                    initItem()
                }
            }
            TurnItem -> {
                if (Plugin.game?.owner === event.player && !game.turnMutex.isLocked){
                    Plugin.plugin.launch {
                        Turn(Plugin.game!!).processTurn(this)
                    }
                }
                player.inventory.run {
                    clear()
                    initItem()
                }
            }
            SkipItem -> {
                val a = ask
                if (a != null) {
                    if (a is Ask.AskAnimal) {
                        a.data.send(
                            Ask.AskAnimal.AskAnimalData.Skip()
                        )
                        res.send(true)
                    }
                }else {
                    player.sendMessage(Component.text("현재 턴과 맞지 않습니다!").color(TextColor.color(255, 0, 0)))
                }

                player.inventory.run {
                    clear()
                    initItem()
                }
            }
        }
    }

    override suspend fun ask(ask: Ask<out Ask.AskData>) {
        this.ask = ask
        if (this.skip) {
            when(ask){
                is Ask.AskAnimal -> ask.data.send(Ask.AskAnimal.AskAnimalData.Skip())
            }
        }else {
            when(ask){
                is Ask.AskAnimal -> {
                    val fallingBlock = FallingBlock.createFallingBlock(Material.JIGSAW, player.location, Plugin.plugin, false, mutableListOf(player))
                    val armorstand = Armorstand.createArmorstand(player.location, Plugin.plugin, mutableListOf(player))
                    (fallingBlock as FallingBlockImpl).bukkitFallingEntity.run {
                        isGlowing = true
                    }
                    (armorstand as ArmorstandImpl).bukkitEntity.run {
                        this.isMarker = true
                        this.isSmall = true
                        this.isVisible = false
                        this.isCustomNameVisible = true
                    }
                    fallingBlock.updateMeta()
                    val taskId: Int = player.server.scheduler.scheduleSyncRepeatingTask(Plugin.plugin, {
                        player.rayTraceBlocks(64.0)?.run {
                            hitBlock?.location?.run {
                                fallingBlock.position.set(x + .5, game.mapPosition.blockY.toDouble() - .1, z + .5)
                                armorstand.position.set(x + .5, game.mapPosition.blockY.toDouble() + 6, z + .5)
                                fallingBlock.updateLocation()
                                armorstand.updateLocation()

                                val p = game.getPointByWorld(this)
                                val x = p.x
                                val y = p.y

                                armorstand.bukkitEntity.run {
                                    this.customName(
                                        Component
                                            .text("${game.animalMap.map[x][y].health ?: "-"}")
                                            .color(TextColor.color(255, 0, 0)).append(
                                                Component.text(" | ").color(TextColor.color(255, 255, 255))).append(
                                                    Component
                                                        .text("${game.animalMap.map[x][y].animal?.diffusion ?: "-"}")
                                                        .color(TextColor.color(TextColor.color(0, 255, 0)))).append(
                                                            Component.text(" | ").color(TextColor.color(255, 255, 255))).append(
                                                                Component
                                                                    .text("${game.animalMap.map[x][y].animal?.canDiffusion ?: false} ${game.animalMap.map[x][y].animal?.power ?: "-"}")
                                                                    .color(TextColor.color(TextColor.color(0, 0, 255))))
                                    )
                                }
                                armorstand.updateMeta()
                            }
                        }
                                                                                                       }, 0, 1)
                    res.receive()
                    fallingBlock.remove()
                    armorstand.remove()
                    player.server.scheduler.cancelTask(taskId)
                }

                else -> {}
            }
        }
        this.ask = null
    }
}
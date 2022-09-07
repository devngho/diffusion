package com.github.devngho.diffusion.papermc

import com.github.devngho.diffusion.core.game.Turn
import com.github.devngho.diffusion.core.map.Generator
import com.github.devngho.diffusion.papermc.event.PlayerEvent
import com.github.devngho.diffusion.papermc.game.PaperGame
import com.github.devngho.diffusion.papermc.item.DiffusionItem
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.executors.NativeCommandExecutor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import com.github.devngho.nplug.api.entity.Armorstand
import com.github.devngho.nplug.api.entity.FallingBlock
import com.github.devngho.nplug.api.entity.Itemframe
import com.github.devngho.nplug.impl.entity.ArmorstandImpl
import com.github.devngho.nplug.impl.entity.FallingBlockImpl
import com.github.devngho.nplug.impl.util.Direction
import dev.jorel.commandapi.arguments.EntitySelector
import dev.jorel.commandapi.arguments.MultiLiteralArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import kotlinx.coroutines.runBlocking
import org.bukkit.plugin.java.JavaPlugin
import kotlin.concurrent.thread

class Plugin : JavaPlugin() {
    companion object {
        lateinit var plugin: JavaPlugin
    }

    var game: PaperGame? = null

    override fun onEnable() {
        super.onEnable()

        server.pluginManager.registerEvents(PlayerEvent(), this)
    }

    override fun onLoad() {
        plugin = this

        super.onLoad()

        CommandAPICommand("diffusion")
            .withSubcommand(
                CommandAPICommand("start")
                    .withPermission(CommandPermission.OP)
                    .withArguments(EntitySelectorArgument<Collection<Player>>("player", EntitySelector.MANY_PLAYERS))
                    .executesNative(NativeCommandExecutor { sender, args ->
                        val players = (args[0] as Collection<*>).toList().filterIsInstance<Player>().toMutableList()
                        game = PaperGame(players, sender.location, this)
                    })
            )
            .withSubcommand(
                CommandAPICommand("turn")
                    .withPermission(CommandPermission.OP)
                    .executesNative(NativeCommandExecutor { _, _ ->
                        val g = game
                        if (g != null) {
                            thread {
                                runBlocking{
                                    Turn(g).processTurn(this)
                                }
                            }
                        }
                    })
            )
            .withSubcommand(
                CommandAPICommand("debug")
                    .withSubcommand(
                        CommandAPICommand("generate")
                            .withPermission(CommandPermission.OP)
                            .executesNative(NativeCommandExecutor { sender, _ ->
                                val map = Generator.generate()

                                map.map.forEachIndexed { x, list ->
                                    list.forEachIndexed { y, v ->
                                        val loc = sender.location.clone().apply {
                                            this.x += x
                                            this.z += y
                                        }

                                        when(v.id){
                                            "hill" -> {
                                                loc.block.type = Material.STONE
                                                loc.apply { this.y += 1 }.block.type = Material.STONE
                                            }
                                            "normal" -> {
                                                loc.block.type = Material.GRASS_BLOCK
                                            }
                                            "water" -> {
                                                loc.block.type = Material.WATER
                                            }
                                            "mountain" -> {
                                                loc.block.type = Material.STONE
                                                loc.apply { this.y += 1 }.block.type = Material.STONE
                                                loc.apply { this.y += 1 }.block.type = Material.STONE
                                                loc.apply { this.y += 1 }.block.type = Material.STONE
                                                loc.apply { this.y += 1 }.block.type = Material.SNOW
                                            }
                                        }
                                    }
                                }
                            })
                    )
                    .withSubcommand(
                        CommandAPICommand("stand")
                            .withPermission(CommandPermission.OP)
                            .executesNative(NativeCommandExecutor { sender, _ ->
                                val stand = Armorstand.createArmorstand(sender.location, this, server.onlinePlayers.toMutableList())
                                (stand as ArmorstandImpl).bukkitEntity.apply {
                                    setItem(EquipmentSlot.HEAD, ItemStack(Material.BLUE_CONCRETE))
                                    isInvisible = true
                                    isMarker = true
                                }
                                stand.updateEquipments()
                                stand.updateMeta()
                            })
                    )
                    .withSubcommand(
                        CommandAPICommand("frame")
                            .withPermission(CommandPermission.OP)
                            .executesNative(NativeCommandExecutor { sender, _ ->
                                val stand = Itemframe.createItemframe(sender.location, this, server.onlinePlayers.toMutableList(), Direction.UP)
                                stand.updateMeta()
                            })
                    )
                    .withSubcommand(
                        CommandAPICommand("falling")
                            .withPermission(CommandPermission.OP)
                            .executesNative(NativeCommandExecutor { sender, _ ->
                                val fallingBlock = FallingBlock.createFallingBlock(Material.JIGSAW, sender.location, plugin, true, server.onlinePlayers.toMutableList())
                                (fallingBlock as FallingBlockImpl).bukkitFallingEntity.run {
                                    isGlowing = true
                                }
                                server.scheduler.scheduleSyncRepeatingTask(this, {fallingBlock.position.y += 0.1}, 0, 1)
                                fallingBlock.updateMeta()
                            })
                    )
                    .withSubcommand(
                        CommandAPICommand("update")
                            .withPermission(CommandPermission.OP)
                            .executesNative(NativeCommandExecutor { _, _ ->
                                game?.mapAnimalRender?.renderOrUpdate()
                            })
                    )
                    .withSubcommand(
                        CommandAPICommand("tracker")
                            .withPermission(CommandPermission.OP)
                            .executesPlayer(PlayerCommandExecutor { sender, _ ->
                                val fallingBlock = FallingBlock.createFallingBlock(
                                    Material.JIGSAW,
                                    sender.location,
                                    plugin,
                                    false,
                                    sender.server.onlinePlayers.toMutableList()
                                )
                                (fallingBlock as FallingBlockImpl).bukkitFallingEntity.run {
                                    isGlowing = true
                                }
                                fallingBlock.updateMeta()
                                val taskId: Int = sender.server.scheduler.scheduleSyncRepeatingTask(plugin, {
                                    sender.rayTraceBlocks(64.0)?.run {
                                        hitBlock?.location?.run {
                                            fallingBlock.position.set(x + .5, y - .1, z + .5)
                                        }
                                    }
                                }, 0, 2)
                                sender.server.scheduler.scheduleSyncDelayedTask(plugin, {
                                    sender.server.scheduler.cancelTask(taskId)
                                }, 100)
                            })
                    )
                    .withSubcommand(
                        CommandAPICommand("item")
                            .withPermission(CommandPermission.OP)
                            .withArguments(MultiLiteralArgument(*DiffusionItem.values().map { it.id  }.toTypedArray()))
                            .executesNative(NativeCommandExecutor { s, a ->
                                val str = (a[0] as String)
                                s.location.world.dropItem(s.location, DiffusionItem.values().find { it.id == str }!!.item)
                            })
                    )
            ).register()
    }
}
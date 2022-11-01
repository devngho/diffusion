package com.github.devngho.diffusion.papermc

import com.github.devngho.diffusion.core.game.Turn
import com.github.devngho.diffusion.core.map.Generator
import com.github.devngho.diffusion.papermc.event.PlayerEvent
import com.github.devngho.diffusion.papermc.game.PaperGame
import com.github.devngho.diffusion.papermc.item.DiffusionItem
import com.github.devngho.diffusion.papermc.map.MapRenderer
import com.github.devngho.diffusion.papermc.player.PaperPlayerImpl
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
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import dev.jorel.commandapi.arguments.BooleanArgument
import dev.jorel.commandapi.arguments.EntitySelector
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.arguments.MultiLiteralArgument
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import net.kyori.adventure.text.Component
import org.bukkit.plugin.java.JavaPlugin

class Plugin : JavaPlugin() {
    companion object {
        lateinit var plugin: JavaPlugin
        var game: PaperGame? = null
    }

    override fun onEnable() {
        super.onEnable()

        server.pluginManager.registerSuspendingEvents(PlayerEvent(), this)
    }

    override fun onLoad() {
        plugin = this

        super.onLoad()

        CommandAPICommand("diffusion")
            .withSubcommand(
                CommandAPICommand("start")
                    .withPermission(CommandPermission.OP)
                    .withArguments(EntitySelectorArgument<Collection<Player>>("player", EntitySelector.MANY_PLAYERS))
                    .executesPlayer(PlayerCommandExecutor { sender, args ->
                        val players = (args[0] as Collection<*>).toList().filterIsInstance<Player>().toMutableList()
                        game = PaperGame(players, sender.location, this, sender)
                    })
            )
            .withSubcommand(
                CommandAPICommand("turn")
                    .withPermission(CommandPermission.OP)
                    .executesNative(NativeCommandExecutor { _, _ ->
                        val g = game
                        if (g != null) {
                            launch {
                                Turn(g).processTurn(this)
                            }
                        }
                    })
            )
            .withSubcommand(
                CommandAPICommand("debug")
                    .withSubcommand(
                        CommandAPICommand("animals")
                            .withPermission(CommandPermission.OP)
                            .executesNative(NativeCommandExecutor { sender, _ ->
                                game?.run {
                                    this.animals.forEach {
                                        sender.sendMessage(Component.text("${it.uuid} : ${it.uuid} ${it.color} ${it.power} ${it.diffusion}"))
                                    }
                                }
                            })
                    )
                    .withSubcommand(
                        CommandAPICommand("generate")
                            .withPermission(CommandPermission.OP)
                            .executesNative(NativeCommandExecutor { sender, _ ->
                                val map = Generator.generate()

                                MapRenderer.render(map.map, sender.location.apply { y -= 1 })
                            })
                    )
                    .withSubcommand(
                        CommandAPICommand("skip")
                            .withPermission(CommandPermission.OP)
                            .executesPlayer(PlayerCommandExecutor { sender, _ ->
                                if (game != null){
                                    game?.player?.find { (it as PaperPlayerImpl).player.uniqueId == sender.uniqueId }?.run {
                                        (this as PaperPlayerImpl).skip = !this.skip
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
                    .withSubcommand(
                        CommandAPICommand("reload")
                            .withPermission(CommandPermission.OP)
                            .withArguments(BooleanArgument("show"))
                            .executesPlayer(PlayerCommandExecutor { s, args ->
                                game?.run {
                                    this.mapAnimalRender.itemFrameMap.flatten().forEach {
                                        if (!(args[0] as Boolean)) it.removePlayer(s)
                                        else it.addPlayer(s)
                                        it.updateMeta()
                                        // it.updateLocation()
                                    }
                                }
                            })
                    )
                    .withSubcommand(
                        CommandAPICommand("add")
                            .withPermission(CommandPermission.OP)
                            .withArguments(PlayerArgument("player"))
                            .executesNative(NativeCommandExecutor { _, a ->
                                val p = a[0] as Player
                                game?.bukkitPlayers?.add(p)
                            })
                    )
                    .withSubcommand(
                        CommandAPICommand("setdiffusion")
                            .withPermission(CommandPermission.OP)
                            .withArguments(MultiLiteralArgument(*(com.github.devngho.diffusion.core.util.Color.values().map { it.name })
                                .toTypedArray()), IntegerArgument("diffusion"), IntegerArgument("power"))
                            .executesNative(NativeCommandExecutor { _, a ->
                                val c = com.github.devngho.diffusion.core.util.Color.values().find { it.name == a[0] as String }
                                game?.animals?.find {
                                    it.color == c
                                }?.run {
                                    this.diffusion = a[1] as Int
                                    this.power = a[2] as Int
                                }
                            })
                    )
            ).register()
    }
}
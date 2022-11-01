package com.github.devngho.diffusion.papermc.game

import com.github.devngho.diffusion.core.game.Game
import com.github.devngho.diffusion.core.game.Point
import com.github.devngho.diffusion.papermc.map.MapAnimalRender
import com.github.devngho.diffusion.papermc.map.MapRenderer
import com.github.devngho.diffusion.papermc.player.PaperPlayerImpl
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import kotlin.math.round
import com.github.devngho.diffusion.papermc.util.plus
import net.kyori.adventure.text.format.TextColor

class PaperGame(val bukkitPlayers: MutableList<Player>, val mapPosition: Location, val plugin: JavaPlugin, val owner: Player) : Game(bukkitPlayers.map { PaperPlayerImpl(it) }) {
    val mapAnimalRender = MapAnimalRender(this)
    init {
        MapRenderer.render(map.map, mapPosition)

        player.filterIsInstance<PaperPlayerImpl>().forEach {
            it.game = this
        }
    }

    fun getPointByWorld(location: Location): Point{
        return location.apply {
            x  -= mapPosition.x - 0.5
            z -= mapPosition.z - 0.5
        }.run {
            Point(round(this.x).toInt(), round(this.z).toInt())
        }
    }

    override fun finalizeTurn(){
        bukkitPlayers.forEach {
            it.sendMessage(Component.text("diffusion").color(TextColor.color(0, 255, 0)) + (Component.text(" : ").color(TextColor.color(255, 255, 255)) + Component.text("턴 종료")))
        }

        mapAnimalRender.renderOrUpdate()
    }
}
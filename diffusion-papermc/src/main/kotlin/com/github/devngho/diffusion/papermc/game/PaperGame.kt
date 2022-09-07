package com.github.devngho.diffusion.papermc.game

import com.github.devngho.diffusion.core.game.Game
import com.github.devngho.diffusion.core.game.Point
import com.github.devngho.diffusion.papermc.map.MapAnimalRender
import com.github.devngho.diffusion.papermc.map.MapRenderer
import com.github.devngho.diffusion.papermc.player.PaperPlayerImpl
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class PaperGame(val bukkitPlayers: MutableList<Player>, val mapPosition: Location, val plugin: JavaPlugin) : Game(bukkitPlayers.map { PaperPlayerImpl(it) }) {
    val mapAnimalRender = MapAnimalRender(this)
    init {
        MapRenderer.render(map.map, mapPosition)

        player.filterIsInstance<PaperPlayerImpl>().forEach {
            it.game = this
        }
    }

    fun getPointByWorld(location: Location): Point{
        return location.apply {
            x - mapPosition.x
            z - mapPosition.z
        }.run {
            Point(this.blockX, this.blockZ)
        }
    }
}
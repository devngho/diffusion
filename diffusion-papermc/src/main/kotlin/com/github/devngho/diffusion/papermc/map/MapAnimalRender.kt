package com.github.devngho.diffusion.papermc.map

import com.github.devngho.diffusion.core.game.AnimalTile
import com.github.devngho.diffusion.core.map.Tile
import com.github.devngho.diffusion.papermc.game.PaperGame
import com.github.devngho.nplug.api.entity.Armorstand
import com.github.devngho.nplug.api.entity.Itemframe
import com.github.devngho.nplug.impl.entity.ArmorstandImpl
import com.github.devngho.nplug.impl.entity.ItemframeImpl
import com.github.devngho.nplug.impl.util.Direction
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class MapAnimalRender(private val game: PaperGame) {
    val itemFrameMap: MutableList<MutableList<Itemframe>> =
        MutableList(64) { MutableList(64) { Itemframe.createItemframe(game.mapPosition.clone(), game.plugin, game.bukkitPlayers, Direction.UP) } }

    private fun getTileItem(tile: Tile, animalTile: AnimalTile): Material{
        return when(tile.id){
            "mountain" -> {
                Material.BARRIER
            }
            else -> {
                val animal = animalTile.animal
                if (animal == null){
                    Material.WHITE_CONCRETE
                } else{
                    com.github.devngho.diffusion.papermc.util.Color.colorToConcrete(animal.color)
                }
            }
        }
    }

    fun renderOrUpdate(){
        game.map.map.forEachIndexed { x, list ->
            list.forEachIndexed { y, v ->
                val frame = itemFrameMap[x][y]

                (frame as ItemframeImpl).bukkitEntity.apply {
                    this.setItem(ItemStack(getTileItem(v, game.animalMap.map[x][y])))
                }

                frame.updateMeta()
            }
        }
    }

    init {

        game.map.map.forEachIndexed { x, list ->
            list.forEachIndexed { y, v ->
                val frame = itemFrameMap[x][y]

                frame.position.apply {
                    this.x += x
                    this.y += 5
                    this.z += y
                }

                (frame as ItemframeImpl).bukkitEntity.apply {
                    this.setItem(ItemStack(getTileItem(v, game.animalMap.map[x][y])))
                    isFixed = true
                    isVisible = false
                }

                frame.updateMeta()

                frame.updateLocation()
            }
        }

        renderOrUpdate()
    }
}
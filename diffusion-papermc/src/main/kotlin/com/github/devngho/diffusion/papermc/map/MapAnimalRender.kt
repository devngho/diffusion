package com.github.devngho.diffusion.papermc.map

import com.github.devngho.diffusion.core.game.AnimalTile
import com.github.devngho.diffusion.core.map.Tile
import com.github.devngho.diffusion.papermc.game.PaperGame
import com.github.devngho.nplug.api.entity.Itemframe
import com.github.devngho.nplug.impl.entity.ItemframeImpl
import com.github.devngho.nplug.impl.util.Direction
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class MapAnimalRender(val game: PaperGame) {
    val armorstandMap: MutableList<MutableList<Itemframe>> =
        MutableList(64) { MutableList(64) { Itemframe.createItemframe(game.mapPosition.clone(), game.plugin, game.bukkitPlayers, Direction.UP) } }

    fun getTileItem(tile: Tile, animalTile: AnimalTile): Material{
        return when(tile.id){
            "mountain" -> {
                Material.BARRIER
            }
            else -> {
                val animal = animalTile.animal
                if (animal == null){
                    Material.WHITE_CONCRETE
                } else{
                    com.github.devngho.diffusion.papermc.util.Color.ColorToConcrete(animal.color)
                }
            }
        }
    }

    fun renderOrUpdate(){
        game.map.map.forEachIndexed { x, list ->
            list.forEachIndexed { y, v ->
                val stand = armorstandMap[x][y]

                (stand as ItemframeImpl).bukkitEntity.apply {
                    this.setItem(ItemStack(getTileItem(v, game.animalMap.map[x][y])))
                }

                stand.updateMeta()
            }
        }
    }

    init {

        game.map.map.forEachIndexed { x, list ->
            list.forEachIndexed { y, v ->
                val stand = armorstandMap[x][y]

                stand.position.apply {
                    this.x += x
                    this.y += 5
                    this.z += y
                }

                (stand as ItemframeImpl).bukkitEntity.apply {
                    this.setItem(ItemStack(getTileItem(v, game.animalMap.map[x][y])))
                    isFixed = true
                    isVisible = false
                }

                stand.updateMeta()
            }
        }

        renderOrUpdate()
    }
}
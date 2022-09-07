package com.github.devngho.diffusion.papermc.map

import com.github.devngho.diffusion.core.map.Tile
import org.bukkit.Location
import org.bukkit.Material

object MapRenderer {
    fun render(map: MutableList<MutableList<Tile>>, origin: Location){
        map.forEachIndexed { x, list ->
            list.forEachIndexed { y, v ->
                val loc = origin.clone().apply {
                    this.x += x
                    this.z += y
                }

                when (v.id) {
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
    }
}
package com.github.devngho.diffusion.papermc.util

import com.github.devngho.diffusion.core.util.Color
import org.bukkit.Material

object Color {
    fun ColorToConcrete(color: Color): Material{
        return when(color){
            Color.WHITE -> Material.WHITE_CONCRETE
            Color.ORANGE -> Material.ORANGE_CONCRETE
            Color.MAGENTA -> Material.MAGENTA_CONCRETE
            Color.LIGHT_BLUE -> Material.LIGHT_BLUE_CONCRETE
            Color.YELLOW -> Material.YELLOW_CONCRETE
            Color.LIME -> Material.LIME_CONCRETE
            Color.PINK -> Material.PINK_CONCRETE
            Color.GRAY -> Material.GRAY_CONCRETE
            Color.LIGHT_GRAY -> Material.LIGHT_GRAY_CONCRETE
            Color.CYAN -> Material.CYAN_CONCRETE
            Color.PURPLE -> Material.PURPLE_CONCRETE
            Color.BLUE -> Material.BLUE_CONCRETE
            Color.BROWN -> Material.BROWN_CONCRETE
            Color.GREEN -> Material.GREEN_CONCRETE
            Color.RED -> Material.RED_CONCRETE
            Color.BLACK -> Material.BLACK_CONCRETE
        }
    }
}
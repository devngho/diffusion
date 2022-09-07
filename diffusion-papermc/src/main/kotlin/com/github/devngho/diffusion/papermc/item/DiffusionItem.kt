package com.github.devngho.diffusion.papermc.item

import de.tr7zw.nbtapi.NBTItem
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

enum class DiffusionItem {
    AnimalAddNewItem {
        override val id: String = "animal_add"
        override val itemName: String = "동물 생성"
        override val material: Material = Material.GREEN_DYE
        override fun onInteraction(event: PlayerInteractEvent) {
            event.isCancelled = true
        }
    },
    AnimalModifyItem {
        override val id: String = "animal_modify"
        override val itemName: String = "동물 변경"
        override val material: Material = Material.LIGHT_BLUE_DYE
        override fun onInteraction(event: PlayerInteractEvent) {
            event.isCancelled = true
        }
    }
    ;

    abstract val id: String
    abstract val itemName: String
    abstract val material: Material

    val item: ItemStack
    get() {
        return ItemStack(material).apply {
            editMeta {
                it.displayName(Component.text(itemName).color(TextColor.color(255, 255 , 255)).decoration(TextDecoration.ITALIC, false))
            }
            NBTItem(this).run {
                setString("diffusion", id)
                applyNBT(this@apply)
            }
        }
    }

    abstract fun onInteraction(event: PlayerInteractEvent)
}

fun ItemStack.toDiffusionItem(): DiffusionItem?{
    return DiffusionItem.values().find { it.id == NBTItem(this).getString("diffusion") }
}
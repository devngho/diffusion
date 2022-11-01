package com.github.devngho.diffusion.papermc.item

import com.github.devngho.diffusion.papermc.Plugin
import com.github.devngho.diffusion.papermc.player.PaperPlayerImpl
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
        override suspend fun onInteraction(event: PlayerInteractEvent) {
            Plugin.game?.run {
                player.find {
                    (it as PaperPlayerImpl).player.uniqueId == event.player.uniqueId
                }?.run {
                    (this as PaperPlayerImpl).onDiffusionItem(AnimalAddNewItem, event)
                }
            }
            event.isCancelled = true
        }
    },
    AnimalModifyItem {
        override val id: String = "animal_modify"
        override val itemName: String = "동물 변경"
        override val material: Material = Material.LIGHT_BLUE_DYE
        override suspend fun onInteraction(event: PlayerInteractEvent) {
            Plugin.game?.run {
                player.find {
                    (it as PaperPlayerImpl).player.uniqueId == event.player.uniqueId
                }?.run {
                    (this as PaperPlayerImpl).onDiffusionItem(AnimalModifyItem, event)
                }
            }
            event.isCancelled = true
        }
    },
    AnimalAddNewSmallItem {
        override val id: String = "animal_add_small"
        override val itemName: String = "작은 동물 | 고번식"
        override val material: Material = Material.WHITE_DYE
        override suspend fun onInteraction(event: PlayerInteractEvent) {
            Plugin.game?.run {
                player.find {
                    (it as PaperPlayerImpl).player.uniqueId == event.player.uniqueId
                }?.run {
                    (this as PaperPlayerImpl).onDiffusionItem(this@AnimalAddNewSmallItem, event)
                }
            }
            event.isCancelled = true
        }
    },
    AnimalAddNewBigItem {
        override val id: String = "animal_add_big"
        override val itemName: String = "큰 동물 | 균형"
        override val material: Material = Material.BLACK_DYE
        override suspend fun onInteraction(event: PlayerInteractEvent) {
            Plugin.game?.run {
                player.find {
                    (it as PaperPlayerImpl).player.uniqueId == event.player.uniqueId
                }?.run {
                    (this as PaperPlayerImpl).onDiffusionItem(this@AnimalAddNewBigItem, event)
                }
            }
            event.isCancelled = true
        }
    },
    ResetItem {
        override val id: String = "reset"
        override val itemName: String = "턴 초기화"
        override val material: Material = Material.RED_DYE
        override suspend fun onInteraction(event: PlayerInteractEvent) {
            Plugin.game?.run {
                player.find {
                    (it as PaperPlayerImpl).player.uniqueId == event.player.uniqueId
                }?.run {
                    (this as PaperPlayerImpl).onDiffusionItem(this@ResetItem, event)
                }
            }
            event.isCancelled = true
        }
    },
    TurnItem {
        override val id: String = "turn_item"
        override val itemName: String = "턴 진행"
        override val material: Material = Material.WHITE_DYE
        override suspend fun onInteraction(event: PlayerInteractEvent) {
            Plugin.game?.run {
                player.find {
                    (it as PaperPlayerImpl).player.uniqueId == event.player.uniqueId
                }?.run {
                    (this as PaperPlayerImpl).onDiffusionItem(this@TurnItem, event)
                }
            }
            event.isCancelled = true
        }
    },
    SkipItem {
        override val id: String = "skip_item"
        override val itemName: String = "스킵"
        override val material: Material = Material.ORANGE_DYE
        override suspend fun onInteraction(event: PlayerInteractEvent) {
            Plugin.game?.run {
                player.find {
                    (it as PaperPlayerImpl).player.uniqueId == event.player.uniqueId
                }?.run {
                    (this as PaperPlayerImpl).onDiffusionItem(this@SkipItem, event)
                }
            }
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

    abstract suspend fun onInteraction(event: PlayerInteractEvent)
}

fun ItemStack.toDiffusionItem(): DiffusionItem?{
    val k = NBTItem(this).getString("diffusion")
    return DiffusionItem.values().find { it.id == k }
}
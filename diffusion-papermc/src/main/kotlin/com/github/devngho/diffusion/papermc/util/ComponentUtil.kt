package com.github.devngho.diffusion.papermc.util

import net.kyori.adventure.text.TextComponent

operator fun TextComponent.plus(other: TextComponent): TextComponent {
    return this.append(other)
}
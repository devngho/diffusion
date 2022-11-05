// SPDX-License-Identifier: MIT

package com.github.devngho.diffusion.core.map

object Generator {
    private const val fq = 0.3

    fun generate(): Map{
        val noiseData = mutableListOf<MutableList<Double>>()

        for (i in 0..63) noiseData.add(mutableListOf())

        for (y in 0..63) {
            for (x in 0..63) {
                noiseData[y].add(SimplexNoise.noise(x.toDouble() * fq, y.toDouble() * fq))
            }
        }

        val map = Map(mutableListOf())

        for (i in 0..63) map.map.add(mutableListOf())

        noiseData.forEachIndexed { x, doubles ->
            doubles.forEachIndexed { _, v ->
                // println(v)
                map.map[x].add(if (v > 0.7) Tile.mountain else if(v > 0.4) Tile.hill else if (v > -0.4) Tile.normal else Tile.water)
            }
        }

        // println(map.map.map { it.map { i -> i.id.first() } }.joinToString(separator = "\n") { it.joinToString(" ")})
        return map
    }
}
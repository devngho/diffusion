package com.github.devngho.diffusion.core.map

/*
 * A speed-improved simplex noise algorithm for 2D, 3D and 4D in Java.
 *
 * Based on example code by Stefan Gustavson (stegu@itn.liu.se).
 * Optimisations by Peter Eastman (peastman@drizzle.stanford.edu).
 * Better rank ordering method by Stefan Gustavson in 2012.
 *
 * This could be speeded up even further, but it's useful as it is.
 *
 * Version 2012-03-09
 *
 * This code was placed in the public domain by its original author,
 * Stefan Gustavson. You may use it as you see fit, but
 * attribution is appreciated.
 *
 * 3D+4D implementations removed and other methods added by github.com/jrenner 2015-03-08
 *
 * Convert Java to Kotlin by Jetbrains Intellij IDEA
 *
 */
object SimplexNoise {
    // Simplex noise in 2D, 3D and 4D
    private val grad3 = arrayOf(
        Grad(1.0, 1.0, 0.0), Grad(-1.0, 1.0, 0.0), Grad(1.0, -1.0, 0.0), Grad(-1.0, -1.0, 0.0),
        Grad(1.0, 0.0, 1.0), Grad(-1.0, 0.0, 1.0), Grad(1.0, 0.0, -1.0), Grad(-1.0, 0.0, -1.0),
        Grad(0.0, 1.0, 1.0), Grad(0.0, -1.0, 1.0), Grad(0.0, 1.0, -1.0), Grad(0.0, -1.0, -1.0)
    )

    /*private static Grad grad4[]= {new Grad(0,1,1,1),new Grad(0,1,1,-1),new Grad(0,1,-1,1),new Grad(0,1,-1,-1),
			new Grad(0,-1,1,1),new Grad(0,-1,1,-1),new Grad(0,-1,-1,1),new Grad(0,-1,-1,-1),
			new Grad(1,0,1,1),new Grad(1,0,1,-1),new Grad(1,0,-1,1),new Grad(1,0,-1,-1),
			new Grad(-1,0,1,1),new Grad(-1,0,1,-1),new Grad(-1,0,-1,1),new Grad(-1,0,-1,-1),
			new Grad(1,1,0,1),new Grad(1,1,0,-1),new Grad(1,-1,0,1),new Grad(1,-1,0,-1),
			new Grad(-1,1,0,1),new Grad(-1,1,0,-1),new Grad(-1,-1,0,1),new Grad(-1,-1,0,-1),
			new Grad(1,1,1,0),new Grad(1,1,-1,0),new Grad(1,-1,1,0),new Grad(1,-1,-1,0),
			new Grad(-1,1,1,0),new Grad(-1,1,-1,0),new Grad(-1,-1,1,0),new Grad(-1,-1,-1,0)};*/
    private val p = (1..256).shuffled().map { it.toShort() }.toTypedArray()

    // To remove the need for index wrapping, double the permutation table length
    private val perm = ShortArray(512)
    private val permMod12 = ShortArray(512)

    init {
        for (i in 0..511) {
            perm[i] = p[i and 255]
            permMod12[i] = (perm[i] % 12).toShort()
        }
    }

    // Skewing and unskewing factors for 2, 3, and 4 dimensions
    private val F2 = 0.5 * (Math.sqrt(3.0) - 1.0)
    private val G2 = (3.0 - Math.sqrt(3.0)) / 6.0

    /*	private static final double F3 = 1.0/3.0;
	private static final double G3 = 1.0/6.0;
	private static final double F4 = (Math.sqrt(5.0)-1.0)/4.0;
	private static final double G4 = (5.0-Math.sqrt(5.0))/20.0;*/
    // This method is a *lot* faster than using (int)Math.floor(x)
    private fun fastfloor(x: Double): Int {
        val xi = x.toInt()
        return if (x < xi) xi - 1 else xi
    }

    private fun dot(g: Grad, x: Double, y: Double): Double {
        return g.x * x + g.y * y
    }

    /*	private static double dot(Grad g, double x, double y, double z) {
		return g.x*x + g.y*y + g.z*z; }
	private static double dot(Grad g, double x, double y, double z, double w) {
		return g.x*x + g.y*y + g.z*z + g.w*w; }*/
    // 2D simplex noise
    fun noise(xin: Double, yin: Double): Double {
        val n0: Double
        val n1: Double
        val n2: Double // Noise contributions from the three corners
        // Skew the input space to determine which simplex cell we're in
        val s = (xin + yin) * F2 // Hairy factor for 2D
        val i = fastfloor(xin + s)
        val j = fastfloor(yin + s)
        val t = (i + j) * G2
        val X0 = i - t // Unskew the cell origin back to (x,y) space
        val Y0 = j - t
        val x0 = xin - X0 // The x,y distances from the cell origin
        val y0 = yin - Y0
        // For the 2D case, the simplex shape is an equilateral triangle.
        // Determine which simplex we are in.
        val i1: Int
        val j1: Int // Offsets for second (middle) corner of simplex in (i,j) coords
        if (x0 > y0) {
            i1 = 1
            j1 = 0
        } // lower triangle, XY order: (0,0)->(1,0)->(1,1)
        else {
            i1 = 0
            j1 = 1
        } // upper triangle, YX order: (0,0)->(0,1)->(1,1)
        // A step of (1,0) in (i,j) means a step of (1-c,-c) in (x,y), and
        // a step of (0,1) in (i,j) means a step of (-c,1-c) in (x,y), where
        // c = (3-sqrt(3))/6
        val x1 = x0 - i1 + G2 // Offsets for middle corner in (x,y) unskewed coords
        val y1 = y0 - j1 + G2
        val x2 = x0 - 1.0 + 2.0 * G2 // Offsets for last corner in (x,y) unskewed coords
        val y2 = y0 - 1.0 + 2.0 * G2
        // Work out the hashed gradient indices of the three simplex corners
        val ii = i and 255
        val jj = j and 255
        val gi0 = permMod12[ii + perm[jj]].toInt()
        val gi1 = permMod12[ii + i1 + perm[jj + j1]].toInt()
        val gi2 = permMod12[ii + 1 + perm[jj + 1]].toInt()
        // Calculate the contribution from the three corners
        var t0 = 0.5 - x0 * x0 - y0 * y0
        if (t0 < 0) n0 = 0.0 else {
            t0 *= t0
            n0 = t0 * t0 * dot(grad3[gi0], x0, y0) // (x,y) of grad3 used for 2D gradient
        }
        var t1 = 0.5 - x1 * x1 - y1 * y1
        if (t1 < 0) n1 = 0.0 else {
            t1 *= t1
            n1 = t1 * t1 * dot(grad3[gi1], x1, y1)
        }
        var t2 = 0.5 - x2 * x2 - y2 * y2
        if (t2 < 0) n2 = 0.0 else {
            t2 *= t2
            n2 = t2 * t2 * dot(grad3[gi2], x2, y2)
        }
        // Add contributions from each corner to get the final noise value.
        // The result is scaled to return values in the interval [-1,1].
        return 70.0 * (n0 + n1 + n2)
    }

    /* fun generateSimplexNoise(
        startX: Int,
        startY: Int,
        width: Int,
        height: Int,
        frequency: Float,
        storage: Array<FloatArray>
    ) {
        var i = 0
        var x = startX
        while (x < startX + width) {
            var j = 0
            var y = startY
            while (y < startY + height) {
                storage[i][j] = noise((x * frequency).toDouble(), (y * frequency).toDouble()).toFloat()
                storage[i][j] = (storage[i][j] + 1) / 2 //generate values between 0 and 1
                j++
                y++
            }
            i++
            x++
        }
    } */

    // Inner class to speed upp gradient computations
    // (array access is a lot slower than member access)
    private class Grad  /*		Grad(double x, double y, double z, double w)
		{
			this.x = x;
			this.y = y;
			this.z = z;
			this.w = w;
		}*/(//double x, y, z, w;
        var x: Double, var y: Double, var z: Double
    )
}
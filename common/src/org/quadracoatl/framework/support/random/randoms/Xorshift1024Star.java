/**
 * Written in 2014 - 2015 by Sebastiano Vigna (vigna@acm.org)
 * 
 * To the extent possible under law, the author has dedicated all copyright
 * and related and neighboring rights to this software to the public domain
 * worldwide. This software is distributed without any warranty.
 * 
 * See <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package org.quadracoatl.framework.support.random.randoms;

/**
 * This is a port of the xorshift1024start PRNG, a fast and top-quality
 * generator.
 * 
 * @see <a href="http://xorshift.di.unimi.it/xorshift1024star.c">Original C
 *      source</a>
 */
public final class Xorshift1024Star {
	/** The index of the current state. */
	private int currentIndex = 0;
	/** The internal state of the generator. */
	private long[] state = new long[16];
	
	/**
	 * Creates a new instance of {@link Xorshift1024Star}.
	 *
	 * @param seed The seed.
	 */
	public Xorshift1024Star(long seed) {
		super();
		
		SplitMix64 seeder = new SplitMix64(seed);
		
		for (int index = 0; index < state.length; index++) {
			state[index] = seeder.next();
		}
	}
	
	/**
	 * Gets the next value from the generator.
	 *
	 * @return The next value.
	 */
	public final long next() {
		long state0 = state[currentIndex];
		
		currentIndex = (currentIndex + 1) & 15;
		
		long state1 = state[currentIndex];
		state1 = state1 ^ (state1 << 31);
		
		state[currentIndex] = state1 ^ state0 ^ (state1 >>> 11) ^ (state0 >>> 30);
		
		long result = state[currentIndex] * 1181783497276652981l;
		
		return result;
	}
}

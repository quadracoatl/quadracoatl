/**
 * Written in 2015 by Sebastiano Vigna (vigna@acm.org)
 * 
 * To the extent possible under law, the author has dedicated all copyright
 * and related and neighboring rights to this software to the public domain
 * worldwide. This software is distributed without any warranty.
 * 
 * See <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package org.quadracoatl.framework.support.randoms;

/**
 * This is a port of the splitmix64 PRNG, a fixed-increment version of Java 8's
 * SplittableRandom generator.
 * 
 * @see <a href="http://xorshift.di.unimi.it/splitmix64.c">Original C source</a>
 */
public final class SplitMix64 {
	/** The internal state of the generator. */
	private long state = 0l;
	
	/**
	 * Creates a new instance of {@link SplitMix64}.
	 *
	 * @param seed The seed.
	 */
	public SplitMix64(long seed) {
		super();
		
		state = seed;
	}
	
	/**
	 * Gets the next value from the generator.
	 * 
	 * @return The next value.
	 */
	public final long next() {
		state = state + 0x9e3779b97f4a7c15l;
		
		long result = state;
		result = (result ^ (result >>> 30)) * 0xbf58476d1ce4e5b9l;
		result = (result ^ (result >>> 27)) * 0x94d049bb133111ebl;
		result = result ^ (result >>> 31);
		
		return result;
	}
}

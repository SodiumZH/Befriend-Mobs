package net.sodiumstudio.nautils.math;

import java.util.Collection;
import java.util.Random;

public class RndUtil {
	
	protected static Random rnd = new Random();
	
	public static double rndRangedDouble(double min, double max)
	{
		return rnd.nextDouble() * (max - min) + min ;
	}

	public static float rndRangedFloat(float min, float max)
	{
		return rnd.nextFloat() * (max - min) + min ;
	}
	
	public static <T> T randomPick(T[] array)
	{
		return array[rnd.nextInt(array.length)];
	}

}

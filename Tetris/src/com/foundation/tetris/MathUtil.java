/**
 * 
 */
package com.foundation.tetris;

import java.util.Random;

/**
 * @author alexunder
 *
 */
public class MathUtil {
	
	public static void baseMatrixRotate90ClockWise( byte[] Matrix, int dim ) {
		if( Matrix == null ) {
			return;		  
		}

		int i;
		byte temp = 0;

		int start_index = 0;
		int end_index = dim - 1;

		for(;start_index < end_index; start_index++, end_index--)
		{
			for(i = start_index; i < end_index; i++)
			{
				temp = Matrix[i+start_index*dim];
				Matrix[i + start_index*dim] = Matrix[start_index + (end_index - i + start_index)*dim];
				Matrix[start_index + (end_index - i + start_index)*dim] = Matrix[(end_index - i + start_index) + end_index*dim];
				Matrix[(end_index - i + start_index) + end_index*dim]   = Matrix[end_index + i*dim];
				Matrix[end_index + i*dim]   = temp; 	
			}
		}
	}
	
	public static int generateRandomNumber(int start, int end) {
		if(start >= end) {
			return 0;
		}
		
		int num = end - start + 1;
		
		Random r = new Random(System.currentTimeMillis());
		int ret = r.nextInt(num) + start;
		return ret;
	}
}

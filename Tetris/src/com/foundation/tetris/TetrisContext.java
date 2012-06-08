/**
 * 
 */
package com.foundation.tetris;

import android.util.Log;

/**
 * @author alexunder
 *
 */
public class TetrisContext {
	private static final String TAG = "TetrisContext";
	
	private byte   mbDimension;
	private byte   mbXSize;
	private byte   mbYSize;
	private byte[] mSceneData;
	
	public TetrisContext() {
		mbDimension = 0;
		mbXSize = 0;
		mbYSize = 0;
		mSceneData = null;
	}
	
	public TetrisContext(int idimension, int iX, int iY) {
		mbDimension = (byte)idimension;
		mbXSize     = (byte)iX;
		mbYSize     = (byte)iY;
		
		int idatasize = mbXSize*mbYSize;
		mSceneData = new byte[idatasize];
		
		for(int i = 0; i < idatasize; i++) {
			mSceneData[i] = 0;
		}
	}
	
	public static TetrisContext CreateSceneContext( int idimension, int iX, int iY) {
		if(idimension != 2) {
			return null;
		}
		
		return new TetrisContext(idimension, iX, iY);
	}
	
	public int getXSize() {
		return (int)mbXSize;
	}
	
	public int getYSize() {
		return (int)mbYSize;
	}
	
	public void setPixel(byte value, int x, int y) {
		mSceneData[x + y*mbXSize] = value;
	}
	
	public byte getPixel(int x, int y) {
		return mSceneData[x + y*mbXSize];
	}
	
	public byte getLinearPoint(int index) {
		return mSceneData[index];
	}
	
	public byte[] getSceneData() {
		return mSceneData;
	}
	
	public void ClearSceneContext() {
		Log.v(TAG, "ClearSceneContext");
		int iSize = mbXSize*mbYSize;
		
		for(int i = 0; i < iSize; i++) {
			mSceneData[i] = 0;
		}
	}
	
	public void SyncFromOtherSceneData(TetrisContext srcContext) {
		byte [] srcSceneData = srcContext.getSceneData();
		
		for(int i = 0; i < mbXSize*mbYSize; i++) {
			mSceneData[i] = srcSceneData[i];
		}
	}
	
	public void dumpData() {
		Log.v(TAG, "dumpData");
		for(int i = 0; i < mbYSize; i++) {
			String line = "";
			for(int j = 0; j < mbXSize; j++) {
				line += mSceneData[j + i*mbXSize] + ",";
			}
			Log.v(TAG, line);
		}
	}
}

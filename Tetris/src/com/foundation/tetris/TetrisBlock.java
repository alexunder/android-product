/**
 * The 2nd Foundation studio 
 */
package com.foundation.tetris;

/**
 * @author alexunder
 *
 */
public class TetrisBlock {
	/*The enum constant for block category*/
	public  static final int BLOCK_CUBIC  = 0;
	public  static final int BLOCK_BAR    = 1;
	public  static final int BLOCK_LHOOK  = 2;
	public  static final int BLOCK_RHOOK  = 3;
	public  static final int BLOCK_MIDDLE = 4;
	
	public  static final int BLOCK_DATA_SIZE = 16;
	public  static final int BUFFER_SIZE = 10;
	
	protected int m_datasize;
	protected int m_istart_x;
	protected int m_istart_y;
	protected int m_iwidth_CompareDate;
	protected int m_iheight_CompareDate;
	protected int m_ienum_category;
	protected boolean m_bIs_falldown;
	
	protected byte[] mdata = new byte[BLOCK_DATA_SIZE];
	protected byte[] m_CompareData;
	
	public TetrisBlock() {
		m_istart_x = 0;
		m_istart_y = 0;
		m_datasize = 4;

		m_iwidth_CompareDate = 0;
		m_iheight_CompareDate = 0;
		m_ienum_category = BLOCK_CUBIC;
		m_bIs_falldown = false;
	}
	
	public void initBlock(byte[] data, int iwidth, int iheight, int category) {
		if (data == null ) {
			return;
		}

		int iSize = iwidth*iheight;
		m_CompareData = new byte[iSize];
		
		System.arraycopy(data, 0, m_CompareData, 0, iSize);
		
		m_iwidth_CompareDate = iwidth;
		m_iheight_CompareDate = iheight;
		m_ienum_category = category;

		m_bIs_falldown = false;
		m_istart_x = (iwidth - m_datasize)/2 ;
		m_istart_y = 0;

		for(int i = 0; i < BLOCK_DATA_SIZE; i++) {
			mdata[i] = 0;
		}
		
		switch ( m_ienum_category ) {
		case BLOCK_CUBIC: {
				mdata[5]  = 1;
				mdata[6]  = 1;
				mdata[9]  = 1;
				mdata[10] = 1;
			 }
			 break;
		case BLOCK_BAR: {
				 mdata[1]  = 2;
				 mdata[5]  = 2;
				 mdata[9]  = 2;
				 mdata[13] = 2;
			 }
			 break;
		case BLOCK_LHOOK: {
				 mdata[1]  = 3;
				 mdata[2]  = 3;
				 mdata[6]  = 3;
				 mdata[10] = 3;
			 }
			 break;
		case BLOCK_RHOOK: {
				mdata[1]  = 4;
				mdata[2]  = 4;
				mdata[5]  = 4;
				mdata[9]  = 4;
			}
			break;
		case BLOCK_MIDDLE: {
				 mdata[1]  = 5;
				 mdata[6]  = 5;
				 mdata[5]  = 5;
				 mdata[9]  = 5;
			 }
			 break;
		}
	}
	
	public void rotate() {
		if( m_ienum_category == BLOCK_CUBIC ) {
			return;
		}

		if( can_transform() ) {
			MathUtil.baseMatrixRotate90ClockWise(mdata, 4);
		}
	}
	
	public void move_right() {
		if ( can_move_right() ) {
			m_istart_x++;
		}
	}
	
	public void move_left() {
		if ( can_move_left() ) {
			m_istart_x--;
		}
	}
	
	public void fall_slow() {
		if ( is_not_down() ) {
			m_istart_y++;
		}
		else {
			m_bIs_falldown = true;
		}
	}
	
	public void draw( TetrisContext context ) {
		if (context == null) {
			return;
		}

		if ( m_istart_x >= context.getXSize()||
			 m_istart_x + m_datasize <= 0     ||
			 m_istart_y >= context.getYSize()) {
			return;
		}

		int i;
	    int j;

		int isrc_offset = 0;
		int ides_offset = 0;
	    int icopy_size  = 0;

		if ( m_istart_x < 0 ) {
			ides_offset = 0;
			isrc_offset = - m_istart_x;
			icopy_size = m_datasize + m_istart_x; 
		}
		else if ( m_istart_x + m_datasize >= context.getYSize() ) {
			ides_offset = m_istart_x;
			isrc_offset = 0;
			icopy_size = context.getYSize() - m_istart_x;
		}
		else {
			ides_offset = m_istart_x;
			isrc_offset = 0;
			icopy_size = m_datasize;
		}

		for ( i = 0; i < m_datasize; i++ ) {
			for ( j = 0; j < icopy_size; j++ ) {
				if ( mdata[isrc_offset+j+i*m_datasize] != 0 ) {
					context.setPixel(mdata[isrc_offset+j+i*m_datasize], ides_offset + j, m_istart_y + i);
				}
			} 
		}
	}
	
	public boolean isBlockDown() {
		return m_bIs_falldown;
	}
	
	/* The private method below */
	private boolean can_transform() {
		int i;
		if( m_istart_x >= 0 && m_istart_x + m_datasize <= m_iwidth_CompareDate - 1 ) {
			for(i = 0; i < m_datasize; i++) {
				if( m_CompareData[m_istart_x + i + (m_istart_y+3)*m_iwidth_CompareDate] != 0 ) {
					return false;
				}
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private boolean can_move_right() {
		int i;
		int [] index_buffer = new int[BUFFER_SIZE];
		
		for(i = 0; i < BUFFER_SIZE; i++) {
			index_buffer[i] = 0;
		}
	   
		int icount = 0;

		for (i = 0; i < BLOCK_DATA_SIZE; i++ ) {
			if ( (mdata[i] != 0   && 
				  mdata[i+1] == 0 &&
				  (i + 1)%m_datasize != 0) ||
				  ((i + 1)%m_datasize == 0 &&
				  mdata[i] != 0) ) {
				index_buffer[icount++] = i;
			}
		}

		for ( i = 0; i < icount; i++ ) {
			int idelta_x = index_buffer[i]%m_datasize;
			int idelta_y = index_buffer[i]/m_datasize;

			int iX_offset = m_istart_x + idelta_x;
			int iY_offset = m_istart_y + idelta_y;
			if ( iX_offset == m_iwidth_CompareDate - 1|| 
					m_CompareData[iX_offset + 1 + iY_offset*m_iwidth_CompareDate] != 0 ) {
				return false;
			}
		}

		return true;
	}

	boolean can_move_left() {
		int i;
		int [] index_buffer = new int[BUFFER_SIZE];
		
		for(i = 0; i < BUFFER_SIZE; i++) {
			index_buffer[i] = 0;
		}

		int icount = 0;

		for ( i = 0; i < BLOCK_DATA_SIZE; i++ ) {
			if ( (mdata[i] != 0  && 
				  mdata[i-1] == 0 &&
				  i%m_datasize != 0)||
				  (i%m_datasize == 0 &&
				   mdata[i] != 0 )) {
				index_buffer[icount++] = i;
			}
		}

		for ( i = 0; i < icount; i++ ) {
			int idelta_x = index_buffer[i]%m_datasize;
			int idelta_y = index_buffer[i]/m_datasize;

			int iX_offset = m_istart_x + idelta_x;
			int iY_offset = m_istart_y + idelta_y;
			if ( iX_offset == 0 || 
					m_CompareData[iX_offset - 1 + iY_offset*m_iwidth_CompareDate] != 0 ) {
				return false;
			}
		}

		return true;
	}
	
	private boolean is_not_down() {
			int i;
			int [] index_buffer = new int[BUFFER_SIZE];
			
			for(i = 0; i < BUFFER_SIZE; i++) {
				index_buffer[i] = 0;
			}
			
			int icount = 0;

			for ( i = 0; i < 16; i++ ) {
				
				if(i < 12) {
					if(mdata[i] != 0 && mdata[i + m_datasize] == 0) {
						index_buffer[icount++] = i;
					}
				} else {
					if(i/m_datasize == 3 && mdata[i] != 0) {
						index_buffer[icount++] = i;
					}
				}
			}

			for ( i = 0; i < icount; i++ ) {
				int idelta_x = index_buffer[i]%m_datasize;
				int idelta_y = index_buffer[i]/m_datasize;

				int iX_offset = m_istart_x + idelta_x;
				int iY_offset = m_istart_y + idelta_y;
				if ( iY_offset == m_iheight_CompareDate - 1 || 
						m_CompareData[iX_offset + (iY_offset + 1)*m_iwidth_CompareDate] != 0 ) {
						return false;
				}
			}

			return true;
	}
}

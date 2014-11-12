/*****************************************************************************
 **
 ** @author Lee Neuse (coder@humbleprogrammer.net)
 ** @since 1.0
 **
 **	---------------------------- [License] ----------------------------------
 **	This work is licensed under the Creative Commons Attribution-NonCommercial-
 **	ShareAlike 3.0 Unported License. To view a copy of this license, visit
 **				http://creativecommons.org/licenses/by-nc-sa/3.0/
 **	or send a letter to Creative Commons, 444 Castro Street Suite 900, Mountain
 **	View, California, 94041, USA.
 **	--------------------- [Disclaimer of Warranty] --------------------------
 **	There is no warranty for the program, to the extent permitted by applicable
 **	law.  Except when otherwise stated in writing the copyright holders and/or
 **	other parties provide the program “as is” without warranty of any kind,
 **	either expressed or implied, including, but not limited to, the implied
 **	warranties of merchantability and fitness for a particular purpose.  The
 **	entire risk as to the quality and performance of the program is with you.
 **	Should the program prove defective, you assume the cost of all necessary
 **	servicing, repair or correction.
 **	-------------------- [Limitation of Liability] --------------------------
 **	In no event unless required by applicable law or agreed to in writing will
 **	any copyright holder, or any other party who modifies and/or conveys the
 **	program as permitted above, be liable to you for damages, including any
 **	general, special, incidental or consequential damages arising out of the
 **	use or inability to use the program (including but not limited to loss of
 **	data or data being rendered inaccurate or losses sustained by you or third
 **	parties or a failure of the program to operate with any other programs),
 **	even if such holder or other party has been advised of the possibility of
 **	such damages.
 **
 ******************************************************************************/
package net.humbleprogrammer.humble;

import java.awt.*;
import java.awt.image.BufferedImage;

@SuppressWarnings( "unused" )
public class GfxUtil
	{

	//  -----------------------------------------------------------------------
	//	CONSTANTS
	//	-----------------------------------------------------------------------

	/** Normal margin, measured in pixels. */
	public static final int MARGIN       = 8;
	/** Wide margin, measured in pixels. */
	public static final int MARGIN_THICK = 12;
	/** Narrow margin, measured in pixels. */
	public static final int MARGIN_THIN  = 4;

	//  -----------------------------------------------------------------------
	//	PUBLIC METHODS
	//	-----------------------------------------------------------------------

	/**
	 * Resizes an image.
	 *
	 * @param image
	 * 	Image to resize.
	 * @param dim
	 * 	New size.
	 */
	public static BufferedImage scaleImage( BufferedImage image, final Dimension dim )
		{
		DBC.requireNotNull( image, "Image" );
		DBC.requireNotNull( dim, "Dimension" );

		assert dim.height > 0;
		assert dim.width > 0;
		/*
		**  CODE
        */
		BufferedImage imgResized = new BufferedImage( dim.width, dim.height,
													  BufferedImage.TRANSLUCENT );
		Graphics2D gfx = imgResized.createGraphics();

		try
			{
			gfx.addRenderingHints( new RenderingHints( RenderingHints.KEY_RENDERING,
													   RenderingHints.VALUE_RENDER_QUALITY ) );
			gfx.drawImage( image, 0, 0, dim.width, dim.height, null );
			}
		finally
			{
			gfx.dispose();
			}

		return imgResized;
		}

	}   /* end of class GfxUtil */

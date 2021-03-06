/*****************************************************************************
 **
 ** @since 1.0
 **
 ******************************************************************************/
package net.humbleprogrammer.e4.gui.helpers;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Map;
import java.util.WeakHashMap;
import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.humbleprogrammer.humble.DBC;
import net.humbleprogrammer.humble.GfxUtil;

@SuppressWarnings( "unused" )
public class ResourceManager
	{

	//  -----------------------------------------------------------------------
	//	STATIC DECLARATIONS
	//	-----------------------------------------------------------------------

	/** Logger */
	private static final Logger s_log = LoggerFactory.getLogger( ResourceManager.class );

	/** Cached images */
	private static final Map<String, BufferedImage> s_images = new WeakHashMap<>();

	//  -----------------------------------------------------------------------
	//	PUBLIC METHODS
	//	-----------------------------------------------------------------------

	/**
	 * Loads an image resource.
	 *
	 * @param strName
	 * 	Image name.
	 *
	 * @return Image if loaded; null on error.
	 */
	public static BufferedImage getImage( final String strName )
		{
		DBC.requireNotNull( strName, "Image Name" );

		if (s_images.containsKey( strName ))
			return s_images.get( strName );
		/*
		**	CODE
		*/
		BufferedImage image = null;
		String strPath = "resources/images/" + strName;

		try
			{
			final URL url = ClassLoader.getSystemClassLoader().getResource( strPath );

			if (url != null &&
				(image = ImageIO.read( url )) != null)
				{
				s_log.debug( "ResourceManager loaded image '{}'.", strName );
				s_images.put( strName, image );
				}
			}
		catch (Exception ex)
			{
			s_log.warn( String.format( "Failed to load image resource '%s'.", strName ), ex );
			}

		return image;
		}

	/**
	 * Loads an image resource.
	 *
	 * @param strName
	 * 	Image name.
	 * @param dim
	 * 	Desired size of image.
	 *
	 * @return Image if loaded; null on error.
	 */
	public static BufferedImage getScaledImage( final String strName, final Dimension dim )
		{
		DBC.requireNotNull( strName, "Image Name" );
		DBC.requireNotNull( dim, "Image Size" );
		/*
		**	CODE
		*/
		BufferedImage image = getImage( strName );

		return (image != null)
			   ? GfxUtil.scaleImage( image, dim, true )
			   : null;
		}
	}   /* end of class ResourceManager */
/*****************************************************************************
 **
 ** @author Lee Neuse (coder@humbleprogrammer.net)
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
 **	other parties provide the program "as is" without warranty of any kind,
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

/* ****************************************************************************
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
package net.humbleprogrammer.maxx.interfaces;

import net.humbleprogrammer.maxx.*;
import net.humbleprogrammer.maxx.pgn.PgnParser;

/**
 * The {@link IPgnListener} interface describes the behavior of listeners
 * attached to the {@link PgnParser} class must implement.
 */
public interface IPgnListener
	{
	/**
	 * A Numeric Annotation Glyph (NAG) has been parsed.
	 *
	 * @param strAnnotation
	 *            NAG, including the leading '$'.
	 */
	void onAnnotation( final String strAnnotation );

	/**
	 * A comment has been parsed.
	 *
	 * @param strComment
	 *            Comment text, excluding opening/closing markers.
	 */
	void onComment( final String strComment );

	/**
	 * A move has been parsed.
	 *
	 * @param strSAN
	 *            Move string.
	 * @param strSuffix
	 *            Optional suffix string.
	 * @return .T. if parsing should continue; .F. to abort parsing.
	 */
	boolean onMove( final String strSAN, final String strSuffix );

	/**
	 * A move number has been parsed.
	 *
	 * @param iMoveNumber
	 *            Move number.
	 * @return .T. if parsing should continue; .F. to abort parsing.
	 */
	boolean onMoveNumber( final int iMoveNumber );

	/**
	 * A move placeholder ("..") has been parsed.
	 *
	 * @return .T. if move placeholder is valid; .F. to abort parsing.
	 */
	boolean onMovePlaceholder();

	/**
	 * A null move ("--") has been parsed.
	 *
	 * @return .T. if parsing should continue; .F. to abort parsing.
	 */
	boolean onNullMove();

	/**
	 * A move number has been parsed.
	 *
	 * @param result
	 *            Result
	 * @return .T. if parsing should continue; .F. to abort parsing.
	 */
	boolean onResult( final Result result );

	/**
	 * A tag name/value pair has been parsed.
	 *
	 * @param strName
	 *            Tag name.
	 * @param strValue
	 *            Tag value.
	 * @return .T. if parsing should continue; .F. to abort parsing.
	 */
	boolean onTag( final String strName, final String strValue );

	/**
	 * A variation open marker '(' was parsed.
	 */
	void onVariationEnter();

	/**
	 * A variation close marker ')' was parsed.
	 */
	void onVariationExit();

	/**
	 * A new game is being started.
	 */
	void onGameStart();
	
	/**
	 * The current game is ending.
	 */
	void onGameOver();
	} /* end of interface IPgnListener */

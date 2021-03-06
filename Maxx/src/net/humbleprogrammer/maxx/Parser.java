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
package net.humbleprogrammer.maxx;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.humbleprogrammer.humble.StrUtil;

import static net.humbleprogrammer.maxx.Constants.*;

@SuppressWarnings( "WeakerAccess" )
public class Parser
	{

	//  -----------------------------------------------------------------------
	//	CONSTANTS
	//	-----------------------------------------------------------------------

	/** CR/LF sequence. */
	public static final String STR_CRLF = System.getProperty( "line.separator" );
	/** Placeholder in EPD/FEN strings. */
	public static final String STR_DASH = "-";

	/** Castling Queen-side, AKA "castling long". */
	public static final String STR_CASTLE_LONG  = "O-O-O";
	/** Castling King-side, AKA "castling short". */
	public static final String STR_CASTLE_SHORT = "O-O";


	/** Dash, hyphen, minus sign, etc. */
	protected static final char SYM_DASH  = '-';
	/** Marks a text literal. */
	protected static final char SYM_SPACE = ' ';
	/** Marks a text literal. */
	protected static final char SYM_QUOTE = '"';

	/** Characters allowed in a move. */
	protected static final String STR_MOVE = "abcdefgh12345678BKNQRx:=O-";

	/** Regular expression for whitespace or end of string. */
	protected static final String RX_EOS       = "(?:\\z|\\s+)";
	/** Regular expression for castling flags. */
	protected static final String RX_CASTLING  = "(-|[KkQq]{1,4})";
	/** Regular expression for an e.p. square. */
	protected static final String RX_EP_SQUARE = "(-|[a-h][36])";
	/** Regular expression for player color. */
	protected static final String RX_PLAYER    = "(w|b)";
	/** Regular expression for a board position. */
	protected static final String RX_POSITION  =
		"([BbKkNnQqRr1-8]{1,8}(?:/[BbKkNnPpQqRr1-8]{1,8}){6}/[BbKkNnQqRr1-8]{1,8})";

	/** Piece glyphs */
	private static final String PIECE_GLYPHS  = "PpNnBbRrQqKk";
	/** Player glyphs */
	private static final String PLAYER_GLYPHS = "wb";

	//  -----------------------------------------------------------------------
	//	STATIC DECLARATIONS
	//	-----------------------------------------------------------------------

	/** Last error encountered. */
	protected static String s_strError;

	/** FEN string pattern. */
	protected static final Pattern s_rxFEN = Pattern.compile
		(
			// group[1] -- position
			RX_POSITION +
			// group[2] -- player
			"\\s+" + RX_PLAYER +
			// group[3] -- castling flags
			"\\s+" + RX_CASTLING +
			// group[4] -- e.p. square
			"\\s+" + RX_EP_SQUARE +
			// group[5] -- half move clock
			"\\s+(0|[1-9]\\d*)" +
			// group[6] -- full move clock
			"\\s+([1-9]\\d*)" +
			// End of string or white space
			RX_EOS
		);

	//  -----------------------------------------------------------------------
	//	PUBLIC METHODS
	//	-----------------------------------------------------------------------

	/**
	 * Searches a string for a valid FEN string.
	 *
	 * @param strFEN
	 * 	String to search.
	 *
	 * @return {@link java.util.regex.Matcher} if found; null otherwise.
	 */
	public static Matcher matchFEN( final String strFEN )
		{
		if (StrUtil.isBlank( strFEN )) return null;
		//	-----------------------------------------------------------------
		Matcher match = s_rxFEN.matcher( strFEN );

		return match.lookingAt() ? match : null;
		}

	/**
	 * Gets the piece represented by a single character.
	 *
	 * @param ch
	 * 	Character (code point)
	 *
	 * @return {@link net.humbleprogrammer.maxx.Piece} if recognized; <c>EMPTY</c> otherwise.
	 */
	public static int pieceFromGlyph( int ch )
		{
		int iPos = PIECE_GLYPHS.indexOf( ch );

		return (iPos >= 0)
			   ? (iPos + MAP_W_PAWN)
			   : EMPTY;
		}

	/**
	 * Gets the character representing a piece.
	 *
	 * @param piece
	 * 	Piece.
	 *
	 * @return Character, or zero if piece is invalid.
	 */
	public static char pieceToGlyph( int piece )
		{
		return (piece >= MAP_W_PAWN && piece <= MAP_B_KING)
			   ? PIECE_GLYPHS.charAt( piece - MAP_W_PAWN )
			   : '\0';
		}

	/**
	 * Gets the human-readable string for a piece, i.e., "White Queen" or "Black Bishop".
	 *
	 * @param piece
	 * 	Piece.
	 *
	 * @return String, or empty string if piece is invalid.
	 */
	public static String pieceToString( int piece )
		{
		switch (piece)
			{
			case Piece.W_PAWN:
				return "White Pawn";
			case Piece.B_PAWN:
				return "Black Pawn";
			case Piece.W_BISHOP:
				return "White Bishop";
			case Piece.B_BISHOP:
				return "Black Bishop";
			case Piece.W_KNIGHT:
				return "White Knight";
			case Piece.B_KNIGHT:
				return "Black Knight";
			case Piece.W_ROOK:
				return "White Rook";
			case Piece.B_ROOK:
				return "Black Rook";
			case Piece.W_QUEEN:
				return "White Queen";
			case Piece.B_QUEEN:
				return "Black Queen";
			case Piece.W_KING:
				return "White King";
			case Piece.B_KING:
				return "Black King";
			}

		return "";
		}

	/**
	 * Gets the piece type represented by a single character.
	 *
	 * @param ch
	 * 	Character (code point)
	 *
	 * @return Piece type [PAWN..KING] if recognized; <c>INVALID</c> otherwise.
	 */
	public static int pieceTypeFromGlyph( int ch )
		{
		int iPos = PIECE_GLYPHS.indexOf( ch );

		return (iPos >= 0)
			   ? ((iPos + MAP_W_PAWN) >> 1)
			   : INVALID;
		}

	/**
	 * Gets the piece type represented by a single character.
	 *
	 * @param pt
	 * 	Piece type [PAWN..KING]
	 *
	 * @return Character representing the piece.
	 */
	public static char pieceTypeToGlyph( int pt )
		{
		return (pt >= PAWN && pt <= KING)
			   ? PIECE_GLYPHS.charAt( (pt - 1) << 1 )
			   : 0;
		}

	/**
	 * Gets the player represented by a single character.
	 *
	 * @param ch
	 * 	Character (code point)
	 *
	 * @return [WHITE|BLACK] if recognized; <c>INVALID</c> otherwise.
	 */
	public static int playerFromGlyph( final int ch )
		{
		int iPos = PLAYER_GLYPHS.indexOf( ch );

		return (iPos >= 0) ? iPos : INVALID;
		}

	/**
	 * Gets the glyph for a player color.
	 *
	 * @param player
	 * 	Player color [WHITE|BLACK]
	 *
	 * @return Single character if recognized; zero otherwise.
	 */
	public static char playerToGlyph( int player )
		{
		return (player == WHITE || player == BLACK)
			   ? PLAYER_GLYPHS.charAt( player )
			   : '\0';
		}

	/**
	 * Gets the label for a player color.
	 *
	 * @param player
	 * 	Player color [WHITE|BLACK]
	 *
	 * @return String if recognized; null otherwise.
	 */
	public static String playerToString( int player )
		{
		if (player == WHITE)
			return "White";

		if (player == BLACK)
			return "Black";

		return "";
		}

	//  -----------------------------------------------------------------------
	//	PUBLIC GETTERS & SETTERS
	//	-----------------------------------------------------------------------

	/**
	 * Gets the language setting for the parser.
	 *
	 * @return Language code.
	 */
	public static String getLanguage()
		{
		return "en-US";
		}

	}   /* end of class Parser */

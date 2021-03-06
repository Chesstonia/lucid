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

import net.humbleprogrammer.TestBase;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;
import static net.humbleprogrammer.maxx.Constants.*;

public class TestParser extends TestBase
    {

    //  -----------------------------------------------------------------------
    //	UNIT TESTS
    //	-----------------------------------------------------------------------

    @Test
    public void t_getLanguage()
        {
        assertEquals( "en-US", Parser.getLanguage() );
        }

    @Test
    public void t_matchFEN()
        {
        final Collection<String> listFEN = getFEN();

        for ( String strFEN : listFEN )
            assertNotNull( strFEN, Parser.matchFEN( strFEN ) );
        }

    @Test
    public void t_matchFEN_fail()
        {
        assertNull( Parser.matchFEN( null ) );
        assertNull( Parser.matchFEN( "" ) );
        }

    @Test
    public void t_pieceTypeToGlyph()
        {
        assertEquals( 'P', Parser.pieceTypeToGlyph(  PAWN ) );
        assertEquals( 'N', Parser.pieceTypeToGlyph( KNIGHT ) );
        assertEquals( 'B', Parser.pieceTypeToGlyph( BISHOP ) );
        assertEquals( 'R', Parser.pieceTypeToGlyph( ROOK ) );
        assertEquals( 'Q', Parser.pieceTypeToGlyph( QUEEN ) );
        assertEquals( 'K', Parser.pieceTypeToGlyph( KING ) );
        }

    @Test
    public void t_pieceToGlyph()
        {
        assertEquals( 'P', Parser.pieceToGlyph(  Piece.W_PAWN ) );
        assertEquals( 'p', Parser.pieceToGlyph(  Piece.B_PAWN ) );
        assertEquals( 'N', Parser.pieceToGlyph(  Piece.W_KNIGHT ) );
        assertEquals( 'n', Parser.pieceToGlyph(  Piece.B_KNIGHT ) );
        assertEquals( 'B', Parser.pieceToGlyph(  Piece.W_BISHOP ) );
        assertEquals( 'b', Parser.pieceToGlyph(  Piece.B_BISHOP ) );
        assertEquals( 'R', Parser.pieceToGlyph(  Piece.W_ROOK ) );
        assertEquals( 'r', Parser.pieceToGlyph(  Piece.B_ROOK ) );
        assertEquals( 'Q', Parser.pieceToGlyph(  Piece.W_QUEEN ) );
        assertEquals( 'q', Parser.pieceToGlyph(  Piece.B_QUEEN ) );
        assertEquals( 'K', Parser.pieceToGlyph(  Piece.W_KING ) );
        assertEquals( 'k', Parser.pieceToGlyph(  Piece.B_KING ) );
        }

    @Test
    public void t_pieceToGlyph_fail_low()
        {
        for (int idx = SQ_LO; idx < Piece.FIRST; ++idx)
        	assertEquals( '\0', Parser.pieceToGlyph(  idx ) );
        }

    @Test
    public void t_pieceToGlyph_fail_high()
        {
        for (int idx = Piece.LAST + 1; idx < SQ_HI; ++idx)
        	assertEquals( '\0', Parser.pieceToGlyph(  idx ) );
        }

    @Test
    public void t_playerToGlyph()
        {
        assertEquals( 'w', Parser.playerToGlyph(  WHITE ) );
        assertEquals( 'b', Parser.playerToGlyph(  BLACK ) );
        }

    @Test
    public void t_playerToGlyph_fail_low()
        {
        for (int idx = SQ_LO; idx < WHITE; ++idx)
        	assertEquals( '\0', Parser.playerToGlyph(  idx ) );
        }

    @Test
    public void t_playerToGlyph_fail_high()
        {
        for (int idx = BLACK + 1; idx < SQ_HI; ++idx)
        	assertEquals( '\0', Parser.playerToGlyph(  idx ) );
        }

    //  -----------------------------------------------------------------------
    //	PUBLIC METHODS
    //	-----------------------------------------------------------------------

    @BeforeClass
    public static void setup()
        {
        String strLang = Parser.getLanguage();

        assertNotNull( strLang );
        assertTrue( strLang.equalsIgnoreCase( "en-US" ) );
        }
    }   /* end of class TestParser */

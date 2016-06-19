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
import org.junit.Test;

import static net.humbleprogrammer.maxx.Constants.*;
import static org.junit.Assert.assertEquals;

public class TestPiece extends TestBase
    {
    //  -----------------------------------------------------------------------
    //	UNIT TESTS
    //	-----------------------------------------------------------------------

    @Test
    public void t_getColor()
        {
        assertEquals( WHITE, Piece.getColor( Piece.W_PAWN ) );
        assertEquals( BLACK, Piece.getColor( Piece.B_PAWN ) );
        assertEquals( WHITE, Piece.getColor( Piece.W_KNIGHT ) );
        assertEquals( BLACK, Piece.getColor( Piece.B_KNIGHT ) );
        assertEquals( WHITE, Piece.getColor( Piece.W_BISHOP ) );
        assertEquals( BLACK, Piece.getColor( Piece.B_BISHOP ) );
        assertEquals( WHITE, Piece.getColor( Piece.W_ROOK ) );
        assertEquals( BLACK, Piece.getColor( Piece.B_ROOK ) );
        assertEquals( WHITE, Piece.getColor( Piece.W_QUEEN ) );
        assertEquals( BLACK, Piece.getColor( Piece.B_QUEEN ) );
        assertEquals( WHITE, Piece.getColor( Piece.W_KING ) );
        assertEquals( BLACK, Piece.getColor( Piece.B_KING ) );
        }

    @Test
    public void t_type()
        {
        assertEquals( PAWN, Piece.getType( Piece.W_PAWN ) );
        assertEquals( PAWN, Piece.getType( Piece.B_PAWN ) );
        assertEquals( KNIGHT, Piece.getType( Piece.W_KNIGHT ) );
        assertEquals( KNIGHT, Piece.getType( Piece.B_KNIGHT ) );
        assertEquals( BISHOP, Piece.getType( Piece.W_BISHOP ) );
        assertEquals( BISHOP, Piece.getType( Piece.B_BISHOP ) );
        assertEquals( ROOK, Piece.getType( Piece.W_ROOK ) );
        assertEquals( ROOK, Piece.getType( Piece.B_ROOK ) );
        assertEquals( QUEEN, Piece.getType( Piece.W_QUEEN ) );
        assertEquals( QUEEN, Piece.getType( Piece.B_QUEEN ) );
        assertEquals( KING, Piece.getType( Piece.W_KING ) );
        assertEquals( KING, Piece.getType( Piece.B_KING ) );
        }
    }   /* end of class TestPiece */

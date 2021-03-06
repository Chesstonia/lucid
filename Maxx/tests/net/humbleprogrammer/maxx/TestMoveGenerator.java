/* ****************************************************************************
 *
 *	@author Lee Neuse (coder@humbleprogrammer.net)
 *	@since 1.0
 *
 *	---------------------------- [License] ----------------------------------
 *	This work is licensed under the Creative Commons Attribution-NonCommercial-
 *	ShareAlike 3.0 Unported License. To view a copy of this license, visit
 *			http://creativecommons.org/licenses/by-nc-sa/3.0/
 *	or send a letter to Creative Commons, 444 Castro Street Suite 900, Mountain
 *	View, California, 94041, USA.
 *	--------------------- [Disclaimer of Warranty] --------------------------
 *	There is no warranty for the program, to the extent permitted by applicable
 *	law.  Except when otherwise stated in writing the copyright holders and/or
 *	other parties provide the program "as is" without warranty of any kind,
 *	either expressed or implied, including, but not limited to, the implied
 *	warranties of merchantability and fitness for a particular purpose.  The
 *	entire risk as to the quality and performance of the program is with you.
 *	Should the program prove defective, you assume the cost of all necessary
 *	servicing, repair or correction.
 *	-------------------- [Limitation of Liability] --------------------------
 *	In no event unless required by applicable law or agreed to in writing will
 *	any copyright holder, or any other party who modifies and/or conveys the
 *	program as permitted above, be liable to you for damages, including any
 *	general, special, incidental or consequential damages arising out of the
 *	use or inability to use the program (including but not limited to loss of
 *	data or data being rendered inaccurate or losses sustained by you or third
 *	parties or a failure of the program to operate with any other programs),
 *	even if such holder or other party has been advised of the possibility of
 *	such damages.
 *
 ******************************************************************************/
package net.humbleprogrammer.maxx;

import net.humbleprogrammer.humble.*;

import org.junit.*;

import java.util.concurrent.TimeUnit;

import static net.humbleprogrammer.maxx.Constants.*;
import static org.junit.Assert.*;

public class TestMoveGenerator extends net.humbleprogrammer.TestBase
	{

	//  -----------------------------------------------------------------------
	//	STATIC DECLARATIONS
	//	-----------------------------------------------------------------------

	/** Total number of moves generated. */
	private static long s_lNetMoves    = 0L;
	/** Total number of nanoseconds spent generating moves. */
	private static long s_lNetNanosecs = 0L;

	//  -----------------------------------------------------------------------
	//	UNIT TESTS
	//	-----------------------------------------------------------------------

	@Test
	public void t_perft_black()
		{
		for ( TestPosition position : s_positions )
			s_lNetNanosecs += test( position, s_iMaxDepth, BLACK );
		}

	@Test
	public void t_perft_white()
		{
		for ( TestPosition position : s_positions )
			s_lNetNanosecs += test( position, s_iMaxDepth, WHITE );
		}

	//  -----------------------------------------------------------------------
	//	METHODS
	//	-----------------------------------------------------------------------

	@BeforeClass
	public static void setup()
		{
		s_lNetMoves = s_lNetNanosecs = 0L;
		}

	@AfterClass
	public static void teardown()
		{
		long lMillisecs;

		if (s_lNetMoves > 0L &&
			(lMillisecs = TimeUnit.NANOSECONDS.toMillis( s_lNetNanosecs )) > 0)
			{
			s_log.info( String.format( "%s: MoveList generated %,d moves in %s (%,d mps)",
									   DURATION.toString(),
									   s_lNetMoves,
									   TimeUtil.formatMillisecs( lMillisecs, true ),
									   (s_lNetMoves * 1000) / lMillisecs ) );
			}
		}

	//  -----------------------------------------------------------------------
	//	IMPLEMENTATION
	//	-----------------------------------------------------------------------

	private long test( TestPosition position, int iMaxDepth, int player )
		{
		assert position != null;
		assert iMaxDepth >= 0;
		assert player == WHITE || player == BLACK;
		//	-------------------------------------------------------------
		Stopwatch swatch = Stopwatch.startNew();

		int iDepth = position.test( player, iMaxDepth );

		swatch.stop();

		//	Compare the actual results to the expected results.
		assertArrayEquals( position.getFEN( player ),
						   position.getExpected( iDepth ), position.getActual( iDepth ) );

		//	Add add the actual move counts
		for ( long count : position.getActual( iDepth ) )
			s_lNetMoves += count;

		return swatch.getElapsed();
		}
	} /* end of unit test class TestMoveGenerator */

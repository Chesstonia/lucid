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
package net.humbleprogrammer.toolbox;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import net.humbleprogrammer.humble.*;
import net.humbleprogrammer.maxx.*;
import net.humbleprogrammer.maxx.factories.BoardFactory;
import net.humbleprogrammer.maxx.factories.MoveFactory;
import net.humbleprogrammer.maxx.interfaces.IPgnListener;
import net.humbleprogrammer.maxx.pgn.*;

public class Checker extends ToolboxApp
	{
	//  -----------------------------------------------------------------------
	//	CONSTANTS
	//	-----------------------------------------------------------------------
	private static final int STOP_AFTER = 25;

	//  -----------------------------------------------------------------------
	//	DECLARATIONS
	//	-----------------------------------------------------------------------

	/** Array of PGN files. */
	private final List<Path> _listPGN;

	/** Number of errors found so far. */
	private int _iFound;

	//  -----------------------------------------------------------------------
	//	CTOR
	//	-----------------------------------------------------------------------

	/**
	 * Default CTOR.
	 *
	 * @param strArgs
	 * 	Command-line arguments.
	 */
	private Checker( String[] strArgs )
		{
		assert strArgs != null;
		//	-----------------------------------------------------------------
		String strPath = (strArgs.length > 0) ? strArgs[ 0 ] : "P:\\Chess\\PGN\\TWIC";

		_listPGN = getPGN( strPath );

		printLine( "Found %,d *.pgn %s",
				   _listPGN.size(),
				   StrUtil.pluralize( _listPGN.size(), "file", null ) );

		if (_listPGN.isEmpty()) throw new RuntimeException( "No *.pgn files found." );
		}

	//  -----------------------------------------------------------------------
	//	PUBLIC METHODS
	//	-----------------------------------------------------------------------

	/**
	 * Entry point for the application.
	 *
	 * @param strArgs
	 * 	Command-line parameters.
	 */
	public static void main( String[] strArgs )
		{
		try
			{
			new Checker( strArgs ).run( STOP_AFTER );
			}
		catch (Exception ex)
			{
			s_log.warn( "Caught fatal exception.", ex );
			}
		}

	//  -----------------------------------------------------------------------
	//	IMPLEMENTATION
	//	-----------------------------------------------------------------------
	private int _iLongest = 0;

	private void display( Board bd, List<Move> moves )
		{
		if (bd == null || moves.size() <= _iLongest) return;
		//	-----------------------------------------------------------------
		_iFound++;
		_iLongest = moves.size();

		print( BoardFactory.exportEPD( bd ) );
		print( "; bm" );
		for ( Move move : moves )
			print( ' ' + MoveFactory.toSAN( bd, move, true ) );

		printLine( "" );

		moves.clear();
		}

	private void run( int iMaxCount )
		{
		assert iMaxCount >= 0;
		//	-----------------------------------------------------------------
		try
			{
			IPgnListener listener = new CheckerListener();

			for ( Path path : _listPGN )
				{
				String strPGN;

				try (PgnReader pgn = new PgnReader( new FileReader( path.toFile() ) ))
					{
					printLine( "# " + path.toString() );

					for ( int iGames = 0; (strPGN = pgn.readGame()) != null; ++iGames )
						if (PgnParser.parse( listener, strPGN ))
							{
							if (iMaxCount > 0 && ++_iFound >= iMaxCount)
								{
								printLine( "Stopped after %,d %s.",
										   _iFound,
										   StrUtil.pluralize( _iFound, "position", null ) );
								return;
								}
							}
						else
							{
							printLine( "Game #%,d:", (iGames + 1) );
							printLine( strPGN );
							printLine( PgnParser.getLastError() );
							printLine( "" );
							}

					}
				}
			}
		catch (IOException ex)
			{
			s_log.error( ex.getMessage() );
			}
		}

	//  -----------------------------------------------------------------------
	//	NESTED CLASS: CheckerListener
	//	-----------------------------------------------------------------------

	private class CheckerListener extends PgnValidator
		{
		/**
		 * A move has been parsed.
		 *
		 * @param strSAN
		 * 	Move string.
		 * @param strSuffix
		 * 	Optional suffix string.
		 *
		 * @return .T. if parsing is to continue; .F. to abort parsing.
		 */
		@Override
		public boolean onMove( final String strSAN, final String strSuffix )
			{
			if (!super.onMove( strSAN, strSuffix )) return false;
			//	-------------------------------------------------------------
			final Board bd = new Board( _pv.getCurrentPosition() );
			final List<Move> checks = new ArrayList<>();
			final MoveList moves = MoveList.generate( bd );

			for ( Move mv : moves )
				{
				Board bdNew = new Board( bd );

				bdNew.makeMove( mv );
				if (Arbiter.isInCheck( bdNew ))
					checks.add( mv );
				}

			if (!checks.isEmpty())
				display( bd, checks );

			return true;
			}
		}
	} /* end of class Checker */

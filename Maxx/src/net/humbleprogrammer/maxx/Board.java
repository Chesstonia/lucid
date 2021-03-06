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

import net.humbleprogrammer.humble.BitUtil;
import net.humbleprogrammer.humble.DBC;
import net.humbleprogrammer.maxx.exceptions.IllegalMoveException;
import net.humbleprogrammer.maxx.factories.BoardFactory;

import java.util.Arrays;

import static net.humbleprogrammer.maxx.Constants.*;

/**
 * The Board entity is primarily responsible for keeping track of the game in progress.
 *
 * It must be able to verify that the current position is valid, and make/unmake moves.  It must
 * also be able to determine whether or not the moving player is in check.  This requires
 * maintaining the following information: <ul> <li>location of each piece</li> <li>which player
 * is "on the move"</li> <li>whether or not castling moves are possible</li> <li>if an en
 * passant capture is possible.</li> </ul>
 *
 * Conceptually, the Board represents a snapshot of the game in progress, and as such does not
 * maintain historical information: each new position completely supersedes it predecessors.
 * Consequently, the Board is not able to 'undo' or 'redo' moves, nor is it able to detect
 * repetition of moves or positions.
 *
 * Since a 'half move clock' is maintained, it is possible to detect games drawn by the '50-move
 * rule'.  Games drawn by insufficient material can also be detected.
 *
 * The Board entity can determine: <ul> <li>If a square on the board is empty or occupied</li>
 * <li>The piece (color and type) on an occupied square</li> <li>If the current position is
 * legal within the rules of chess</li> <li>If there is an en passant capture possible, and if
 * so, on which square</li> <li>The color of the player "on the move" (and "off the move").</li>
 * </ul> The Board entity <b>cannot</b> determine: <ul> <li>What the previous move was*</li>
 * <li>How many moves have been made so far by either player</li> <li>How many times the current
 * position has occurred</li> <li>If the game has reached a verdict.</li> </ul> * pawn advances
 * can be inferred by the existence of an e.p. square
 */
@SuppressWarnings( "WeakerAccess" )
public class Board
	{

	//  -----------------------------------------------------------------------
	//	STATIC DECLARATIONS
	//	-----------------------------------------------------------------------

	/** Castling flags, by square. */
	private static final int[] s_castling = new int[ 64 ];

	//  -----------------------------------------------------------------------
	//	DECLARATIONS
	//	-----------------------------------------------------------------------

	/** Array of bitboards. */
	final long[] map = new long[ MAP_LENGTH ];

	/** Array of pieces */
	private final int[] _sq = new int[ 64 ];

	/** Current castling privileges. */
	private int _castling   = CastlingFlags.NONE;
	/** Color of player currently "on the move" */
	private int _player     = WHITE;
	/** Move number, which starts at one and increments after Black's move. */
	private int _iFullMoves = 1;
	/** Half move clock, which is the number of moves since the last capture or pawn move. */
	private int _iHalfMoves;
	/** En passant square in 8x8 format, or <c>INVALID</c> if e.p. not possible. */
	private int _iSqEP = INVALID;

	/** Bitboard of opposing pieces that check the moving player's King. */
	private long _bbCheckers = Bitboards.INVALID;

	/** Zobrist hash of castling privileges, e.p. square, and moving player. */
	private long _hashExtra  = HASH_BLANK;
	/** Zobrist hash of pawn position. */
	private long _hashPawns  = HASH_BLANK;
	/** Zobrist hash of piece positions. */
	private long _hashPieces = HASH_BLANK;

	//  -----------------------------------------------------------------------
	//	CTOR
	//	-----------------------------------------------------------------------

	static
		{
		Arrays.fill( s_castling, CastlingFlags.ALL );

		s_castling[ Square.A1 ] &= ~CastlingFlags.WHITE_LONG;
		s_castling[ Square.E1 ] &= ~CastlingFlags.WHITE_BOTH;
		s_castling[ Square.H1 ] &= ~CastlingFlags.WHITE_SHORT;

		s_castling[ Square.A8 ] &= ~CastlingFlags.BLACK_LONG;
		s_castling[ Square.E8 ] &= ~CastlingFlags.BLACK_BOTH;
		s_castling[ Square.H8 ] &= ~CastlingFlags.BLACK_SHORT;
		}

	/**
	 * Default CTOR for the {@link Board} class.
	 */
	public Board()
		{
		/*
		**  EMPTY CTOR
        */
		}

	/**
	 * Copy CTOR for the {@link Board} class.
	 *
	 * @param src
	 * 	Board to copy from.
	 */
	public Board( Board src )
		{
		DBC.requireNotNull( src, "Board" );
		//	-----------------------------------------------------------------
		copyFrom( src );
		}

	/**
	 * Alternate CTOR for the {@link Board} class.
	 *
	 * @param src
	 * 	Board to copy from.
	 * @param move
	 * 	Move to apply.
	 */
	Board( Board src, Move move )
		{
		assert src != null;
		assert move != null;
		//	-----------------------------------------------------------------
		copyFrom( src );
		makeMove( move );
		}

	/**
	 * Alternate CTOR for the {@link Board} class.
	 *
	 * @param src
	 * 	Board to copy from.
	 * @param packed
	 * 	Packed move to apply.
	 */
	Board( Board src, int packed )
		{
		assert src != null;
		//	-----------------------------------------------------------------
		copyFrom( src );
		applyMove( Move.unpackFromSq( packed ),
				   Move.unpackToSq( packed ),
				   Move.unpackType( packed ) );
		}

	//  -----------------------------------------------------------------------
	//	OVERRIDES
	//	-----------------------------------------------------------------------

	public boolean equals( Board bd )
		{
		return (bd != null &&
				bd._iFullMoves == _iFullMoves &&
				bd._iHalfMoves == _iHalfMoves &&
				bd.getZobristHash() == getZobristHash());
		}

	@Override
	public boolean equals( Object obj )
		{
		return ((obj instanceof Board) && equals( (Board) obj ));
		}

	@Override
	public int hashCode()
		{
		long hash = getZobristHash();

		return _iFullMoves ^
			   _iHalfMoves ^
			   ((397 * (int) (hash >>> 32)) ^ (int) (hash & 0xFFFFFFFFL));
		}

	/**
	 * Creates a Forsth-Edwards Notation (FEN) string for the board.
	 *
	 * @return FEN string.
	 */
	@Override
	public String toString()
		{
		return BoardFactory.toString( this );
		}

	//  -----------------------------------------------------------------------
	//	PUBLIC METHODS
	//	-----------------------------------------------------------------------

	/**
	 * Computes the ply from a move number and player.
	 *
	 * @param iMoveNum
	 * 	Move number (starts at 1).
	 * @param player
	 * 	Moving player [WHITE|BLACK].
	 *
	 * @return Zero-based ply number, or INVALID if move number is less than 1.
	 */
	public static int computePly( final int iMoveNum, final int player )
		{
		return (iMoveNum >= 1)
			   ? (((iMoveNum - 1) << 1) + (player & 1))
			   : INVALID;
		}

	/**
	 * Tests a move for legality against the current position.
	 *
	 * @param move
	 * 	Move to test.
	 *
	 * @return <code>.T.</code> if move is legal; <code>.F.</code> otherwise.
	 */
	public boolean isLegalMove( final Move move )
		{
		return (move != null && move.hashBefore == getZobristHash());
		}

	/**
	 * Makes a move on the board.
	 *
	 * @param move
	 * 	Move to make.
	 *
	 * @return this
	 */
	public Board makeMove( Move move )
		{
		if (!isLegalMove( move )) throw new IllegalMoveException( this, move );
		//	-----------------------------------------------------------------
		applyMove( move.iSqFrom, move.iSqTo, move.iType );
		return this;
		}

	//  -----------------------------------------------------------------------
	//	PUBLIC GETTERS & SETTERS
	//	-----------------------------------------------------------------------

	/**
	 * Gets the piece on a square.
	 *
	 * @param iSq
	 * 	Square index in 8x8 format.
	 *
	 * @return Piece on square, or <c>EMPTY</c> if square is empty.
	 */
	public int get( final int iSq )
		{
		return Square.isValid( iSq ) ? _sq[ iSq ] : EMPTY;
		}

	/**
	 * Tests a square to see if it is empty.
	 *
	 * @param iSq
	 * 	Square index in 8x8 format.
	 *
	 * @return .T. if square is empty, .F. if occupied.
	 */
	public boolean isEmpty( final int iSq )
		{
		return (Square.isValid( iSq ) && _sq[ iSq ] == EMPTY);
		}

	/**
	 * Sets the piece on a square.
	 *
	 * @param iSq
	 * 	Square index in 8x8 format.
	 * @param piece
	 * 	Piece to place, or <c>null</c> to remove the existing piece.
	 *
	 * @return .T. on success, .F. on failure.
	 */
	public boolean set( final int iSq, final int piece )
		{
		if (!Square.isValid( iSq ) || piece < MAP_W_PAWN || piece > MAP_B_KING) return false;
		//	-----------------------------------------------------------------
		if (_sq[ iSq ] != piece)
			{
			if (_sq[ iSq ] != EMPTY)
				removePiece( iSq );

			if (piece != EMPTY)
				placePiece( iSq, piece );

			_bbCheckers = Bitboards.INVALID;
			}

		return true;
		}

	/**
	 * Gets all pieces for a given type that can move to a target square.
	 *
	 * @param pt
	 * 	Piece type [PAWN|KNIGHT|BISHOP|ROOK|QUEEN|KING].
	 * @param iSqTo
	 * 	Target square, in 8x8 format.
	 *
	 * @return Bitboard of pieces.
	 */
	public long getCandidates( int pt, int iSqTo )
		{
		if (pt == EMPTY || !Square.isValid( iSqTo )) return 0L;
		//	-----------------------------------------------------------------
		switch (pt)
			{
			case PAWN:
				if (iSqTo == _iSqEP || _sq[ iSqTo ] != EMPTY)
					{
					return (_player == WHITE)
						   ? (map[ MAP_W_PAWN ] & Bitboards.pawnDownwards[ iSqTo ])
						   : (map[ MAP_B_PAWN ] & Bitboards.pawnUpwards[ iSqTo ]);
					}

				return (_player == WHITE)
					   ? (map[ MAP_W_PAWN ] & (Square.getMask( iSqTo - 8 ) |
											   (Square.getMask( iSqTo - 16 ) & Bitboards.rankMask[ 1 ])))
					   : (map[ MAP_B_PAWN ] & (Square.getMask( iSqTo + 8 ) |
											   (Square.getMask( iSqTo + 16 ) & Bitboards.rankMask[ 6 ])));

			case KNIGHT:
				return map[ MAP_W_KNIGHT + _player ] & Bitboards.knight[ iSqTo ];

			case BISHOP:
				return Bitboards.getDiagonalAttackers( iSqTo,
													   map[ MAP_W_BISHOP + _player ],
													   (map[ MAP_W_ALL ] | map[ MAP_B_ALL ]) );

			case ROOK:
				return Bitboards.getLateralAttackers( iSqTo,
													  map[ MAP_W_ROOK + _player ],
													  (map[ MAP_W_ALL ] | map[ MAP_B_ALL ]) );

			case QUEEN:
				return map[ MAP_W_QUEEN + _player ] &
					   Bitboards.getSlidingMovesFrom( iSqTo,
													  (map[ MAP_W_ALL ] | map[ MAP_B_ALL ]) );

			case KING:
				//  Don't mask against Bitboards.king[] because that excludes castling moves.
				return map[ MAP_W_KING + _player ];
			}

		return 0L;
		}

	/**
	 * Gets the current castling privileges.
	 *
	 * @return Set of <c>CastlingFlags.*</c> values.
	 */
	public int getCastlingFlags()
		{
		return _castling;
		}

	/**
	 * Sets the castling flags.
	 *
	 * @param castling
	 * 	Flags to set
	 */
	public void setCastlingFlags( int castling )
		{
		castling &= CastlingFlags.ALL;    // drop any extra bits
		//
		//  Check White player's King and Rooks
		//
		if (_sq[ Square.E1 ] != Piece.W_KING)
			castling &= ~CastlingFlags.WHITE_BOTH;
		else
			{
			if (_sq[ Square.A1 ] != Piece.W_ROOK)
				castling &= ~CastlingFlags.WHITE_LONG;
			if (_sq[ Square.H1 ] != Piece.W_ROOK)
				castling &= ~CastlingFlags.WHITE_SHORT;
			}
		//
		//  And the same for Black
		//
		if (_sq[ Square.E8 ] != Piece.B_KING)
			castling &= ~CastlingFlags.BLACK_BOTH;
		else
			{
			if (_sq[ Square.A8 ] != Piece.B_ROOK)
				castling &= ~CastlingFlags.BLACK_LONG;
			if (_sq[ Square.H8 ] != Piece.B_ROOK)
				castling &= ~CastlingFlags.BLACK_SHORT;
			}

		if (_castling != castling)
			{
			_castling = castling;
			_hashExtra = ZobristHash.getExtraHash( _castling, _iSqEP, _player );
			}
		}

	/**
	 * Get a bitboard of all pieces that check the King.
	 *
	 * @return Bitboard of checking pieces.
	 */
	public long getCheckers()
		{
		if (_bbCheckers == Bitboards.INVALID)
			_bbCheckers = Bitboards.getAttackedBy( map, getKingSquare(), getOpposingPlayer() );

		return _bbCheckers;
		}

	/**
	 * Gets the current En Passant square.
	 *
	 * @return e.p. square index, in 8x8 format.
	 */
	public int getEnPassantSquare()
		{
		return _iSqEP;
		}

	/**
	 * Sets the current En Passant square.
	 *
	 * @param iSq
	 * 	Square index in 8x8 format.
	 */
	public void setEnPassantSquare( int iSq )
		{
		if (Square.isValid( iSq ) && _sq[ iSq ] == EMPTY)
			{
			if (_player == WHITE)
				{
				if (Square.getRank( iSq ) != 5 || _sq[ iSq - 8 ] != Piece.B_PAWN)
					iSq = INVALID;
				}
			else // if (_player == BLACK)
				{
				if (Square.getRank( iSq ) != 2 || _sq[ iSq + 8 ] != Piece.W_PAWN)
					iSq = INVALID;
				}
			}
		else
			iSq = INVALID;

		if (_iSqEP != iSq)
			{
			_iSqEP = iSq;
			_hashExtra = ZobristHash.getExtraHash( _castling, _iSqEP, _player );
			}
		}

	/**
	 * Gets the current 'half move clock'.
	 *
	 * @return Number of plies since last capture or pawn move.
	 */
	public int getHalfMoveClock()
		{
		return _iHalfMoves;
		}

	/**
	 * Sets the current 'half move clock'.
	 *
	 * @param iNumber
	 * 	Number of pliece since last capture or pawn move, which must be .GE. zero.
	 */
	public void setHalfMoveClock( int iNumber )
		{
		_iHalfMoves = Math.max( iNumber, 0 );
		}

	/**
	 * Gets the square occupied by the moving player's King.
	 *
	 * @return King square in 8x8 format, or INVALID if no king on the board.
	 */
	public int getKingSquare()
		{
		return BitUtil.first( map[ MAP_W_KING + _player ] );
		}

	/**
	 * Gets the square occupied by the opposing player's King.
	 *
	 * @return King square in 8x8 format, or INVALID if no king on the board.
	 */
	public int getOpposingKingSquare()
		{
		return BitUtil.first( map[ MAP_B_KING - _player ] );
		}

	/**
	 * Gets the current move number.
	 *
	 * @return Move number.
	 */
	public int getMoveNumber()
		{
		return _iFullMoves;
		}

	/**
	 * Sets the current mvoe number.
	 *
	 * @param iNumber
	 * 	Move number, which must be .GT. zero.
	 */
	public void setMoveNumber( int iNumber )
		{
		_iFullMoves = Math.max( iNumber, 1 );
		}

	/**
	 * Gets the player currently "on the move".
	 *
	 * @return [WHITE|BLACK].
	 */
	public int getMovingPlayer()
		{
		return _player;
		}

	/**
	 * Gets the player currently NOT "on the move".
	 *
	 * @return [WHITE|BLACK].
	 */
	public int getOpposingPlayer()
		{
		return (_player ^ 1);
		}

	/**
	 * Sets the color of the player currently "on the move".
	 *
	 * @param player
	 * 	Moving player [WHITE|BLACK]
	 */
	public void setMovingPlayer( final int player )
		{
		DBC.require( (player == WHITE || player == BLACK), "Invalid player color." );
		//	-----------------------------------------------------------------
		if (_player != player)
			{
			_player = player;
			_hashExtra = ZobristHash.getExtraHash( _castling, _iSqEP, _player );
			}
		}

	/**
	 * Exposes the internal piece maps.
	 *
	 * @param index
	 * 	Index into map.
	 *
	 * @return Bitboard at map index.
	 */
	public long getPieceMap( int index )
		{ return (index >= 0 && index < map.length) ? map[ index ] : 0L;}


	/**
	 * Gets the type of piece on a square.
	 *
	 * @param sq
	 * 	Square index in 8x8 format.
	 *
	 * @return Piece type [PAWN|KNIGHT|BISHOP|etc.] on square, or
	 * <c>EMPTY</c> if square is empty.
	 */
	public int getPieceType( final int sq )
		{
		return Square.isValid( sq ) ? Piece.getType( _sq[ sq ] ) : EMPTY;
		}

	/**
	 * Gets the Zobrist hash for the current position.
	 *
	 * @return 64-bit hash value.
	 */
	public long getZobristHash()
		{
		return (_hashExtra ^ _hashPawns ^ _hashPieces);
		}


	/**
	 * Determines if the moving player is currently in check.
	 *
	 * @return .T. if moving player in check; .F. otherwise.
	 */
	public boolean isInCheck()
		{
		return (getCheckers() != 0L);
		}

	//  -----------------------------------------------------------------------
	//	GETTERS & SETTERS
	//	-----------------------------------------------------------------------
	/**
	 * Get a bitboard of all pinned pieces.
	 *
	 * @return Bitboard of pinned pieces, or zero if moving player is in check.
	 */
	long getPinnedPieces()
		{
		if (isInCheck()) return 0L;
		//	-----------------------------------------------------------------
		final int opponent = getOpposingPlayer();
		final int sqKing = getKingSquare();

		final long bbQueen = map[ MAP_W_QUEEN + opponent ];
		final long bbBishops = map[ MAP_W_BISHOP + opponent ];
		final long bbRooks = map[ MAP_W_ROOK + opponent ];
		final long bbOpponent = map[ MAP_W_ALL + opponent ];
		final long bbPlayer = map[ MAP_W_ALL + _player ];
		//
		//  Find pinned pieces.  This is done by finding all of the opposing pieces that
		//  could attack the King if the moving player's pieces were removed.
		//
		long bbPinned = 0L;
		long bbPinners =
			Bitboards.getDiagonalAttackers( sqKing, (bbQueen | bbBishops), bbOpponent ) |
			Bitboards.getLateralAttackers( sqKing, (bbQueen | bbRooks), bbOpponent );

		for ( long bb = bbPinners & ~Bitboards.king[ sqKing ]; bb != 0L; bb &= (bb - 1) )
			{
			//
			//  If there is one (and only one) moving piece that lies on the path between a
			//	threatening piece (the "pinner") and the King, then it is pinned. Pinned
			//	pieces may still be able to move (except for Knights) but need to test for
			//	check when they do so.
			//
			long bbBetween = bbPlayer &
							 Bitboards.getSquaresBetween( sqKing, BitUtil.first( bb ) );

			if (BitUtil.singleton( bbBetween ))
				bbPinned |= bbBetween;
			}

		return bbPinned;
		}

	//  -----------------------------------------------------------------------
	//	METHODS
	//	-----------------------------------------------------------------------

	/**
	 * Makes a move on the board.
	 *
	 * @param iSqFrom
	 * 	From square, in 8x8 format.
	 * @param iSqTo
	 * 	To square, in 8x8 format.
	 * @param iType
	 * 	Move.Type.*
	 */
	private void applyMove( int iSqFrom, int iSqTo, int iType )
		{
		if (_sq[ iSqTo ] != EMPTY)
			{
			_iHalfMoves = 0;
			removePiece( iSqTo );
			}
		else if (Piece.getType( _sq[ iSqFrom ] ) == PAWN)
			_iHalfMoves = 0;
		else
			_iHalfMoves++;

		_bbCheckers = Bitboards.INVALID;
		_iSqEP = INVALID;

		switch (iType)
			{
			case Move.Type.NORMAL:
				movePiece( iSqFrom, iSqTo );
				break;

			case Move.Type.PAWN_PUSH:
				movePiece( iSqFrom, iSqTo );
				_iSqEP = (iSqFrom + iSqTo) >>> 1;
				break;

			case Move.Type.CASTLING:
				movePiece( iSqFrom, iSqTo );
				if (iSqTo > iSqFrom)    // .T. if O-O; .F. if O-O-O
					movePiece( iSqTo + 1, iSqTo - 1 );
				else
					movePiece( iSqTo - 2, iSqTo + 1 );
				break;

			case Move.Type.EN_PASSANT:
				movePiece( iSqFrom, iSqTo );
				removePiece( (iSqFrom & 0x38) | (iSqTo & 0x07) );
				break;

			case Move.Type.PROMOTION:
				removePiece( iSqFrom );
				placePiece( iSqTo, Piece.W_QUEEN + _player );
				break;

			case Move.Type.PROMOTE_ROOK:
				removePiece( iSqFrom );
				placePiece( iSqTo, Piece.W_ROOK + _player );
				break;

			case Move.Type.PROMOTE_BISHOP:
				removePiece( iSqFrom );
				placePiece( iSqTo, Piece.W_BISHOP + _player );
				break;

			case Move.Type.PROMOTE_KNIGHT:
				removePiece( iSqFrom );
				placePiece( iSqTo, Piece.W_KNIGHT + _player );
				break;

			default:
				throw new RuntimeException( "Unrecognized move type." );
			}
		//
		//  Update the castling flags, move number, and flip the player.
		//
		if (_castling != CastlingFlags.NONE)
			_castling &= s_castling[ iSqFrom ] & s_castling[ iSqTo ];

		if ((_player ^= 1) == WHITE)
			_iFullMoves++;

		_hashExtra = ZobristHash.getExtraHash( _castling, _iSqEP, _player );
		}

	/**
	 * Copies another board to this one.
	 *
	 * @param src
	 * 	Source (other) board.
	 */
	void copyFrom( Board src )
		{
		assert src != null;
		//	-----------------------------------------------------------------
		_castling = src._castling;
		_bbCheckers = src._bbCheckers;
		_hashExtra = src._hashExtra;
		_iFullMoves = src._iFullMoves;
		_iHalfMoves = src._iHalfMoves;
		_iSqEP = src._iSqEP;
		_player = src._player;

		if (_hashPawns != src._hashPawns || _hashPieces != src._hashPieces)
			{
			_hashPawns = src._hashPawns;
			_hashPieces = src._hashPieces;
			System.arraycopy( src.map, 0, map, 0, MAP_LENGTH );
			System.arraycopy( src._sq, 0, _sq, 0, 64 );
			}
		}

	/**
	 * Moves a piece from one square to another.
	 *
	 * @param iSqFrom
	 * 	"From" square in 8x8 format.
	 * @param iSqTo
	 * 	"To" square in 8x8 format.
	 */
	private void movePiece( int iSqFrom, int iSqTo )
		{
		assert Square.isValid( iSqFrom );
		assert Square.isValid( iSqTo );

		assert _sq[ iSqFrom ] != EMPTY;
		assert _sq[ iSqTo ] == EMPTY;
		//	-----------------------------------------------------------------
		final int piece = _sq[ iSqFrom ];
		final long bbSqMask = (1L << iSqFrom) | (1L << iSqTo);

		map[ piece ] ^= bbSqMask;
		map[ piece & 1 ] ^= bbSqMask;

		_sq[ iSqFrom ] = EMPTY;
		_sq[ iSqTo ] = piece;

		if (Piece.getType( piece ) == PAWN)
			_hashPawns ^= ZobristHash.getPieceHash( iSqFrom, iSqTo, piece );
		else
			_hashPieces ^= ZobristHash.getPieceHash( iSqFrom, iSqTo, piece );
		}

	/**
	 * Places a piece on a square.
	 *
	 * @param iSq
	 * 	Square index, in 8x8 format.
	 * @param piece
	 * 	Piece to place.
	 */
	void placePiece( int iSq, final int piece )
		{
		assert Square.isValid( iSq );
		assert piece >= MAP_W_PAWN && piece <= MAP_B_KING;
		//	-----------------------------------------------------------------
		final long bbMask = 1L << iSq;

		map[ piece ] |= bbMask;
		map[ piece & 1 ] |= bbMask;

		_sq[ iSq ] = piece;

		if (Piece.getType( piece ) == PAWN)
			_hashPawns ^= ZobristHash.getPieceHash( iSq, piece );
		else
			_hashPieces ^= ZobristHash.getPieceHash( iSq, piece );
		}

	/**
	 * Removes the piece on a square.
	 *
	 * @param iSq
	 * 	Square index, in 8x8 format.
	 */
	void removePiece( int iSq )
		{
		assert Square.isValid( iSq );
		//	-----------------------------------------------------------------
		final int piece = _sq[ iSq ];
		final long bbNotMask = ~(1L << iSq);

		map[ piece ] &= bbNotMask;
		map[ piece & 1 ] &= bbNotMask;

		_sq[ iSq ] = EMPTY;

		if (Piece.getType( piece ) == PAWN)
			_hashPawns ^= ZobristHash.getPieceHash( iSq, piece );
		else
			_hashPieces ^= ZobristHash.getPieceHash( iSq, piece );
		}

	//  -----------------------------------------------------------------------
	//	NESTED CLASS: CastlingFlags
	//	-----------------------------------------------------------------------

	@SuppressWarnings( "PointlessBitwiseExpression" )
	public static class CastlingFlags
		{
		/** No castling is possible in the current position. */
		public static final int NONE = 0;

		/** White can castle king-side (short). */
		public static final int WHITE_SHORT = 1 << 0;
		/** White can castling queen-side (long). */
		public static final int WHITE_LONG  = 1 << 1;
		/** White can castle in both directions. */
		public static final int WHITE_BOTH  = WHITE_SHORT | WHITE_LONG;

		/** Black can castle king-side (short). */
		public static final int BLACK_SHORT = 1 << 2;
		/** Black can castling queen-side (long). */
		public static final int BLACK_LONG  = 1 << 3;
		/** Black can castle in both directions. */
		public static final int BLACK_BOTH  = BLACK_SHORT | BLACK_LONG;

		/** All of the castling flags. */
		public static final int ALL = WHITE_BOTH | BLACK_BOTH;
		}   /* end of class CastlingFlags */

	}   /* end of class Board */

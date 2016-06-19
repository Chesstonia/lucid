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
package net.humbleprogrammer.humble;

import java.util.concurrent.TimeUnit;

public class TimeUtil
    {

    //  -----------------------------------------------------------------------
    //	PUBLIC DECLARATIONS
    //	-----------------------------------------------------------------------

    /** Number of milliseconds in 1 second. */
    public static final long MILLISECONDS = TimeUnit.SECONDS.toMillis( 1L );
    /** Number of nanoseconds in 1 second. */
    public static final long NANOSECONDS  = TimeUnit.SECONDS.toNanos( 1L );


    //  -----------------------------------------------------------------------
    //	PUBLIC METHODS
    //	-----------------------------------------------------------------------

    public static String formatMillisecs( long lMillisecs, final boolean bWantMSecs )
        {
        if (lMillisecs <= 0L)
            return bWantMSecs ? "00:00.000" : "00:00";
        /*
        **  CODE
        */
        final StringBuilder sb = new StringBuilder();
        //
        //  Hours
        //
        final int iHours = (int) TimeUnit.MILLISECONDS.toHours( lMillisecs );

        if (iHours > 0)
            {
            lMillisecs -= TimeUnit.HOURS.toMillis( iHours );

            if (sb.length() > 0)
                sb.append( ' ' );

            sb.append( String.format( "%,d:", iHours ) );
            }
        //
        //  Minutes
        //
        final int iMinutes = (int) TimeUnit.MILLISECONDS.toMinutes( lMillisecs );

        if (iMinutes > 0)
            {
            assert iMinutes < 60;

            lMillisecs -= TimeUnit.MINUTES.toMillis( iMinutes );
            sb.append( String.format( "%02d:", iMinutes ) );
            }
        else
            sb.append( "00:" );
        //
        //  Seconds
        //
        final int iSeconds = (int) TimeUnit.MILLISECONDS.toSeconds( lMillisecs );

        if (iSeconds > 0)
            {
            assert iSeconds < 60;

            lMillisecs -= TimeUnit.SECONDS.toMillis( iSeconds );
            sb.append( String.format( "%02d", iSeconds ) );
            }
        else
            sb.append( "00" );
        //
        //  Milliseconds
        //
        if (bWantMSecs)
            {
            assert lMillisecs < MILLISECONDS;

            if (lMillisecs > 0L)
                sb.append( String.format( ".%03d", lMillisecs ) );
            else
                sb.append( ".000" );
            }

        return sb.toString();
        }
    }   /* end of class TimeUtil */

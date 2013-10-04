package net.mustelinae.siteswap;

import java.util.*;

/**
 * Static utility methods.
 *
 * @author Boris Grozev
 */
public class Utils
{
    public static boolean DEBUG = false;

    /**
     * Translate between a throw height specified as a symbol of the alphabet
     * we use to denote siteswaps (0 through Z) and it's associated throw
     * height as an int.
     * 
     * Examples:
     * charToInt('a') returns 10
     */
    public static int charToInt(char c)
            throws Exception
    {
        int ret;
        if ('0' <= c && c <= '9')
            ret = c - '0';
        else if ('a' <= c && c <= 'z')
            ret = c - 'a' + 10;
        else if ('A' <= c && c <= 'Z')
            ret = c - 'A' + 10;
        else
            throw new Exception("not allowed char");

        return ret;
    }

    /**
     * Translate between a throw height specified as an int and it's
     * representation in the alphabet we use to denote siteswaps (0 through Z).
     *
     * Example:
     * intToChar(12) returns 'c'
     */
    public static char intToChar(int i)
            throws Exception
    {
        if (i<0 || i>35)
            throw new Exception("int out of bounds");
        if(i<9)
            return (char) ('0'+i);
        else
            return (char) ('a'+i-10);
    }

    /**
     * Translate an array of int-s specifying throw height into a String that
     * uses the normal siteswap notation.
     *
     * Example:
     * arrayToString([11,9,7,5,3,1]) returns "b97531"
     */
    public static String arrayToString(int[] s)
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i<s.length; i++)
        {
            try
            {
                sb.append(intToChar(s[i]));
            }
            catch (Exception e)
            {
                return null;
            }
        }
        return sb.toString();
    }

    /**
     * Translate a siteswap represented as a String into an array of int-s.
     *
     * Example:
     * stringToArray("1337beef") returns [1, 3, 3, 7, 11, 14, 14, 15]
     */
    public static int[] stringToArray(String str)
    {
        if (str == null || str.length() == 0)
            return null;

        int[] ret = new int[str.length()];

        try
        {
            for (int i = 0; i<str.length(); i++)
                ret[i] = charToInt(str.charAt(i));
        }
        catch (Exception e)
        {
            return null;
        }

        return ret;
    }

    /**
     * Normalize the sequence <tt>a</tt> by first shortening it to the shortest
     * subsequence which when repeated yields <tt>a</tt>, and then finding the
     * lexicographically largest of its shifts.
     *
     * For example:
     * [3,3,3] is normalized to [3]
     * [1,4,4,1,4,4] is normalized to [4,4,1]
     *
     * @param a the sequence to normalize
     * @return the normalized sequence
     */
    public static int[] normalize(int[] a)
    {
        int len = a.length;
        for (int i = 1; i < len; i++) //ugly way to iterate over len's devisors
        {
            if (len % i == 0)
            {
                boolean cannot_be_cut = false;
                for (int j = 0; j<i; j++)
                {
                    for(int k = 1; k < (len/i); k++)
                    {
                        if(a[j] != a[(j+i*k)%len])
                        {
                            cannot_be_cut = true;
                            break;
                        }
                    }
                    if (cannot_be_cut)
                    {
                        break;
                    }
                }
                if (!cannot_be_cut)
                {
                    int[] aa = new int[i];
                    for(int j = 0; j<i; j++)
                        aa[j] = a[j];
                    return order(aa);
                }
            }
        }
        return order(a);
    }

    /**
     * Returns the lexicographically largest of <tt>a</tt>'s shifts
     */
    private static int[] order(int[] a)
    {
        int best_shift = 0;
        int len = a.length;
        for (int i = 1; i<len; i++)
        {
            if(compare_shift(a, i, best_shift) == 1)
                best_shift = i;
        }

        if (best_shift == 0)
            return a;

        int[] aa = new int[len];
        for (int i = 0; i<len; i++)
            aa[i] = a[(i+best_shift)%len];
        return aa;
    }

    /**
     * Checks if <tt>a</tt> shifted by <tt>s1</tt> is lexicographically 
     * bigger, the same, or smaller than <tt>a</tt> sifted by <tt>s2</tt>.
     * Returns 1, 0 or -1 respectively.
     *
     * That is returns:
     * 1 if (a_s1) > (a_s2)
     * 0 if (a_s1) = (a_s2)
     *-1 if (a_s1) < (a_s2)
     */
    private static int compare_shift(int[] a, int s1, int s2)
    {
        int len = a.length;
        for (int i = 0; i < a.length; i++)
        {
            if(a[(i+s1)%len] > a[(i+s2)%len])
                return 1;
            else if(a[(i+s1)%len] < a[(i+s2)%len])
                return -1;
        }

        return 0;
    }

    /**
     * Checks whether <tt>sequence</tt> represents a valid vanilla siteswap pattern.
     */
    public static boolean isValid(int[] siteswap)
    {
        if (siteswap == null || siteswap.length == 0)
            return false;

        int len = siteswap.length;
        int sum = 0;
        int[] landing = new int[len];
        for (int i = 0; i<len; i++)
        {
            sum += siteswap[i];
            landing[(i+siteswap[i])%len]++; //where i-th throw lands
        }

        if (sum % len != 0)
            return false;

        for (int i = 0; i<len; i++)
            if (landing[i] > 1) //collision de las bolas
                return false;

        return true;
    }
}

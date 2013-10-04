package net.mustelinae.siteswap;

import java.util.*;

/**
 * Represents a sequence of integers, could be a valid (vanilla) siteswap or
 * not. Use <tt>isValid()</tt> to check.
 *
 * @author Boris Grozev
 */
public class Siteswap
{
    private static boolean DEBUG = false;

    private int[] siteswap;
    private int[] pb; //period base
    private int len;

    public Siteswap(int[] a)
    {
        int[] normalized = Utils.normalize(a);
        if(DEBUG)
        {
            System.err.println("pre-normalize: "+Arrays.toString(a));
            System.err.println("post-normalize: "+Arrays.toString(normalized));
        }

        siteswap = normalized;
        len = normalized.length;
        pb = new int[len];
        for (int i = 0; i < len; i++)
            pb[i] = siteswap[i] % len;
    }

    public Siteswap(String str)
    {
        this(Utils.stringToArray(str));
    }

    /**
     * Checks that the required fields are non-null and have a length >0
     */
    private boolean sanityCheck()
    {
        if (siteswap == null || siteswap.length == 0
                || pb == null || siteswap.length != pb.length
                || siteswap.length != len)
            return false;

        return true;
    }

    /**
     * Checks if this <tt>Siteswap</tt> actually represents a valid siteswap
     * pattern (and not a sequence such as "21" for example)
     */
    public boolean isValid()
    {
        if (!sanityCheck())
            return false;
        return Utils.isValid(siteswap);
    }

    public int[] getSequence()
    {
        return siteswap;
    }

    /**
     * Checks whether the siteswap represented by this instance is the same
     * as the siteswap represented by <tt>o</tt>. Considers that all invalid
     * siteswaps are equal to each other.
     */
    public boolean equals(Object o)
    {
        if (o == null || !(o instanceof Siteswap))
            return false;
        Siteswap s = (Siteswap)o;

        if (!isValid() && !s.isValid())
            return true;
        if (!isValid() && s.isValid())
            return false;
        if (isValid() && !s.isValid())
            return false;

        int[] sequence = s.getSequence();

        return Arrays.equals(
                Utils.normalize(siteswap),
                Utils.normalize(sequence));
    }

    /**
     * Baaad practice. Returns a hash code that depends (in a deterministic,
     * but arbitrarily chosen way) on the values of <tt>siteswap</tt>. Just a
     * hack to make java Set-s of Siteswap-s work as expected...probably kills
     * performance :/
     */
    @Override
    public int hashCode()
    {
        int hc = 0;
        for(int i = 0; i<siteswap.length; i++)
        {
            hc+=42;
            hc*=siteswap[i];
        }

        return hc;
    }

    /**
     * Checks whether this Siteswap belongs to the class Interesting1
     *
     * Interesting1 is the unimaginatively named wide class of siteswaps which
     * have a high (4 or higher) throw that follows a 1.
     */
    public boolean isInteresting1()
    {
        if (!isValid())
            return false;

        for (int i = 0; i < len; i++)
            if (siteswap[i] == 1 && siteswap[(i+1)%len] >= 4)
                return true;

        return false;
    }

    /**
     * Checks whether this Siteswap belongs to the class Interesting2
     *
     * Interesting2 is the unimaginatively named class of siteswaps in which
     * every high (4 or higher) throw is preceeded by a 1.
     */
    public boolean isInteresting2()
    {
        if (!isValid())
            return false;

        for (int i = 0; i < len; i++)
            if (siteswap[i] >= 4 && siteswap[(len+i-1)%len] != 1)
                return false;

        return true;
    }

    /**
     * Checks whether the reverse sequence of this siteswap is a valid siteswap.
     */
    public boolean isReverseValid()
    {
        if (!sanityCheck())
            return false;

        int[] reverse = new int[len];
        for (int i = 0; i<len; i++)
            reverse[i] = siteswap[len-i-1];

        return Utils.isValid(reverse);
    }

    /**
     * Checks whether this <tt>Siteswap</tt> belongs to the class of siteswaps
     * defined by Nikolaj Beluhov as follows:
     *
     * siteswap с период, който не се дели на три, който не съдържа
     * хвърляния, по-високи от петица, не съдържа единици, не съдържа твърде
     * много двойки или нули, всичките му височини са взаимно прости с периода
     * и е симетричен по отношение на ляво и дясно.
     */
    public boolean isInterestingNikolaj()
    {
        if(!isValid())
            return false;
        if(len % 3 == 0 || len % 2 == 0)
        {
            return false;
        }

        if(contains(1))
        {
            return false;
        }

        int count0 = 0; int count2 = 0;
        for(int i = 0; i<len; i++)
        {
            if(siteswap[i] > 2 && gcd(siteswap[i], len) != 1)
            {
                return false;
            }

            if (siteswap[i]==0)
                count0++;
            if (siteswap[i]==2)
                count2++;
        }

        //Борис: това е моята интерпретация на "твърде много двойки или нули".
        //Може да не е адекватна.
        if((len < 6 && count0+count2 >= 2)
                || (len >= 6 && count0+count2 >=3))
        {
            return false;
        }

        return true;
    }

    private static int gcd(int a, int b)
    {
        if(a == b)
            return a;
        else if(a > b)
            return gcd(b, a-b);
        return gcd(a, b-a);
    }

    /**
     * Checks whether this <tt>Siteswap</tt> contains a throw of height <tt>x</tt>
     */
    public boolean contains(int x)
    {
        if (!sanityCheck())
            return false;

        for (int i = 0; i < len; i++)
            if (siteswap[i] == x)
                return true;
        return false;
    }

    public String toString()
    {
        if(!isValid())
            return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++)
            sb.append(siteswap[i]);

        return sb.toString();
    }
}

package net.mustelinae.siteswap;

import java.io.*;


/**
 * Reads siteswaps from a file (one siteswap on a line) and outputs to stdout 
 * only those siteswaps which satisfy a certain condition. See the code for 
 * available conditions.
 *
 * @author Boris Grozev
 */
public class Filter {
    public static void main(String[] args) {
        if (args == null || args.length != 2)
        {
            System.err.println("Usage: Filter <'i1' | 'i2' | 'nikolaj'> <filename>");
            return;
        }

        F filter = null;
        if ("i1".equalsIgnoreCase(args[0]))
        {
            filter = new F() {
                @Override
                public boolean f(String s) {
                    return (new Siteswap(s).isInteresting1());
                }
            };
        }
        else if ("i2".equalsIgnoreCase(args[0]))
        {
            filter = new F() {
                @Override
                public boolean f(String s) {
                    return (new Siteswap(s).isInteresting2());
                }
            };
        }
        else if ("nikolaj".equalsIgnoreCase(args[0]))
        {
            filter = new F() {
                @Override
                public boolean f(String s) {
                    return (new Siteswap(s).isInterestingNikolaj());
                }
            };
        }

        if (filter == null)
        {
            System.err.println("Invalid filter.");
            return;
        }


        BufferedReader br;
        try
        {
            br = new BufferedReader(new FileReader(args[1]));
        }
        catch (FileNotFoundException e)
        {
            System.err.println("Could not open file: "+args[1]);
            return;
        }

        String line;
        try
        {
            while ((line = br.readLine()) != null)
            {
                if(filter.f(line))
                    System.out.println(""+line);
            }
            br.close();
        }
        catch (IOException e)
        {
            System.err.println("IOException: "+e);
            return;
        }
    }

    private interface F
    {
        public boolean f(String s);
    }
}

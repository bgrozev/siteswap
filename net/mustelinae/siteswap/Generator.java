package net.mustelinae.siteswap;

import java.util.*;

/**
 * @author Boris Grozev
 */
public class Generator
{
    private static boolean DEBUG = Utils.DEBUG;
    private SiteswapHandler handler;
    private int balls;
    private int max_height;
    private int generated_patterns = 0;
    private StateGraph graph;

    public Generator(int balls, int max_height)
    {
        this(   balls,
                max_height,
                new SiteswapHandler() {
                    @Override
                    public boolean handle(Siteswap siteswap) {
                        if (siteswap.isValid())
                        {
                            System.err.println("" + siteswap);
                            return true;
                        }
                        else
                        {
                            System.err.println("Ops, the generator generated "
                                    + "an invalid siteswap. This shouldn't"
                                    + "have happened: " + siteswap);
                            return false;
                        }
                    }
                });
    }

    public Generator(int balls, int max_height, SiteswapHandler handler)
    {
        this.balls = balls;
        this.max_height = max_height;
        this.handler = handler;
    }

    public void setHandler(SiteswapHandler handler)
    {
        this.handler = handler;
    }

    /**
     * Look for cycles in the graph with period <tt>period</tt>, translate them
     * to siteswaps and handle them with <tt>handler</tt>
     */

    public int generate(int period)
    {
        //we generate paths with length 'period' and check whether the first and
        //last node is the same. this gives us cycles of length period-1
        period++;
        if (graph == null)
            graph = new StateGraph(balls, max_height);
        int patterns = 0;

        if(DEBUG)
            System.err.println("Generating cycles with period "+(period-1));

        for (long root : graph.getNodes())
        {
            if(DEBUG)
            {
                System.err.print("Starting from root: ");
                graph.print(root);
            }
            LinkedList<Long> path = new LinkedList<Long>();
            long node = root;
            while(path.size() < period)
            {
                path.add(node);
                node = graph.getNextChild(node, 0); //always get the first child
            }

            while(path != null)
            {
                if (path.get(0) == path.get(period-1))
                {
                    //cycle. restore the siteswap and handle it.
                    int[] siteswap = new int[period-1];
                    for (int i = 0; i<period-1; i++)
                    {
                        int height = graph.getLabel(path.get(i), path.get(i+1));
                        siteswap[i] = height;
                    }
                    patterns++;

                    Siteswap s = new Siteswap(siteswap);
                    //dont handle siteswaps which turn out to have smaller period
                    //e.g. don't handle '3' as a siteswap with period >1
                    if(s.getSequence().length == period-1) 
                        handler.handle(s);
                }

                path = getNextPath(path, period);
            }
        }
        return patterns;
    }

    private LinkedList<Long> getNextPath(LinkedList<Long> path, int period)
    {
        if (path.size() < 2)
            return null;

        long pop = path.getLast();
        path.removeLast();
        long pop2 = path.getLast();
        long next_child = graph.getNextChild(pop2, pop);
        if (next_child == 0) //it's the last child, backtrack
            return getNextPath(path, period);

        long node = next_child;
        while(path.size() < period)
        {
            path.add(node);
            node = graph.getNextChild(node, 0); //get the first child all the way down
        }
        return path;
    }


    public static void main(String[] args)
    {
        if(args.length != 4)
        {
            System.err.println("Usage: Generator <balls> <max-height> <period-from> <period-to>");
            return;
        }
        int balls = Integer.parseInt(args[0]);
        int max_height = Integer.parseInt(args[1]);
        int period_from = Integer.parseInt(args[2]);
        int period_to = Integer.parseInt(args[3]);

        Generator g = new Generator(balls, max_height);

        final Set<Siteswap> siteswaps = new HashSet<Siteswap>();
        // this is slow. for big sets, it is faster to save everything in a
        // file and run 'sort | uniq' on it separately
        SiteswapHandler handler = new SiteswapHandler() {
            @Override
            public boolean handle(Siteswap siteswap) {
                siteswaps.add(siteswap);
                return false;
            }
        };
        g.setHandler(handler);

        for(int period = period_from; period<=period_to; period++)
        {
            if(DEBUG)
                System.err.println("Period="+period);
            g.generate(period);
        }

        for(Siteswap s: siteswaps)
            System.out.println(""+s);
    }
}



package net.mustelinae.siteswap;

import java.util.*;

/**
 * A class that represent a graph of juggling states. The nodes are of type
 * <tt>long</tt> and the edges are labeled with throw heights.
 */
public class StateGraph
{
    private static boolean DEBUG = Utils.DEBUG;

    /**
     * Number of balls.
     */
    private int balls;

    /**
     * Maximum throw height.
     */
    private int max_height;

    /**
     * A node to be considered root. This is set to the "xx...x000" state with
     * <tt>balls</tt> xs and enough 0s to match <tt>max_height</tt>
     */
    private long root;

    /**
     * The set of nodes in the graph.
     */
    private Set<Long> nodes = new HashSet<Long>();

    /**
     * The structure that holds the graph edges. For every node, we maintain an
     * ordered map, which contains entries for the node's children. Each of
     * those child entries is of the form (throw_height, destination).
     */
    private Map<Long, TreeMap<Integer, Long>> edges
            = new HashMap<Long, TreeMap<Integer, Long>>();

    /**
     * Constructs a graph with the given number of balls and the given maximum
     * throw height.
     * @param balls the number of balls
     * @param max_height the maximum throw height
     */
    public StateGraph(int balls, int max_height)
    {
        this.balls = balls;
        this.max_height = max_height;
        root = 0;
        for (int i = 1; i<=balls; i++)
            root = set(root, i);

        Set<Long> undone = new HashSet<Long>();
        undone.add(root);

        while (!undone.isEmpty())
        {
            long node = undone.iterator().next();
            nodes.add(node);
            TreeMap<Integer, Long> children = generateChildren(node);
            for (long child : children.values())
                if (!nodes.contains(child))
                    undone.add(child);

            edges.put(node, children);

            undone.remove(node);
        }

        if (DEBUG)
        {
            print();
            System.err.println("Constructed graph, balls=" + balls
                + ", max_height=" + max_height);
        }
    }

    /**
     * Returns the juggling state obtained by <tt>state</tt> by removing the
     * left-most position and adding a 0 to the right. That is, shifts
     * <tt>state</tt> one position to the left.
     * 
     * Example: 0xxx0 -> xxx00
     * Example: xx000 -> x0000
     * @param state the state to shift.
     */
    private long shiftLeft(long state)
    {
        long ret = 0;
        for (int i = 2; i<=max_height; i++)
            if (isSet(state, i))
                ret = set(ret, i-1);
        return ret;
    }

    /**
     * Prints a list of nodes to stderr
     * @param path
     */
    private void printPath(List<Long> path)
    {
        if(path == null)
            return;
        for(Long l : path)
            print(l);
    }

    /**
     * Prints the graph to stderr
     */
    private void print()
    {
        for(long node : nodes)
        {
            print(node);
            for(Map.Entry<Integer, Long> entry : edges.get(node).entrySet())
            {
                System.err.print(" "+entry.getKey()+": ");
                print(entry.getValue());
            }
            System.err.println("");
        }
    }

    /**
     * For a given node, generates and returns the <tt>TreeMap</tt> of its
     * children. Helps it generating the graph. If the graph is already
     * generated, <tt>getChildren</tt> should be used instead.
     * @param node the node for which to generate the map of its children
     * @return the map of children of <tt>node</tt>
     */
    private TreeMap<Integer, Long> generateChildren(long node)
    {
        TreeMap<Integer,Long> ret = new TreeMap<Integer,Long>();
        long shifted = shiftLeft(node);
        if (isSet(node, 1))
        {
            for (int i = 1; i<= max_height; i++)
            {
                if(!isSet(shifted, i))
                {
                    ret.put(i, set(shifted, i));
                }
            }

        }
        else
        {
            ret.put(0, shifted);
        }

        return ret;
    }

    /**
     * Gets the <tt>TreeMap</tt> of children of <tt>node</tt> from the already
     * generated graph.
     * @param node
     * @return
     */
    public TreeMap<Integer, Long> getChildren(long node)
    {
        return edges.get(node);
    }

    /**
     * Returns the juggling state obtained by <tt>state</tt> by setting the
     * position <tt>position</tt> to 1.
     * @param state
     * @param position
     * @return
     */
    private long set(long state, int position)
    {
        if (isSet(state, position))
            return state;
        else
            return state + power(2, position);
    }

    /**
     * Returns the juggling state obtained by <tt>state</tt> by setting the
     * position <tt>position</tt> to 0.
     * @param state
     * @param position
     * @return
     */
    private long unset(long state, int position)
    {
        if (isSet(state, position))
            return state - power(2, position);
        else
            return state;
    }

    /**
     * Prints a juggling state to stderr
     * @param state
     */
    public void print(long state)
    {
        for (int i = 1; i <= max_height; i++)
        {
            if (isSet(state, i))
                System.err.print("x");
            else
                System.err.print("0");
        }
        System.err.print("\n");
    }

    /**
     * Checks whether <tt>position</tt> is set (i.e. a ball is scheduled to
     * land on it) in the juggling state <tt>state</tt>
     * @param state
     * @param position
     * @return
     */
    private boolean isSet(long state, int position)
    {
        return !( (state & (power(2, position))) == 0);
    }

    public Set<Long> getNodes()
    {
        return nodes;
    }


    /**
     * Returns a^power
     */
    private long power(int a, int power)
    {
        long ret = 1;
        for (int i = 0; i < power; i++)
            ret *= a;
        return ret;
    }

    /**
     * Returns the child "after" <tt>current_child</tt> in the ordered
     * <tt>Map</tt> of children of <tt>node</tt>.
     *
     * If <tt>current_child</tt> is 0, returns the first child.
     *
     * If <tt>current_child</tt> is the last child, returns 0.
     *
     * Assumes that <tt>current_child</tt> is in fact a child of <tt>node</tt>
     *
     * @param node
     * @param current_child
     * @return
     */
    public long getNextChild(long node, long current_child)
    {
        TreeMap<Integer, Long> children = getChildren(node);
        Iterator<Map.Entry<Integer, Long>> iter = children.entrySet().iterator();
        if(current_child == 0)
            return iter.next().getValue();
        else
        {
            boolean next = false;
            while (iter.hasNext())
            {
                if (next)
                    return iter.next().getValue();
                if (iter.next().getValue() == current_child)
                    next = true;
            }
            // either current_child is not a child (contrary to our assumption),
            // or it's the last child
            return 0;
        }
    }

    /**
     * Return the label of the edge in the graph that connects <tt>from</tt>
     * to <tt>to</tt>.
     * <tt>to</tt>
     * @param from
     * @param to
     * @return
     */
    public int getLabel(long from, long to)
    {

        TreeMap<Integer, Long> children = getChildren(from);
        for (Map.Entry<Integer, Long> entry : children.entrySet())
        {
            if (entry.getValue() == to)
                return entry.getKey();
        }
        return -1;
    }
}


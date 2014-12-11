import java.util.*;

public class BoggleSolver {
    private SET<String> dictionarySet;
    private TST<Integer> foundWords;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        dictionarySet = new SET<String>();
        for (String word : dictionary) {
            dictionarySet.add(word);
        }
        foundWords = new TST<Integer>();
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        final int rowsNumber = board.rows();
        final int colsNumber = board.cols();
        final int totalBoardSize = rowsNumber * colsNumber;
        Graph boardDigraph = new Graph(totalBoardSize);
        for (int i = 0; i < rowsNumber - 1; i++) {
            for (int j = 0; j < colsNumber; j++) {
                int currentIndex = i * rowsNumber + j;
                addBottomNeighbor(boardDigraph, currentIndex, colsNumber);
                final int indexInRow = currentIndex % colsNumber;
                if (indexInRow != colsNumber - 1) {
                    addRightNeighbor(boardDigraph, currentIndex);
                    addRightBottomDiagonalNeighbor(boardDigraph, currentIndex, colsNumber);
                }
                if (indexInRow != 0) {
                    addLeftBottomDiagonalNeighbor(boardDigraph, currentIndex, colsNumber);
                }
            }
        }
        for (int i = totalBoardSize - colsNumber; i < totalBoardSize - 1; i++) {
            addRightNeighbor(boardDigraph, i);
        }
        int foundWordTreeValue = 0;
        for (int i = 0; i < totalBoardSize; i++) {
            BreadthFirstAllPaths breadthFirstAllPaths = new BreadthFirstAllPaths(boardDigraph, i);
            for (int j = 0; j < totalBoardSize; j++) {
                if (i != j) {
                    final Set<List<Integer>> allPaths = breadthFirstAllPaths.pathsTo(j);
                    for (List<Integer> charIndexes : allPaths) {
                        StringBuilder possibleWord = new StringBuilder();
                        for (int charIndex : charIndexes) {
                            possibleWord.append(board.getLetter(charIndex / colsNumber, charIndex % colsNumber));
                        }
                        final String word = possibleWord.toString();
                        if (dictionarySet.contains(word)) {
                            foundWords.put(word, foundWordTreeValue++);
                        }
                    }
                }
            }
        }
        return foundWords.keys();
    }

    private void addBottomNeighbor(Graph boardDigraph, int currentIndex, int colsNumber) {
        boardDigraph.addEdge(currentIndex, currentIndex + colsNumber);
    }

    private void addRightNeighbor(Graph boardDigraph, int currentIndex) {
        boardDigraph.addEdge(currentIndex, currentIndex + 1);
    }

    private void addRightBottomDiagonalNeighbor(Graph boardDigraph, int currentIndex, int colsNumber) {
        boardDigraph.addEdge(currentIndex, currentIndex + colsNumber + 1);
    }

    private void addLeftBottomDiagonalNeighbor(Graph boardDigraph, int currentIndex, int colsNumber) {
        boardDigraph.addEdge(currentIndex, currentIndex + colsNumber - 1);
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (word == null || word.isEmpty()) {
            return 0;
        }
        final int wordLength = word.length();
        if (wordLength < 3) {
            return 0;
        } else if (wordLength < 5) {
            return 1;
        } else if (wordLength < 7) {
            return 3;
        } else if (wordLength < 8) {
            return 5;
        } else {
            return 11;
        }
    }

    /*************************************************************************
     *  Compilation:  javac BreadthFirstPaths.java
     *  Execution:    java BreadthFirstPaths G s
     *  Dependencies: Graph.java Queue.java Stack.java StdOut.java
     *  Data files:   http://algs4.cs.princeton.edu/41undirected/tinyCG.txt
     *
     *  Run breadth first search on an undirected graph.
     *  Runs in O(E + V) time.
     *
     *  %  java Graph tinyCG.txt
     *  6 8
     *  0: 2 1 5
     *  1: 0 2
     *  2: 0 1 3 4
     *  3: 5 4 2
     *  4: 3 2
     *  5: 3 0
     *
     *  %  java BreadthFirstPaths tinyCG.txt 0
     *  0 to 0 (0):  0
     *  0 to 1 (1):  0-1
     *  0 to 2 (1):  0-2
     *  0 to 3 (2):  0-2-3
     *  0 to 4 (2):  0-2-4
     *  0 to 5 (1):  0-5
     *
     *  %  java BreadthFirstPaths largeG.txt 0
     *  0 to 0 (0):  0
     *  0 to 1 (418):  0-932942-474885-82707-879889-971961-...
     *  0 to 2 (323):  0-460790-53370-594358-780059-287921-...
     *  0 to 3 (168):  0-713461-75230-953125-568284-350405-...
     *  0 to 4 (144):  0-460790-53370-310931-440226-380102-...
     *  0 to 5 (566):  0-932942-474885-82707-879889-971961-...
     *  0 to 6 (349):  0-932942-474885-82707-879889-971961-...
     *
     *************************************************************************/


    /**
     * The <tt>BreadthFirstPaths</tt> class represents a data type for finding
     * shortest paths (number of edges) from a source vertex <em>s</em>
     * (or a set of source vertices)
     * to every other vertex in an undirected graph.
     * <p/>
     * This implementation uses breadth-first search.
     * The constructor takes time proportional to <em>V</em> + <em>E</em>,
     * where <em>V</em> is the number of vertices and <em>E</em> is the number of edges.
     * It uses extra space (not including the graph) proportional to <em>V</em>.
     * <p/>
     * For additional documentation, see <a href="/algs4/41graph">Section 4.1</a> of
     * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
     *
     * @author Robert Sedgewick
     * @author Kevin Wayne
     */
    private class BreadthFirstAllPaths {
        private boolean[] marked;  // marked[v] = is there an s-v path
        private int[] edgeTo;      // edgeTo[v] = previous edge on shortest s-v path
        private Map<Integer, Set<List<Integer>>> paths;

        /**
         * Computes the shortest path between the source vertex <tt>s</tt>
         * and every other vertex in the graph <tt>G</tt>.
         *
         * @param G the graph
         * @param s the source vertex
         */
        public BreadthFirstAllPaths(Graph G, int s) {
            marked = new boolean[G.V()];
            edgeTo = new int[G.V()];
            paths = new HashMap<Integer, Set<List<Integer>>>();
            bfs(G, s);
        }

        // breadth-first search from a single source
        private void bfs(Graph G, int s) {
            Queue<Integer> q = new Queue<Integer>();
            marked[s] = true;
            q.enqueue(s);

            while (!q.isEmpty()) {
                int v = q.dequeue();
                final Set<List<Integer>> currentPaths = paths.get(v);
                for (int w : G.adj(v)) {
                    edgeTo[w] = v;
                    Set<List<Integer>> newPaths;
                    if (paths.containsKey(w)) {
                        newPaths = new HashSet<List<Integer>>(paths.get(w));
                    } else {
                        newPaths = new HashSet<List<Integer>>();
                    }
                    if (currentPaths == null || currentPaths.isEmpty()) {
                        List<Integer> newPath = new ArrayList<Integer>();
                        newPath.add(v);
                        newPaths.add(newPath);
                    } else {
                        for (List<Integer> path : currentPaths) {
                            List<Integer> newPath = new ArrayList<Integer>(path);
                            newPath.add(v);
                            newPaths.add(newPath);
                        }
                    }
                    paths.put(w, newPaths);
                    if (!marked[w]) {
                        q.enqueue(w);
                    }
                    marked[w] = true;
                }
            }
        }

        /**
         * Returns a shortest path between the source vertex <tt>s</tt> (or sources)
         * and <tt>v</tt>, or <tt>null</tt> if no such path.
         *
         * @param v the vertex
         * @return the sequence of vertices on a shortest path, as an Iterable
         */
        public Set<List<Integer>> pathsTo(int v) {
            if (paths.containsKey(v)) {
                return paths.get(v);
            }
            return Collections.emptySet();
        }


    }

}

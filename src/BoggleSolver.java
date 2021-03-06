import java.util.*;

public class BoggleSolver {
    private BoggleBoard board;
    private SET<String> dictionarySet;
    private Set<String> foundWords;
    private int colsNumber;
    private TST<Integer> dictionaryTST;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        dictionarySet = new SET<String>();
        dictionaryTST = new TST<Integer>();
        int tstValue = 0;
        for (String word : dictionary) {
            dictionarySet.add(word);
            dictionaryTST.put(word, tstValue++);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        foundWords = new HashSet<String>();
        this.board = board;
        final int rowsNumber = board.rows();
        colsNumber = board.cols();
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
        for (int i = 0; i < totalBoardSize; i++) {
            dfsAllPaths(boardDigraph, i);
        }
        return foundWords;
    }

    private boolean fillFinalWordLists(int graphIndex, List<Integer> charIndexes) {
        StringBuilder possibleWord = new StringBuilder();
        char newLetter;
        for (int charIndex : charIndexes) {
            newLetter = board.getLetter(charIndex / colsNumber, charIndex % colsNumber);
            possibleWord.append(newLetter);
            appendUtoQ(possibleWord, newLetter);
        }
        newLetter = board.getLetter(graphIndex / colsNumber, graphIndex % colsNumber);
        possibleWord.append(newLetter);
        appendUtoQ(possibleWord, newLetter);

        final String word = possibleWord.toString();
        if (dictionarySet.contains(word)) {
            if (word.length() > 2) {
                foundWords.add(word);
            }
            return true;
        }
        final Iterable<String> strings = dictionaryTST.prefixMatch(word);
        return strings.iterator().hasNext();
    }

    private void appendUtoQ(StringBuilder possibleWord, char newLetter) {
        if ('Q' == newLetter) {
            possibleWord.append('U');
        }
    }

    private Map<Integer, Set<List<Integer>>> dfsAllPaths(Graph boardDigraph, int v) {
        Map<Integer, Set<List<Integer>>> allPaths = new HashMap<Integer, Set<List<Integer>>>();
        dfs(boardDigraph, v, allPaths, new ArrayList<Integer>());
        return allPaths;
    }

    private void dfs(Graph boardDigraph, int v, Map<Integer, Set<List<Integer>>> allPaths, List<Integer> visitedNodes) {
        visitedNodes.add(v);
        for (int w : boardDigraph.adj(v)) {
            if (!visitedNodes.contains(w)) {
                boolean needToContinue = false;
                if (allPaths.containsKey(w)) {
                    Set<List<Integer>> pathsToW = allPaths.get(w);
                    if (fillAllPaths(v, allPaths, pathsToW, w)) {
                        needToContinue = true;
                    }
                } else {
                    Set<List<Integer>> pathsToW = new HashSet<List<Integer>>();
                    if (fillAllPaths(v, allPaths, pathsToW, w)) {
                        needToContinue = true;
                    }
                    allPaths.put(w, pathsToW);
                }
                if (needToContinue) {
                    dfs(boardDigraph, w, allPaths, new ArrayList<Integer>(visitedNodes));
                }
            }
        }
    }

    private boolean fillAllPaths(int v, Map<Integer, Set<List<Integer>>> allPaths, Set<List<Integer>> pathsToW, int w) {
        final Set<List<Integer>> pathsToV = allPaths.get(v);
        boolean needToContinue = false;
        if (pathsToV == null || pathsToV.isEmpty()) {
            List<Integer> pathToW = new ArrayList<Integer>();
            pathToW.add(v);
            if (fillFinalWordLists(w, pathToW)) {
                pathsToW.add(pathToW);
                needToContinue = true;
            }
        } else {
            for (List<Integer> pathToV : pathsToV) {
                if (!pathToV.contains(w)) {
                    List<Integer> pathToW = new ArrayList<Integer>(pathToV);
                    pathToW.add(v);
                    if (fillFinalWordLists(w, pathToW)) {
                        pathsToW.add(pathToW);
                        needToContinue = true;
                    }
                }
            }
        }
        return needToContinue;
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


}

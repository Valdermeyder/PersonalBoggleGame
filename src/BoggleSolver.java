import java.util.*;

public class BoggleSolver {
    private SET<String> dictionarySet;
    private List<String> foundWords;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        dictionarySet = new SET<String>();
        for (String word : dictionary) {
            dictionarySet.add(word);
        }
        foundWords = new ArrayList<String>();
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
        for (int i = 0; i < totalBoardSize; i++) {
            Map<Integer, Set<List<Integer>>> breadthFirstAllPaths = dfsAllPaths(boardDigraph, i);
            for (int j = 0; j < totalBoardSize; j++) {
                if (i != j) {
                    final Set<List<Integer>> allPaths = breadthFirstAllPaths.get(j);
                    for (List<Integer> charIndexes : allPaths) {
                        StringBuilder possibleWord = new StringBuilder();
                        for (int charIndex : charIndexes) {
                            possibleWord.append(board.getLetter(charIndex / colsNumber, charIndex % colsNumber));
                        }
                        possibleWord.append(board.getLetter(j / colsNumber, j % colsNumber));
                        final String word = possibleWord.toString();
                        if (dictionarySet.contains(word)) {
                            foundWords.add(word);
                        }
                    }
                }
            }
        }
        return foundWords;
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
                if (allPaths.containsKey(w)) {
                    Set<List<Integer>> pathsToW = allPaths.get(w);
                    fillAllPaths(v, allPaths, pathsToW, w);
                } else {
                    Set<List<Integer>> pathsToW = new HashSet<List<Integer>>();
                    fillAllPaths(v, allPaths, pathsToW, w);
                    allPaths.put(w, pathsToW);
                }
                dfs(boardDigraph, w, allPaths, new ArrayList<Integer>(visitedNodes));
            }
        }
    }

    private void fillAllPaths(int v, Map<Integer, Set<List<Integer>>> allPaths, Set<List<Integer>> pathsToW, int w) {
        final Set<List<Integer>> pathsToV = allPaths.get(v);
        if (pathsToV == null || pathsToV.isEmpty()) {
            List<Integer> pathToW = new ArrayList<Integer>();
            pathToW.add(v);
            pathsToW.add(pathToW);
        } else {
            for (List<Integer> pathToV : pathsToV) {
                if (!pathToV.contains(w)) {
                    List<Integer> pathToW = new ArrayList<Integer>(pathToV);
                    pathToW.add(v);
                    pathsToW.add(pathToW);
                }
            }
        }
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

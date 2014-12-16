import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BoggleSolverTest {
    private BoggleSolver solver;
    private BoggleBoard board;

    private void readFiles(String dictionaryFileName, String boardFileName) {
        In in = new In(dictionaryFileName);
        String[] dictionary = in.readAllStrings();
        solver = new BoggleSolver(dictionary);
        board = new BoggleBoard(boardFileName);
    }

    private int calculateBoggleSolverScore() {
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            score += solver.scoreOf(word);
        }
        return score;
    }

    @Test
    public void testBoard4x4WithDictionaryAlgs4() {
        readFiles("dictionary-algs4.txt", "board4x4.txt");
        assertEquals("Score for Boggle Solver is wrong", 33, calculateBoggleSolverScore());
    }

    @Test
    public void testBoard2x2WithDictionaryAlgs4() {
        readFiles("dictionary-algs4.txt", "board2x2.txt");
        assertEquals("Score for Boggle Solver is wrong", 2, calculateBoggleSolverScore());
    }

    @Test
    public void testBoard3x3WithDictionaryAlgs4() {
        readFiles("dictionary-algs4.txt", "board3x3.txt");
        assertEquals("Score for Boggle Solver is wrong", 11, calculateBoggleSolverScore());
    }
}

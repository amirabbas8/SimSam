import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class nPuzzle {
    private static int nPuzzle = 4;
    private static int[][] goalState;

    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(new File(System.getProperty("user.dir") + "/input.txt"));
            nPuzzle = sc.nextInt();
            int[][] initialState = new int[nPuzzle][nPuzzle];
            goalState = new int[nPuzzle][nPuzzle];
            for (int i = 0; i < nPuzzle; i++) {
                for (int j = 0; j < nPuzzle; j++) {
                    initialState[i][j] = sc.nextInt();
                }
            }
            sc = new Scanner(new File(System.getProperty("user.dir") + "/output.txt"));
            for (int i = 0; i < nPuzzle; i++) {
                for (int j = 0; j < nPuzzle; j++) {
                    goalState[i][j] = sc.nextInt();
                }
            }
            searchAStar(initialState, goalState);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private static void searchAStar(int[][] initial, int[][] goal) {
        Node root = new Node(null, initial, 0);
        PriorityQueue<Node> pQueueA = new PriorityQueue<>();
        pQueueA.add(root);
        HashMap<String, Node> visited = new HashMap<>();
        while (!pQueueA.isEmpty()) {
            Node current = pQueueA.remove();
            current.setMisplaced();
            if (Arrays.deepEquals(copyArray(current.getState()), goal)) {
                System.out.println("0:");
                printTable(initial);
                printPath(current);
                break;
            }
            if (!visited.containsKey(Arrays.deepToString(current.getState()))) {
                visited.put(Arrays.deepToString(current.getState()), current);
                ArrayList<Node> children = expandNodes(current);
                pQueueA.addAll(children);
            }
        }
    }

    private static void printTable(int[][] a) {

        for (int i = 0; i < nPuzzle; i++) {
            System.out.println();

            for (int j = 0; j < nPuzzle; j++) {
                System.out.print(" " + a[i][j] + "");
            }
        }
        System.out.println("\n");
    }

    private static ArrayList<Node> createChildren(Node parent, boolean right, boolean left, boolean up, boolean down) {
        int x = parent.getX();
        int y = parent.getY();
        ArrayList<Node> children = new ArrayList<>();
        if (right) {
            Node node = new Node(parent, moveRight(x, y, copyArray(parent.getState())), parent.getLevel() + 1);
            node.setMisplaced();
            children.add(node);
        }
        if (left) {
            Node node = new Node(parent, moveLeft(x, y, copyArray(parent.getState())), parent.getLevel() + 1);
            node.setMisplaced();
            children.add(node);
        }
        if (up) {
            Node node = new Node(parent, moveUp(x, y, copyArray(parent.getState())), parent.getLevel() + 1);
            node.setMisplaced();
            children.add(node);
        }
        if (down) {
            Node node = new Node(parent, moveDown(x, y, copyArray(parent.getState())), parent.getLevel() + 1);
            node.setMisplaced();
            children.add(node);
        }
        return children;
    }

    private static ArrayList<Node> expandNodes(Node node) {
        node.findEmptyTile(node.getState());
        int x = node.getX();
        int y = node.getY();
        if (x == nPuzzle - 1 && y == 0) {
            return createChildren(node, true, false, true, false);
        }
        if (x == 0 && y == 0) {
            return createChildren(node, true, false, false, true);
        }
        if (y == 0) {
            return createChildren(node, true, false, true, true);
        }

        if (x == nPuzzle - 1 && y == nPuzzle - 1) {
            return createChildren(node, false, true, true, false);
        }
        if (x == 0 && y == nPuzzle - 1) {
            return createChildren(node, false, true, false, true);
        }
        if (y == nPuzzle - 1) {
            return createChildren(node, false, true, true, true);
        }

        if (x == nPuzzle - 1) {
            return createChildren(node, true, true, true, false);
        }
        if (x == 0) {
            return createChildren(node, true, true, false, true);
        }
        return createChildren(node, true, true, true, true);
    }


    private static int[][] moveRight(int row, int column, int[][] state) {
        int tile = state[row][column];
        state[row][column] = state[row][column + 1];
        state[row][column + 1] = tile;
        return state;
    }

    private static int[][] moveLeft(int row, int column, int[][] state) {
        int tile = state[row][column];
        state[row][column] = state[row][column - 1];
        state[row][column - 1] = tile;
        return state;
    }

    private static int[][] moveUp(int row, int column, int[][] state) {
        int tile = state[row][column];
        state[row][column] = state[row - 1][column];
        state[row - 1][column] = tile;
        return state;
    }

    private static int[][] moveDown(int row, int column, int[][] state) {
        int tile = state[row][column];
        state[row][column] = state[row + 1][column];
        state[row + 1][column] = tile;
        return state;
    }


    private static int[][] copyArray(int[][] b) {
        int[][] a = new int[b.length][b.length];
        for (int i = 0; i < b.length; i++) {
            System.arraycopy(b[i], 0, a[i], 0, b.length);
        }
        return a;
    }


    private static void printPath(Node n) {
        ArrayList<int[][]> tables = new ArrayList<>();
        while (n.getParent() != null) {
            tables.add(0, n.getState());
            n = n.getParent();
        }
        for (int i = 0; i < tables.size(); i++) {
            System.out.println(i + 1 + ":");
            printTable(tables.get(i));
        }
    }

    public static class Node implements Comparable<Node> {
        private int[][] state;
        private int misplacedNum;
        private int level;
        private Node parent;
        private int x, y;

        Node(Node parent, int[][] state, int level) {
            this.parent = parent;
            this.state = state;
            this.level = level;
        }

        void setMisplaced() {
            misplacedNum = compare() + level;
        }

        Node getParent() {
            return parent;
        }

        int getLevel() {
            return level;
        }

        int[][] getState() {
            return state;
        }

        int getX() {
            return x;
        }

        int getY() {
            return y;
        }

        private int compare() {
            int m = 0;
            for (int i = 0; i < puzzle.goalState.length; i++)
                for (int j = 0; j < puzzle.goalState.length; j++) {
                    if (state[i][j] == 0) {
                        continue;
                    }
                    if (state[i][j] != puzzle.goalState[i][j]) {
                        m++;
                    }
                }
            return m;
        }

        void findEmptyTile(int[][] b) {
            for (int i = 0; i < puzzle.nPuzzle; i++)
                for (int j = 0; j < puzzle.nPuzzle; j++)
                    if (b[i][j] == 0) {
                        x = i;
                        y = j;
                        break;
                    }

        }

        @Override
        public int compareTo(Node o) {
            return Integer.compare(this.misplacedNum, o.misplacedNum);
        }
    }
}

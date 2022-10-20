package tictactoe;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Scanner scanner = new Scanner(System.in);
        String input = "";
        int cmd;
        do {
            cmd = CommandsInput.start(); // 0 1 2
            switch (cmd) {
                case 0:// exit
                    break;
                case 1:// play game
                    TicTacToeGame game = new TicTacToeGame(input, Signs._X,
                            CommandsInput.playerA, CommandsInput.playerB);
                    game.showTable();
                    game.checkInitStage(input);
                    while (!game.endGame()) {
                        game.play();
                    }
                    break;
                case 2:// bad start commands
                    break;
            }
        } while (cmd != 0);
    }
}

enum PlayerType {
    user, easy, medium, hard
}

enum Commands {
    start, exit
}

class Player {
    private PlayerType type;
    private Signs sign;

    public Player(PlayerType type, Signs sign) {
        this.type = type;
        this.sign = sign;
    }

    public PlayerType getType() {
        return type;
    }

    public void setType(PlayerType type) {
        this.type = type;
    }

    public Signs getSign() {
        return sign;
    }

    public void setSign(Signs sign) {
        this.sign = sign;
    }
}

class CommandsInput {
    static Commands command;
    static PlayerType playerA;
    static PlayerType playerB;
    static int start() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Input command: ");
        try {
            String[] inputCmd = scanner.nextLine().split(" ");
            command = Commands.valueOf(inputCmd[0]);
            if (inputCmd.length == 1 && command == Commands.exit) {
                return 0;
            } else if (inputCmd.length == 3 && command == Commands.start) {
                playerA = PlayerType.valueOf(inputCmd[1]);
                playerB = PlayerType.valueOf(inputCmd[2]);
                return 1;
            }
        } catch (NoSuchElementException | IllegalArgumentException e) {
            // do nothing
        }
        System.out.println("Bad parameters!");
        return 2;
    }
}

enum Signs {
    _X("X"), _O("O"), __(" ");

    private final String name;

    public String getName() {
        return name;
    }

    Signs(String name) {
        this.name = name;
    }
}

class PseRanNumber {
    // included lower and higher
    public static int ranNumber(int lower, int higher) {
        Random ran = new Random();
        int numLength = higher - lower + 1;
        return ran.nextInt(numLength) + lower;
    }
}

class Move {
    private int index;
    private int score;

    public Move() {
    }

    public Move(int index, int score) {
        this.index = index;
        this.score = score;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}

class TicTacToeGame {
    private final String[] table;
    private int steps;
    private boolean validateInput;
    private final int[] nextPoint = new int[2];
    private final Player player;
    private final PlayerType playerA;
    private final PlayerType playerB;
    private boolean hasWinner;
    private String winner;
    private static final String C_SPACE = " ";
    private static final String C_UNDER = "_";
    private static final int[][] winData = new int[][] {
            {0,1,2},
            {3,4,5},
            {6,7,8},
            {0,3,6},
            {1,4,7},
            {2,5,8},
            {0,4,8},
            {2,4,6}
    };

    public TicTacToeGame(String table, Signs sign, PlayerType playerA, PlayerType playerB) {
        this.table = checkInitTable(table)
                ? table.trim().replace(C_UNDER, C_SPACE).split("") : new String[] {
                C_SPACE,C_SPACE,C_SPACE,C_SPACE,C_SPACE,C_SPACE,C_SPACE,C_SPACE,C_SPACE
        };
        this.playerA = playerA;
        this.playerB = playerB;
        this.player = new Player(playerA, sign);// first player
    }

    private boolean checkInitTable(String xo) {
        if (xo == null || xo.isBlank() || xo.trim().length() != 9) {
            return false;
        }
        try {
            String[] arrTable = xo.trim().split("");
            for (String s : arrTable) {
                Signs.valueOf(C_UNDER + s);
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public void checkInitStage(String xo) {
        String[] tmp = xo.replace(C_UNDER,"").split("");
        if (tmp.length >= 5) { // maybe has winner
            checkWinner();
        }
    }

    public void showTable() {
        final String line1 = "---------";
        final String line2 = "| ";
        final String line3 = " |";
        steps = 0;
        showNewLine(line1);
        for (int i = 0; i < table.length; i++) {
            // layout
            if (i == 0 || i == 3 || i == 6) {
                show(line2 + table[i]);
            } else if (i == 2 || i == 5 || i == 8) {
                showNewLine(C_SPACE + table[i] + line3);
            } else {
                show(C_SPACE + table[i]);
            }
            if (table[i].equals(Signs._X.getName())
                    || table[i].equals(Signs._O.getName())) {
                steps++;// counts X O
            }
        }
        showNewLine(line1);
    }

    public void play() {
        while (true) {
            move(player);
            showTable();
            if (checkWinner()) return;
            changePlayer();
        }
    }

    private void move(Player player) {
        switch (player.getType()) {
            case user -> playByPlayer();
            case easy -> playWitRanNumber();
            case medium -> playWitAI();
            case hard -> playWitMinimaxAI();
        }
    }

    private void changePlayer(){
        player.setSign(player.getSign() == Signs._X ? Signs._O : Signs._X);
        player.setType(player.getType() == playerA ? playerB : playerA);
    }

    private void playByPlayer() {
        do {
            inputData();
        } while (!validateInput);
        makeAMove(nextPoint[0], nextPoint[1]);

    }

    private void inputData() {
        Scanner sc = new Scanner(System.in);
        show("Enter the coordinates: ");
        int count = 0;
        try {
            validateInput = false;
            while (count < 2) {
                if (sc.hasNextInt()) {
                    nextPoint[count] = sc.nextInt();
                    count++;
                } else {
                    break;
                }
            }
            if (count != 2) {
                showNewLine("You should enter numbers!");
            } else if (nextPoint[0] <= 0 || nextPoint[0] > 3
                    || nextPoint[1] <= 0 || nextPoint[1] > 3) {
                showNewLine("Coordinates should be from 1 to 3!");
            } else {
                int number = buildNumber(nextPoint[0], nextPoint[1]);
                if (!table[number].equals(C_SPACE)) {
                    showNewLine("This cell is occupied! Choose another one!");
                } else {
                    validateInput = true;
                }
            }
        } catch (Exception e) {
            showNewLine("You should enter numbers!");
        }
    }

    private void makeAMove(int x, int y) {
        int number = buildNumber(x, y);
        updateTable(number);
    }

    private void updateTable(int point) {
        table[point] = player.getSign().getName();
    }

    private int buildNumber(int x, int y) {
        return (x == 1) ? y - 1 : 3 * (x - 1) + y - 1;
    }

    private void playWitAI() {
        // winning move
        int aiSelNumber = findLineCanWin(player.getSign());
        if (aiSelNumber < 0) {
            // against winning move
            aiSelNumber = findLineCanWin(findNexPlaSign());
            if (aiSelNumber < 0) {
                // random move
                aiSelNumber = getRanNumber();
            }
        }
        updateTable(aiSelNumber);
        displayAIMessage();
    }

    private int findLineCanWin(Signs sign) {
        int xCount = 0, _Count = 0, tmpPoint = -1, point = -1;
        out:
        for (int[] i : winData) {
            for (int j : i) {
                if (sign.getName().equals(table[j])) {
                    xCount++;
                }
                if (Signs.__.getName().equals(table[j])) {
                    _Count++;
                    tmpPoint = j;
                }
                if (xCount == 2 && _Count == 1) {
                    point = tmpPoint;
                    break out;
                }
            }
            xCount = 0;
            _Count = 0;
            tmpPoint = -1;
        }
        return point;
    }

    private void playWitMinimaxAI() {
        String[] newTable = reformatTable(table);
        Move minimaxBestMove = minimaxAlgorithm(newTable, player.getSign());
        updateTable(minimaxBestMove.getIndex());
        displayAIMessage();
    }

    private String[] reformatTable(String[] table) {
        String[] newTable = new String[table.length];
        for (int i = 0; i < table.length; i++) {
            if (table[i].equals(C_SPACE)) {
                newTable[i] = String.valueOf(i);
            } else {
                newTable[i] = table[i];
            }
        }
        return newTable;
    }

    private Move minimaxAlgorithm(String[] newTable, Signs playTurn) {
        int[] availSpots = emptyIndex(newTable);

        switch (whoCanWin(newTable)) {
            case _X:
                return new Move(-1, 10);
            case _O:
                return new Move(-1, -10);
        }
        if (availSpots.length == 0) {
            return new Move(-1, 0);
        }

        List<Move> moves = new ArrayList<>();
        for (int availSpot : availSpots) {
            Move move = new Move();
            move.setIndex(Integer.parseInt(newTable[availSpot]));
            newTable[availSpot] = playTurn.getName();

            Move sc;
            switch (playTurn) {
                case _X -> {
                    sc = minimaxAlgorithm(newTable, Signs._O);
                    move.setScore(sc.getScore());
                }
                case _O -> {
                    sc = minimaxAlgorithm(newTable, Signs._X);
                    move.setScore(sc.getScore());
                }
            }
            newTable[availSpot] = String.valueOf(move.getIndex());
            moves.add(move);
        }

        Move bestMove = new Move();
        int bestSc;
        switch (playTurn) {
            case _X -> {
                bestSc = -1000;
                for (Move m : moves) {
                    if (m.getScore() > bestSc) {
                        bestSc = m.getScore();
                        bestMove = m;
                    }
                }
            }
            case _O -> {
                bestSc = 1000;
                for (Move m : moves) {
                    if (m.getScore() < bestSc) {
                        bestSc = m.getScore();
                        bestMove = m;
                    }
                }
            }
        }

        return bestMove;
    }

    private int[] emptyIndex(String[] newTable) {
        StringBuilder s = new StringBuilder();
        for (String value : newTable) {
            if (!value.equals(Signs._X.getName())
                    && !value.equals(Signs._O.getName())) {
                s.append(value).append(C_SPACE);
            }
        }
        String rs = s.toString().trim();
        if (rs.isEmpty()) {
            return new int[0];
        }
        String[] tb = rs.split(C_SPACE);
        int[] emptyPoint = new int[tb.length];
        for (int i = 0; i < tb.length; i++) {
            emptyPoint[i] = Integer.parseInt(tb[i]);
        }
        return emptyPoint;
    }

    private Signs whoCanWin(String[] newTable) {
        int winIndex = win(newTable);
        return winIndex >= 0 ? Signs.valueOf(C_UNDER + newTable[winIndex]) : Signs.__;
    }

    private Signs findNexPlaSign() {
        return player.getSign() == Signs._X ? Signs._O : Signs._X;
    }

    private void playWitRanNumber() {
        updateTable(getRanNumber());
        displayAIMessage();
    }

    private int getRanNumber() {
        int aiSelNumber = PseRanNumber.ranNumber(0, table.length - 1);
        while (!table[aiSelNumber].equals(C_SPACE)) {
            aiSelNumber = PseRanNumber.ranNumber(0, table.length - 1);
        }
        return aiSelNumber;
    }

    private void displayAIMessage(){
        showNewLine("Making move level \"" + showAiName() + "\"");
    }

    private String showAiName() {
        return player.getType().name();
    }

    private boolean checkWinner() {
        if (findWinner()) {
            showNewLine(winner + " wins");
            return true;
        } else if (steps == table.length) {
            showNewLine("Draw");
            return true;
        }
        //showNewLine("Game not finished");
        return false;
    }

    private boolean findWinner() {
        hasWinner = false;
        int i = win(table);
        if (i >= 0) {
            hasWinner = true;
            winner = table[i];
        }
        return hasWinner;
    }

    private int win(String[] table) {
        for (int[] i : winData) {
            if (!table[i[0]].equals(Signs.__.getName())
                    && table[i[0]].equals(table[i[1]]) && table[i[0]].equals(table[i[2]])) {
                return i[0];
            }
        }
        return -1;
    }

    public boolean endGame() {
        return (hasWinner || steps == table.length);
    }

    private void showNewLine(String text) {
        System.out.println(text);
    }
    private void show(String text) {
        System.out.print(text);
    }
}
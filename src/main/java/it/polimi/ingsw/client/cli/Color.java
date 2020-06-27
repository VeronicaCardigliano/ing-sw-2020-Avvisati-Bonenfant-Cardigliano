package it.polimi.ingsw.client.cli;

public enum Color {

    ANSI_MAGENTA ("\u001B[35m"),
    ANSI_LIGHTBLUE ("\u001B[36m"),
    ANSI_GREEN ("\u001B[32m"),
    ANSI_YELLOW("\u001b[33m"),
    ANSI_RED("\u001b[31m"),
    ANSI_BLUE("\u001b[34m");

    public static final String RESET = "\u001B[0m";
    private String escape;

    Color (String escape) {
        this.escape = escape;
    }
    public String escape() {
        return escape;
    }

    public static String returnColor (String player) {
        String color = "";
        switch (player) {
            case "MAGENTA":
                color = Color.ANSI_MAGENTA.escape();
                break;
            case "WHITE":
                color = "";
                break;
            case "LIGHT_BLUE":
                color =  Color.ANSI_LIGHTBLUE.escape();
                break;
        }
        return color;
    }
}

package it.polimi.ingsw.client;

public enum Color {

        ANSI_GREY ("\u001B[37m"),
        ANSI_LIGHTBLUE ("\u001B[36m"),
        ANSI_GREEN ("\u001B[32m"),
        ANSI_YELLOW("\u001b[33m"),
        ANSI_BLUE("\u001b[34m");

        static final String RESET = "\u001B[0m";
        private String escape;

        Color (String escape) {
            this.escape = escape;
        }
        public String escape() {
            return escape;
        }
}

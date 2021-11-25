package main.java.com.wdhays.gol;

public class Cell1 {


        //Change these characters to change what represents a living cell and what represents a dead cell
        public static final char aliveChar = '+';
        public static final char deadChar = '=';

        public Boolean alive; //One field - whether it is alive or not
        public Cell1(Boolean alive) {
            this.alive = alive;
        }



}

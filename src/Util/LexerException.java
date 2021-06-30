package Util;

public class LexerException extends RuntimeException {

    public LexerException(String msg, int position) {
        super(String.format("positon %d: " +  msg, position));
    }
}

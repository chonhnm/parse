package lexer.reg;

import java.util.HashMap;
import java.util.Map;

public class NFA {

    public final static State ERROR = new State('0', -1);
    private char c;
    private State start;
    private State accept;
    private Map<State, Map<Character, State>> trans = new HashMap<>(2);


    private final String input;
    private final int len;

    public NFA(String input) {
        this.input = input;
        len = input.length();
    }

    public void init(char c) {
        this.c = c;
        start = new State(c, 0);
        accept = new State(c ,1);
        trans.computeIfAbsent(start, k->new HashMap<>(2)).put(c, accept);
    }

    public State transfer(State s, char c) {
        Map<Character, State> map = trans.get(s);
        if (map == null) return ERROR;
        return map.getOrDefault(c, ERROR);
    }

    public char getC() {
        return c;
    }

    public State getStart() {
        return start;
    }

    public State getAccept() {
        return accept;
    }

    public Map<State, Map<Character, State>> getTrans() {
        return trans;
    }

    public void setC(char c) {
        this.c = c;
    }

    public void setStart(State start) {
        this.start = start;
    }

    public void setAccept(State accept) {
        this.accept = accept;
    }

    public static NFA concat(NFA n1, NFA n2) {
//        NFA nfa = new NFA();
//        nfa.c = '.';
//        nfa.start = n1.start;
//        nfa.accept = n2.accept;
//        nfa.trans.putAll(n1.trans);
//        nfa.trans.putAll(n2.trans);
//        nfa.trans.computeIfAbsent(n1.accept, k->new HashMap<>(2)).put('\0', n2.start);
//        return nfa;
        return null;
    }

}

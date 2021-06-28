package lexer.reg;

import Util.IOUtil;

import java.io.IOException;
import java.util.*;

public class TextSearchMachine {

    private final String[] keys;
    private int newState;

    private final Map<Integer, Map<Character, Integer>> map;
    private final Map<Integer, Set<String>> output;
    private int[] fail;

    public TextSearchMachine(String[] keys) {
        this.keys = keys;
        map = new HashMap<>(keys.length * 10);
        output = new HashMap<>(keys.length);
        newState = 0;
        init();
    }

    private void init() {
        buildGo();
        buildFail();
    }

    private void buildFail() {
        fail = new int[newState + 1];
        Map<Character, Integer> startMap = map.get(0);
        Queue<Integer> queue = new ArrayDeque<>(newState / 2);
        for (Integer st : map.get(0).values()) {
            queue.add(st);
            fail[st] = 0;
        }
        Integer r;
        while ((r = queue.poll()) != null) {
            Map<Character, Integer> nextMap = map.get(r);
            if (nextMap != null) {
                for (Map.Entry<Character, Integer> entry : nextMap.entrySet()) {
                    Integer s = entry.getValue();
                    Character c = entry.getKey();
                    queue.add(s);
                    int st = fail(r);
                    while (go(st, c) == -1) {
                        st = fail(st);
                    }
                    fail[s] = go(st, c);
                    Set<String> output = output(fail(s));
                    if (output != null) {
                        output(s).addAll(output);
                    }
                }
            }
        }
    }

    private void buildGo() {
        for (String key : keys) {
            enter(key);
        }
    }

    private void enter(String key) {
        char[] chars = key.toCharArray();
        int length = chars.length;
        if (length == 0) {
            return;
        }
        int state = 0;
        int i = 0;
        Integer st;
        while ((st = _go(state, chars[i])) != null) {
            state = st;
            i++;
        }
        for (; i < length; i++) {
            setGo(state, chars[i], ++newState);
            state = newState;
        }
        output.computeIfAbsent(state, k -> new HashSet<>(1)).add(key);
    }

    private Integer _go(int state, char c) {
        Map<Character, Integer> m = map.get(state);
        return m == null ? null : m.get(c);
    }

    private void setGo(int state, char c, int newState) {
        map.computeIfAbsent(state, k -> new HashMap<>(2)).put(c, newState);
    }

    public int go(int state, char c) {
        Map<Character, Integer> integerMap = map.get(state);
        if (integerMap == null) return state == 0 ? 0 : -1;
        Integer st = integerMap.get(c);
        return Objects.requireNonNullElseGet(st, () -> state == 0 ? 0 : -1);
    }

    public int fail(int state) {
        return fail[state];
    }

    public Set<String> output(int state) {
        return output.get(state);
    }

    public Map<Integer, Set<String>> search(String txt) {
        Map<Integer, Set<String>> result = new LinkedHashMap<>();
        int state = 0;
        char[] cs = txt.toCharArray();
        char c;
        Set<String> op;
        for (int i = 0; i < cs.length; i++) {
            c = cs[i];
            while (go(state, c) == -1) {
                state = fail(state);
            }
            state = go(state, c);
            if (state != 0 && (op = output(state)) != null) {
                result.put(i, op);
            }
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        long t1 = System.currentTimeMillis();
        String[] keys = new String[]{" he ", " she ", " his ", " her "};
        TextSearchMachine machine = new TextSearchMachine(keys);
        String s = IOUtil.readAsString("/home/pig/wks/idea/ParseTech/src/pap.txt");
        Map<Integer, Set<String>> search = machine.search(s);
        long t2 = System.currentTimeMillis();
        System.out.println("cost: " + (t2 - t1)/ 1000);
        System.out.println();
    }

}

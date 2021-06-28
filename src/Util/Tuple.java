package Util;

import java.util.Objects;

public class Tuple<U, V> {
    public final U t1;
    public final V t2;

    public Tuple(U u, V v) {
        this.t1 = u;
        this.t2 = v;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        return Objects.equals(t1, tuple.t1) && Objects.equals(t2, tuple.t2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(t1, t2);
    }

    @Override
    public String toString() {
        return String.format("[%s, %s]", t1, t2);
    }
}

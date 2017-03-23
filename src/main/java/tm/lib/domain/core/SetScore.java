package tm.lib.domain.core;

public class SetScore {

    public final int v1;
    public final int v2;

    public SetScore(int value1, int value2) {
        v1 = value1;
        v2 = value2;
    }

    public SetScore(SetScore other) {
        this(other.v1, other.v2);
    }

    public static SetScore of(int v1, int v2) {
        return new SetScore(v1, v2);
    }
    
    public SetScore normalized() {
        int d1 = (v1 > v2) ? 1 : 0;
        int d2 = (v2 > v1) ? 1 : 0;
        return new SetScore(d1, d2);
    }
    
    public SetScore summedWith(SetScore other) {
        return new SetScore(v1 + other.v1, v2 + other.v2);
    }
    
    public SetScore reversed() {
        return new SetScore(v2, v1);
    }

    @Override
    public String toString() {
        return v1 + ":" + v2;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + this.v1;
        hash = 97 * hash + this.v2;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SetScore other = (SetScore) obj;
        if (this.v1 != other.v1) {
            return false;
        }
        if (this.v2 != other.v2) {
            return false;
        }
        return true;
    }
}

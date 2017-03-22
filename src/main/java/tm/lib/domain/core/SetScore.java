package tm.lib.domain.core;

public class SetScore {

    public int v1;
    public int v2;

    public SetScore() {
        this(0, 0);
    }

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
        int d2 = (v1 > v2) ? 0 : 1;
        return new SetScore(d1, d2);
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

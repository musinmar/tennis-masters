package tm.lib.domain.core;

/**
 * A basic representation of score: points of one side and points for other side.
 */
public class BasicScore {

    public final int v1;
    public final int v2;

    public BasicScore(int value1, int value2) {
        v1 = value1;
        v2 = value2;
    }

    public BasicScore(BasicScore other) {
        this(other.v1, other.v2);
    }

    public static BasicScore of(int v1, int v2) {
        return new BasicScore(v1, v2);
    }
    
    public BasicScore normalized() {
        int d1 = (v1 > v2) ? 1 : 0;
        int d2 = (v2 > v1) ? 1 : 0;
        return new BasicScore(d1, d2);
    }
    
    public BasicScore summedWith(BasicScore other) {
        return new BasicScore(v1 + other.v1, v2 + other.v2);
    }
    
    public BasicScore reversed() {
        return new BasicScore(v2, v1);
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
        final BasicScore other = (BasicScore) obj;
        if (this.v1 != other.v1) {
            return false;
        }
        if (this.v2 != other.v2) {
            return false;
        }
        return true;
    }
}

package reversi;

public class Tuple<A,B> {
    A fir;
    B sec;

    public Tuple(A fir, B sec) {
        this.fir=fir;
        this.sec=sec;
    }

    public A first(){
        return fir;
    }

    public B second() {
        return sec;
    }
}

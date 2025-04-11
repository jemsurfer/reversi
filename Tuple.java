package reversi;

public class Tuple<A,B> {
    private A fir;
    private B sec;

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

    public void first(A in){
        fir=in;
    }

    public void second(B in){
        sec=in;
    }

    @Override
    public String toString(){
        return String.format("(%d,%d)", fir, sec);
    }
}

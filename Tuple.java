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

    //For debugging
    @Override
    public String toString(){
        return String.format("(%d,%d)", fir, sec);
    }

    //We need equals to use the distinct() method in ReversiController
    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() == this.getClass()){ 
            Tuple<A,B> other = (Tuple<A,B>) obj;
            return (other.first()==fir && other.second()==sec);
        }
        return false;
    }

    //Ditto
    @Override
    public int hashCode(){
        return toString().hashCode();
    }
}

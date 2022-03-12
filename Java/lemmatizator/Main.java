import java.util.concurrent.ForkJoinPool;

public class Main {
    public static void main(String[] args) {
        final String BASE_ADDR = "http://www.playback.ru/";
        Domen domen = new Domen(BASE_ADDR);
        new ForkJoinPool(20).invoke(domen);
    }
}

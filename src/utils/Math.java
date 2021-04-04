package utils;

public class Math {

    /**
     * @param d Число
     * @return Округленное в случае близости к 0 до 0 число
     */
    public static double roundNearZero(double d) {
        if (d < 0.0000001 && d > -0.0000001) {
            return 0;
        }
        return d;
    }
}

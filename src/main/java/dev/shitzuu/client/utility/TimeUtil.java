package dev.shitzuu.client.utility;

import java.util.concurrent.TimeUnit;

public final class TimeUtil {

    private TimeUtil() {

    }

    public static String toString(long time) {
        if (time < 1L) {
            return "< 1s";
        }

        long[] units = {TimeUnit.MILLISECONDS.toDays(time) / 30L, TimeUnit.MILLISECONDS.toDays(time) % 30L, TimeUnit.MILLISECONDS.toHours(time) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(time)), TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)), TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))};
        StringBuilder stringBuilder = new StringBuilder();

        if (units[0] > 0L) {
            stringBuilder.append(units[0]).append("mo")
                .append(" ");
        }

        if (units[1] > 0L) {
            stringBuilder.append(units[1]).append("d")
                .append(" ");
        }

        if (units[2] > 0L) {
            stringBuilder.append(units[2]).append("h")
                .append(" ");
        }

        if (units[3] > 0L) {
            stringBuilder.append(units[3]).append("m")
                .append(" ");
        }

        if (units[4] > 0L) {
            stringBuilder.append(units[4]).append("s");
        }

        return stringBuilder.length() > 0 ? stringBuilder.toString().trim() : time + "ms";
    }

    public static long fromString(String value) {
        StringBuilder stringBuilder = new StringBuilder();

        long time = 0L;
        for (char character : value.toCharArray()) {
            if (Character.isDigit(character)) {
                stringBuilder.append(character);
                continue;
            }

            int amount = Integer.parseInt(stringBuilder.toString());
            switch (character) {
                case 'd': {
                    time += TimeUnit.DAYS.toMillis(amount);
                    break;
                }

                case 'h': {
                    time += TimeUnit.HOURS.toMillis(amount);
                    break;
                }

                case 'm': {
                    time += TimeUnit.MINUTES.toMillis(amount);
                    break;
                }

                case 's': {
                    time += TimeUnit.SECONDS.toMillis(amount);
                    break;
                }

                default:
                    break;
            }
            stringBuilder = new StringBuilder();
        }
        return time;
    }
}
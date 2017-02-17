package Objects;

import java.time.DayOfWeek;


/**
 * Created by Vaerys on 14/01/2017.
 */
public class DailyObject {

    DayOfWeek dayOfWeek;
    String fileName;

    public DailyObject(DayOfWeek dayOfWeek, String fileName) {
        this.dayOfWeek = dayOfWeek;
        this.fileName = fileName.replace("#day#", dayOfWeek.toString());
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public String getFileName() {
        return fileName;
    }

    public void updateFilePath(String newFileName) {
        this.fileName = newFileName.replace("#day#", dayOfWeek.toString());
    }
}

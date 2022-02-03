import com.mksherbini.Scrapper;
import com.mksherbini.Timer;

import java.time.LocalDate;
import java.util.List;

public class Runner {
    public static void main(String[] args) {
        var timer = new Timer("main");
        timer.mark();

        final List<String> ids = List.of(
                "08fce54a-8776-4891-8391-f75c7e0a98bb",
                "WEB-0000000001035662"
        );
        final Scrapper scrapper = new Scrapper();

        ids.forEach(id ->
                scrapper.findLogs(
                        LocalDate.of(2022, 2, 2),
                        id)
        );

        scrapper.cleanUp();
        timer.log();
    }
}

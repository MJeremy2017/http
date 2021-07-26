import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Application {
    private static final List<String> ENDPOINTS = Arrays.asList(
            "http://localhost:8081/task",
            "http://localhost:8082/task");

    private static final List<String> TASKS = Arrays.asList(
            "12,12314,23",
            "53,28,98000000000,245354");

    public static void main(String[] args) {
        Aggregator agg = new Aggregator();
        List<String> results = agg.sendMultipleTasks(ENDPOINTS, TASKS);

        for (String result : results) {
            System.out.println(result);
        }
    }
    
}

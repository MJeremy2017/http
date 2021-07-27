import networking.WebClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Aggregator {
    private WebClient webClient;

    public Aggregator() {
        webClient = new WebClient();
    }

    public List<String> sendMultipleTasks(List<String> urls, List<String> payloads) {
        CompletableFuture<String>[] futures = new CompletableFuture[urls.size()];

        for (int i=0; i<urls.size(); ++i) {
            String url = urls.get(i);
            byte[] payload = payloads.get(i).getBytes();

            futures[i] = webClient.sendTask(url, payload);
        }

        return Stream.of(futures).map(CompletableFuture::join).collect(Collectors.toList());

    }

}

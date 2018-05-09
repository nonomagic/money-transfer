package org.dk.app.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.System.err;
import static java.lang.System.out;
import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.dk.app.utils.RestUtils.readJson;
import static org.eclipse.jetty.http.HttpMethod.PUT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static spark.utils.IOUtils.copy;

public class ConcurrentTransfersTest {

    private final HttpClient client = new HttpClient();
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private Process serverProcess;

    @Test
    public void balances_should_remain_correct__when_multiple_concurrent_transfers_are_requested_for_the_same_account() throws Exception {
        // given
        String bob = createAccount("Bob", "2000");
        String alice = createAccount("Alice", "2000");

        // when
        range(0, 2000)
            .mapToObj(i -> Stream.of(transferRequest(bob, alice, "1"), transferRequest(alice, bob, "1")))
            .flatMap(x -> x)
            .collect(toList())
            .forEach(this::send);

        executor.shutdown();
        executor.awaitTermination(30, SECONDS);

        // then
        assertThat("Bob's balance", balanceOf(bob), is(2000));
        assertThat("Alice's balance", balanceOf(alice), is(2000));
    }

    @Before
    public void start() throws Exception {
        out.print("Starting server... ");

        serverProcess = new ProcessBuilder()
            .command("/bin/bash", "-c", "java -cp " + System.getProperty("user.dir") + "/target/money-transfer-1.0-SNAPSHOT-jar-with-dependencies.jar org.dk.app.Application")
            .redirectErrorStream(true)
            .start();

        sleep(2_000);
        copy(serverProcess.getErrorStream(), err);

        out.println("started");

        out.print("Starting client... ");

        client.start();

        out.println("started");
    }

    @After
    public void stop() throws Exception {
        client.stop();
        serverProcess.destroy();
    }

    private double balanceOf(String bob) throws InterruptedException, ExecutionException, TimeoutException {
        return readJson(client.GET(format("http://localhost:8080/accounts/%s", bob)).getContentAsString())
            .get("balance").asDouble();
    }

    private Future<?> send(Request request) {
        return executor.submit(() -> {
            try {
                ContentResponse response = request.send();

                if (response.getStatus() >= 400)
                    err.println(request.getMethod() + " " + request.getPath() + " => " + response.getStatus());
                // else
                //     out.println(request.getMethod() + " " + request.getPath() + " => " + response.getStatus());

                return readJson(response.getContentAsString());
            } catch (InterruptedException | TimeoutException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Request transferRequest(String fromAccount, String toAccount, String amount) {
        return client.newRequest(format("http://localhost:8080/accounts/%s/transfer/%s", fromAccount, toAccount))
            .method(PUT)
            .content(new StringContentProvider(format("{\"amount\":%s}", amount)));
    }

    private String createAccount(String name, String balance) throws InterruptedException, TimeoutException, ExecutionException {
        String ownerId = POST("http://localhost:8080/owners", format("{\"name\":\"%s\",\"email\":\"%s@megabank.com\"}", name, name))
            .get("id").asText();

        String accountId = POST("http://localhost:8080/accounts", format("{\"ownerId\":\"%s\",\"balance\":%s}", ownerId, balance))
            .get("id").asText();

        return accountId;
    }

    private JsonNode POST(String url, String content) throws InterruptedException, TimeoutException, ExecutionException {
        out.println("------");
        out.println("POST http://localhost:8080/owners");
        out.println("<< " + content);

        String response = client.POST(url)
            .content(new StringContentProvider(content))
            .send()
            .getContentAsString();

        out.println(">> " + response);

        return readJson(response);
    }
}

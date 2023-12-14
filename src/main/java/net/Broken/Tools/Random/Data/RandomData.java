package net.Broken.Tools.Random.Data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RandomData(String jsonrpc, String method, int id, Params params, Error error, Result result) {
    public record Params(String apiKey, int n, int min, int max, boolean replacement) {
    }

    public record Error(long code, String message, Object data) {
    };

    public record Result(Random random, String bitsUsed, String bitsLeft, String requestsLeft, String advisoryDelay) {
        public record Random(List<Integer> data, String completionTime) {
        };
    }
}

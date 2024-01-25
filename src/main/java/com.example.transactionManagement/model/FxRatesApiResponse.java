package com.example.transactionManagement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class FxRatesApiResponse {

    @JsonProperty("rates")
    private Map<String, Double> rates;

    public Map<String, Double> getRates() {
        return rates;
    }

    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }
}

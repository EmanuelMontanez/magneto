package com.emanuelmontanez.magneto.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Stats {

    @JsonProperty
    private long totalMutants;
    @JsonProperty
    private long totalHumans;
    @JsonProperty
    private float ratio;
}

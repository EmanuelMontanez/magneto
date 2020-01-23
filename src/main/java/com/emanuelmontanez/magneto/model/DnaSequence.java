package com.emanuelmontanez.magneto.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Accessors(chain = true)
@Document(value = "dnasequence")
public class DnaSequence {

    @Id
    private String id;

    private String[] dna;

    private boolean mutant;
}

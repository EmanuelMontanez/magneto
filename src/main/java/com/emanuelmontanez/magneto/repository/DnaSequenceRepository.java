package com.emanuelmontanez.magneto.repository;

import com.emanuelmontanez.magneto.model.DnaSequence;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DnaSequenceRepository extends MongoRepository<DnaSequence, String> {

    DnaSequence findByDna(String[] dna);
}

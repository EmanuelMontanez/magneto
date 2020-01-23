package com.emanuelmontanez.magneto.service;

import com.emanuelmontanez.magneto.controller.response.Stats;
import com.emanuelmontanez.magneto.model.DnaSequence;
import com.emanuelmontanez.magneto.repository.DnaSequenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
public class DnaService {

    private static final String VALID_DNA_PATTERN = "^[A|T|C|G]+$";

    @Autowired
    private DnaSequenceRepository dnaSequenceRepository;

    /**
     *
     * @param dna
     */
    public void checkMutant(String[] dna) {
        validateDna(dna);
        DnaSequence dnaSequence = dnaSequenceRepository.findByDna(dna);
        if (Objects.isNull(dnaSequence)) {
            dnaSequence = new DnaSequence().setDna(dna).setMutant(isMutant(dna));
            dnaSequenceRepository.save(dnaSequence);
        }
        if (!dnaSequence.isMutant()) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }
    }

    private void validateDna(String[] dna) {
        int size = dna.length;
        if (size == 0) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
        for (String dnaRow : dna) {
            if (dnaRow.length() != size || !Pattern.matches(VALID_DNA_PATTERN, dnaRow)) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
            }
        }
    }

    private boolean isMutant(String[] dna) {
        int size = dna.length;
        for (int i = 0; i < size; i++) {
            String row = dna[i];
            for (int j = 0; j < size; j++) {
                char target = row.charAt(j);

                // check row
                if (j <= size - 4 && target == row.charAt(j+1) && target == row.charAt(j+2)
                        && target == row.charAt(j+3)) {
                    return true;
                }

                // check file
                if (i <= size - 4 && target == dna[i+1].charAt(j) && target == dna[i+2].charAt(j)
                        && target == dna[i+3].charAt(j)) {
                    return true;
                }

                // check right diagonal
                if (j <= size - 4 && i <= size - 4 && target == dna[i+1].charAt(j+1) && target == dna[i+2].charAt(j+2)
                        && target == dna[i+3].charAt(j+3)) {
                    return true;
                }

                // check left diagonal
                if (j >= 3 && i <= size - 4 && target == dna[i+1].charAt(j-1) && target == dna[i+2].charAt(j-2)
                        && target == dna[i+3].charAt(j-3)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    public Stats getStats() {
        List<DnaSequence> dnaSequenceList = dnaSequenceRepository.findAll();
        long totalHumans = dnaSequenceList.stream().filter(dnaSequence -> !dnaSequence.isMutant()).count();
        long totalMutants = dnaSequenceList.stream().filter(dnaSequence -> dnaSequence.isMutant()).count();
        float ratio = totalHumans == 0 ? (float)totalMutants : (float)totalMutants / (float)totalHumans;
        return new Stats().setTotalHumans(totalHumans).setTotalMutants(totalMutants).setRatio(ratio);
    }
}

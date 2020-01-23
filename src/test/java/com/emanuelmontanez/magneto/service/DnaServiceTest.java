package com.emanuelmontanez.magneto.service;

import com.emanuelmontanez.magneto.controller.response.Stats;
import com.emanuelmontanez.magneto.model.DnaSequence;
import com.emanuelmontanez.magneto.repository.DnaSequenceRepository;

import org.apache.commons.collections.ListUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DnaServiceTest {

    @InjectMocks
    DnaService dnaService;

    @Mock
    DnaSequenceRepository dnaSequenceRepository;

    @Test(expected = HttpClientErrorException.class)
    public void testEmptyRequest() {
        dnaService.checkMutant(new String[]{});
    }

    @Test(expected = HttpClientErrorException.class)
    public void testNonSquareMatrix1() {
        dnaService.checkMutant(new String[]{"ACTG", "TTGA", "CGAT", "ACAC", "TGGC"});
    }

    @Test(expected = HttpClientErrorException.class)
    public void testNonSquareMatrix2() {
        dnaService.checkMutant(new String[]{"ACTGC", "TTGAC", "CGATA", "ACACT"});
    }

    @Test(expected = HttpClientErrorException.class)
    public void testIllegalChar() {
        dnaService.checkMutant(new String[]{"ACTGC", "TTGAC", "CGATA", "ACACT", "ACTZG"});
    }

    @Test(expected = HttpClientErrorException.class)
    public void testNonMutant() {
        when(dnaSequenceRepository.findByDna(any())).thenReturn(null);
        dnaService.checkMutant(new String[]{"ATGCGA", "CAGTGC", "TTATTT", "AGACGG", "GCGTCA", "TCACTG"});
        verify(dnaSequenceRepository, times(1)).save(any(DnaSequence.class));
    }

    @Test(expected = HttpClientErrorException.class)
    public void testNonMutantExistent() {
        String[] dna = new String[]{"ATGCGA", "CAGTGC", "TTATTT", "AGACGG", "GCGTCA", "TCACTG"};
        DnaSequence dnaSequence = new DnaSequence().setDna(dna).setMutant(false);
        when(dnaSequenceRepository.findByDna(dna)).thenReturn(dnaSequence);
        dnaService.checkMutant(dna);
        verify(dnaSequenceRepository, times(0)).save(any(DnaSequence.class));
    }

    @Test
    public void testMutantRow() {
        when(dnaSequenceRepository.findByDna(any())).thenReturn(null);
        dnaService.checkMutant(new String[]{"ATGCGA", "CAGTGC", "TTATGT", "AGAGAG", "CCCCTA", "TCACTG"});
        dnaService.checkMutant(new String[]{"ATGGGG", "CAGTGC", "TTATGT", "AGAGAG", "CCCCTA", "TCACTG"});
        dnaService.checkMutant(new String[]{"ATGCGA", "CAGTGC", "TTATGT", "AGAGAG", "CTCCTA", "TCAAAA"});
        verify(dnaSequenceRepository, times(3)).save(any(DnaSequence.class));
    }

    @Test
    public void testMutantFile() {
        when(dnaSequenceRepository.findByDna(any())).thenReturn(null);
        dnaService.checkMutant(new String[]{"ATGCGA", "CAGTGC", "TTATGT", "AGAGGG", "CTCCTA", "TCACTG"});
        dnaService.checkMutant(new String[]{"ATGCGA", "CAGTGC", "TTATGG", "AGAGGG", "CTCCTG", "TCACTG"});
        dnaService.checkMutant(new String[]{"ATGCGA", "AAGTGC", "ATATGT", "AGAGGG", "CTCCTA", "TCACTG"});
        verify(dnaSequenceRepository, times(3)).save(any(DnaSequence.class));
    }

    @Test
    public void testMutantDiagonals() {
        when(dnaSequenceRepository.findByDna(any())).thenReturn(null);
        dnaService.checkMutant(new String[]{"ATGCGA", "CAGTGC", "TTATGT", "AGAAAG", "CTCCTA", "TCACTG"});
        dnaService.checkMutant(new String[]{"ATGCGA", "CAGTGC", "TTATGT", "AGAAAG", "CTCCAA", "TCACTA"});
        dnaService.checkMutant(new String[]{"ATGCGA", "CACTGC", "TCCTGT", "CGAAAG", "CTCCTA", "TCACTG"});
        verify(dnaSequenceRepository, times(3)).save(any(DnaSequence.class));
    }

    @Test
    public void testMutantExistent() {
        String[] dna = new String[]{"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"};
        DnaSequence dnaSequence = new DnaSequence().setDna(dna).setMutant(true);
        when(dnaSequenceRepository.findByDna(dna)).thenReturn(dnaSequence);
        dnaService.checkMutant(dna);
        verify(dnaSequenceRepository, times(0)).save(any(DnaSequence.class));
    }

    @Test
    public void testStatsNoResults() {
        when(dnaSequenceRepository.findAll()).thenReturn(ListUtils.EMPTY_LIST);
        Stats stats = dnaService.getStats();
        assert stats.getTotalHumans() == 0;
        assert stats.getTotalMutants() == 0;
        assert stats.getRatio() == 0L;
    }

    @Test
    public void testStats() {
        List<DnaSequence> dnaSequenceList = new ArrayList<>();
        dnaSequenceList.add(new DnaSequence()
                .setDna(new String[]{"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"})
                .setMutant(true));
        dnaSequenceList.add(new DnaSequence()
                .setDna(new String[]{"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"})
                .setMutant(false));
        dnaSequenceList.add(new DnaSequence()
                .setDna(new String[]{"GTGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"})
                .setMutant(false));
        when(dnaSequenceRepository.findAll()).thenReturn(dnaSequenceList);
        Stats stats = dnaService.getStats();
        assert stats.getTotalHumans() == 2;
        assert stats.getTotalMutants() == 1;
        assert stats.getRatio() == 0.5;
    }

    @Test
    public void testStatsNoHumans() {
        List<DnaSequence> dnaSequenceList = new ArrayList<>();
        dnaSequenceList.add(new DnaSequence()
                .setDna(new String[]{"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"})
                .setMutant(true));
        when(dnaSequenceRepository.findAll()).thenReturn(dnaSequenceList);
        Stats stats = dnaService.getStats();
        assert stats.getTotalHumans() == 0;
        assert stats.getTotalMutants() == 1;
        assert stats.getRatio() == 1L;
    }

    @Test
    public void testStatsNoMutants() {
        List<DnaSequence> dnaSequenceList = new ArrayList<>();
        dnaSequenceList.add(new DnaSequence()
                .setDna(new String[]{"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"})
                .setMutant(false));
        when(dnaSequenceRepository.findAll()).thenReturn(dnaSequenceList);
        Stats stats = dnaService.getStats();
        assert stats.getTotalHumans() == 1;
        assert stats.getTotalMutants() == 0;
        assert stats.getRatio() == 0L;
    }
}

package com.emanuelmontanez.magneto.controller;

import com.emanuelmontanez.magneto.controller.request.DnaReq;
import com.emanuelmontanez.magneto.controller.response.Stats;
import com.emanuelmontanez.magneto.service.DnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@RestController
public class DnaController {

    @Autowired
    private DnaService dnaService;

    /**
     * Validate if input dna belongs to a mutant.
     *
     */
    @PostMapping("/mutant")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> mutant(@RequestBody DnaReq dna) {
        dnaService.checkMutant(dna.getDna());
        return ResponseEntity.ok().build();
    }

    /**
     * Get current stats including amount of mutants, amount of humans, and ratio.
     *
     */
    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Stats> stats() {
        return ResponseEntity.ok(dnaService.getStats());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    private ResponseEntity handleClientError(HttpClientErrorException ex) {
        return new ResponseEntity(ex.getStatusCode());
    }
}

package com.hoyozero.deploy.service;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RunnerServiceTest {

    private final RunnerService runnerService = new RunnerService();

    @Test
    void shouldMatchStandardBranchRefs() {
        assertTrue(runnerService.matchesBranch("main", "refs/heads/main"));
        assertTrue(runnerService.matchesBranch("main", "main"));
        assertFalse(runnerService.matchesBranch("main", "refs/heads/dev"));
    }

    @Test
    void shouldExtractBranchFromCommonWebhookPayloads() {
        Map<String, Object> payload = Map.of("ref", "refs/heads/main");
        assertEquals("main", runnerService.resolveBranch(payload));
    }
}

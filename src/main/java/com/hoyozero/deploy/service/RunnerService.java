package com.hoyozero.deploy.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RunnerService {

    public boolean matchesBranch(String configuredBranch, String incomingRef) {
        if (configuredBranch == null || configuredBranch.trim().isEmpty()) {
            return true;
        }
        String target = configuredBranch.trim();
        String ref = incomingRef == null ? "" : incomingRef.trim();
        if (ref.startsWith("refs/heads/")) {
            return target.equals(ref.substring("refs/heads/".length()));
        }
        return target.equals(ref);
    }

    public String resolveBranch(Map<String, Object> payload) {
        if (payload == null || payload.isEmpty()) {
            return null;
        }
        Object ref = payload.get("ref");
        if (ref == null) {
            return null;
        }
        String value = ref.toString();
        if (value.startsWith("refs/heads/")) {
            return value.substring("refs/heads/".length());
        }
        return value;
    }
}

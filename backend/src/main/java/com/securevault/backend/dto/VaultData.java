package com.securevault.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class VaultData {

    private List<VaultEntry> entries = new ArrayList<>();

    public VaultData() {}

    public List<VaultEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<VaultEntry> entries) {
        this.entries = entries;
    }
}
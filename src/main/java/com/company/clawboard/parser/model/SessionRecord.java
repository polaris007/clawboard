package com.company.clawboard.parser.model;

public record SessionRecord(String id, int version, String timestamp, String cwd)
    implements JsonlRecord {}

package com.company.clawboard.parser.model;

public record MetadataRecord(String type, String id, String provider, String modelId)
    implements JsonlRecord {}

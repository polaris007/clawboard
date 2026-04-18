package com.company.clawboard.parser.model;

import com.fasterxml.jackson.databind.JsonNode;

public record CustomRecord(String customType, String id, String timestamp, JsonNode data)
    implements JsonlRecord {}

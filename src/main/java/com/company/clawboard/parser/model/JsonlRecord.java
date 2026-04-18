package com.company.clawboard.parser.model;

/**
 * Represents a single parsed line from a JSONL transcript file.
 */
public sealed interface JsonlRecord
    permits SessionRecord, MessageRecord, CustomRecord, MetadataRecord {
}

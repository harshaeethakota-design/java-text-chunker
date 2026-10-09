package io.github.harshaeethakota.textchunker;

import java.util.List;
import java.util.Objects;

/**
 * Splits a document into {@link Chunk}s.
 */
public interface TextSplitter {
    /**
     * @param text the document to split; must not be null
     * @return chunks in document order; empty list for an empty document
     */
    List<Chunk> split(String text);
}

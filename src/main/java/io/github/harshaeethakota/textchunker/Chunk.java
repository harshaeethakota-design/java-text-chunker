package io.github.harshaeethakota.textchunker;

import java.util.Objects;

/**
 * One chunk of a split document.
 *
 * <p>A chunk is always an exact substring of the source document:
 * {@code document.substring(startOffset, endOffset).equals(text)}.
 *
 * @param text       the chunk text, an exact substring of the source document
 * @param index      zero-based position of this chunk in the split result
 * @param startOffset char offset where this chunk starts in the source document
 * @param endOffset   char offset where this chunk ends (exclusive)
 * @param tokenCount  token count of the chunk per the splitter's tokenizer
 */
public record Chunk(String text, int index, int startOffset, int endOffset, int tokenCount) {
    public Chunk {
        Objects.requireNonNull(text, "text");
        if (index < 0) {
            throw new IllegalArgumentException("index must be >= 0");
        }
        if (startOffset < 0 || endOffset < startOffset) {
            throw new IllegalArgumentException(
                    "offsets must satisfy 0 <= startOffset <= endOffset");
        }
        if (tokenCount < 0) {
            throw new IllegalArgumentException("tokenCount must be >= 0");
        }
    }
}

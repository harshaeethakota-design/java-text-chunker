package io.github.harshaeethakota.textchunker;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class FixedSizeSplitterTest {

    private static String words(int n) {
        return IntStream.range(0, n).mapToObj(i -> "w" + i).collect(Collectors.joining(" "));
    }

    @Test
    void chunksRespectTokenBudget() {
        String doc = words(25);
        List<Chunk> chunks = new FixedSizeSplitter(new WordTokenizer(), 10, 2).split(doc);
        assertEquals(3, chunks.size());
        for (Chunk c : chunks) {
            assertTrue(c.tokenCount() <= 10, "chunk too big: " + c);
        }
    }

    @Test
    void overlapCarriesTrailingWordsIntoNextChunk() {
        String doc = words(25);
        List<Chunk> chunks = new FixedSizeSplitter(new WordTokenizer(), 10, 2).split(doc);
        assertTrue(chunks.get(1).text().startsWith("w8 w9"),
                "expected overlap, got: " + chunks.get(1).text());
        assertTrue(chunks.get(1).startOffset() < chunks.get(0).endOffset());
    }

    @Test
    void chunksAreExactSubstringsWithSequentialIndices() {
        String doc = words(25);
        List<Chunk> chunks = new FixedSizeSplitter(new WordTokenizer(), 10, 2).split(doc);
        for (int i = 0; i < chunks.size(); i++) {
            Chunk c = chunks.get(i);
            assertEquals(i, c.index());
            assertEquals(doc.substring(c.startOffset(), c.endOffset()), c.text());
        }
    }

    @Test
    void emptyAndNullInput() {
        TextSplitter splitter = new FixedSizeSplitter(new WordTokenizer(), 10, 0);
        assertTrue(splitter.split("").isEmpty());
        assertThrows(NullPointerException.class, () -> splitter.split(null));
    }

    @Test
    void constructorValidation() {
        Tokenizer t = new WordTokenizer();
        assertThrows(IllegalArgumentException.class, () -> new FixedSizeSplitter(t, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new FixedSizeSplitter(t, 10, 10));
        assertThrows(IllegalArgumentException.class, () -> new FixedSizeSplitter(t, 10, -1));
        assertThrows(NullPointerException.class, () -> new FixedSizeSplitter(null, 10, 0));
    }
}

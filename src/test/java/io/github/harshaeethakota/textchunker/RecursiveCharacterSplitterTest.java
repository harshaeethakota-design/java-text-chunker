package io.github.harshaeethakota.textchunker;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class RecursiveCharacterSplitterTest {

    @Test
    void prefersParagraphBoundaries() {
        String doc = "a".repeat(20) + "\n\n" + "b".repeat(20);
        List<Chunk> chunks =
                new RecursiveCharacterSplitter(new CharacterTokenizer(), 20, 0).split(doc);
        assertEquals(2, chunks.size());
        assertEquals("a".repeat(20), chunks.get(0).text());
        assertEquals("b".repeat(20), chunks.get(1).text());
    }

    @Test
    void fallsBackThroughSeparators() {
        // No "\n\n": splits on "\n", then merges small pieces up to the budget.
        List<Chunk> chunks =
                new RecursiveCharacterSplitter(new CharacterTokenizer(), 4, 0).split("aa\nbb\ncc");
        assertEquals(2, chunks.size());
        assertEquals("aa\nbb", chunks.get(0).text());
        assertEquals("cc", chunks.get(1).text());
    }

    @Test
    void splitsCharactersWhenNoSeparatorMatches() {
        List<Chunk> chunks =
                new RecursiveCharacterSplitter(new CharacterTokenizer(), 4, 0).split("abcdefghij");
        assertEquals(3, chunks.size());
        assertEquals("abcd", chunks.get(0).text());
        assertEquals("efgh", chunks.get(1).text());
        assertEquals("ij", chunks.get(2).text());
    }

    @Test
    void unsplittableOversizedPieceIsEmittedAsIs() {
        // With only " " as separator, a 30-char word cannot be split: honest overflow.
        TextSplitter splitter = new RecursiveCharacterSplitter(
                new CharacterTokenizer(), 10, 0, List.of(" "));
        List<Chunk> chunks = splitter.split("a".repeat(30));
        assertEquals(1, chunks.size());
        assertEquals(30, chunks.get(0).tokenCount());
    }

    @Test
    void customSeparatorsAreRespected() {
        TextSplitter splitter =
                new RecursiveCharacterSplitter(new CharacterTokenizer(), 10, 0, List.of("|"));
        List<Chunk> chunks = splitter.split("aaa|bbb|ccc");
        assertEquals(1, chunks.size());
        assertEquals("aaa|bbb|ccc", chunks.get(0).text());
    }

    @Test
    void overlapCarriesContextBetweenChunks() {
        List<Chunk> chunks =
                new RecursiveCharacterSplitter(new CharacterTokenizer(), 6, 2).split("aa bb cc dd");
        assertTrue(chunks.size() >= 2);
        assertTrue(chunks.get(1).startOffset() < chunks.get(0).endOffset());
        for (Chunk c : chunks) {
            assertTrue(c.tokenCount() <= 6);
        }
    }

    @Test
    void emptyAndNullInput() {
        TextSplitter splitter = new RecursiveCharacterSplitter(new CharacterTokenizer(), 10, 0);
        assertTrue(splitter.split("").isEmpty());
        assertThrows(NullPointerException.class, () -> splitter.split(null));
    }
}

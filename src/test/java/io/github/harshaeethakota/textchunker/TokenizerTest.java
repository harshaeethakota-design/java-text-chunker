package io.github.harshaeethakota.textchunker;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TokenizerTest {

    @Test
    void characterTokenizerCountsCharacters() {
        assertEquals(5, new CharacterTokenizer().countTokens("hello"));
        assertEquals(0, new CharacterTokenizer().countTokens(""));
    }

    @Test
    void wordTokenizerCountsWords() {
        assertEquals(2, new WordTokenizer().countTokens("hello world"));
        assertEquals(2, new WordTokenizer().countTokens("  spaced   out  "));
        assertEquals(0, new WordTokenizer().countTokens(""));
        assertEquals(0, new WordTokenizer().countTokens("   "));
    }

    @Test
    void tokenizersRejectNull() {
        assertThrows(NullPointerException.class, () -> new CharacterTokenizer().countTokens(null));
        assertThrows(NullPointerException.class, () -> new WordTokenizer().countTokens(null));
    }
}

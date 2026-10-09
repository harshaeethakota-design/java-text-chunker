package io.github.harshaeethakota.textchunker;

import java.util.Objects;

/**
 * Baseline tokenizer: one character counts as one token.
 *
 * <p>Deterministic and dependency-free. Useful for tests and as a floor that
 * real tokenizers should beat for accuracy, not as a production choice.
 */
public final class CharacterTokenizer implements Tokenizer {
    @Override
    public int countTokens(String text) {
        Objects.requireNonNull(text, "text");
        return text.length();
    }
}

package io.github.harshaeethakota.textchunker;

import java.util.Objects;

/**
 * Baseline tokenizer: one whitespace-separated word counts as one token.
 *
 * <p>Closer to real model tokenization than {@link CharacterTokenizer} for
 * English prose, still only an approximation. Plug a BPE tokenizer behind
 * {@link Tokenizer} when chunk budgets must match a specific model.
 */
public final class WordTokenizer implements Tokenizer {
    @Override
    public int countTokens(String text) {
        Objects.requireNonNull(text, "text");
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            return 0;
        }
        return trimmed.split("\\s+").length;
    }
}

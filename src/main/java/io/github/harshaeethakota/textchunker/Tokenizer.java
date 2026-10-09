package io.github.harshaeethakota.textchunker;

/**
 * Counts tokens in a piece of text.
 *
 * <p>A tokenizer is intentionally a single-method interface: bring your own
 * counting strategy. The library ships with {@link CharacterTokenizer} and
 * {@link WordTokenizer} as dependency-free baselines. For production RAG,
 * plug in a real subword tokenizer (for example a BPE implementation such as
 * jtiktoken) behind this interface so chunk budgets match the model's own
 * tokenization.
 */
public interface Tokenizer {
    /**
     * @param text text to measure; must not be null
     * @return number of tokens in {@code text}, always {@code >= 0}
     */
    int countTokens(String text);
}

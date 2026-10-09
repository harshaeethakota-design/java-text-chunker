package io.github.harshaeethakota.textchunker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Splits text into fixed-size chunks measured in tokens, with overlap.
 *
 * <p>The document is first cut into word atoms (maximal non-whitespace runs);
 * atoms are then greedily packed into chunks of at most {@code chunkSize}
 * tokens, each new chunk starting with roughly {@code chunkOverlap} tokens of
 * trailing context from the previous one.
 *
 * <p>Honest limits: a single word larger than {@code chunkSize} cannot be
 * split further and is emitted as an oversized chunk. Overlap is approximate
 * at word granularity.
 */
public final class FixedSizeSplitter implements TextSplitter {

    private static final Pattern WORD = Pattern.compile("\\S+");

    private final Tokenizer tokenizer;
    private final int chunkSize;
    private final int chunkOverlap;

    public FixedSizeSplitter(Tokenizer tokenizer, int chunkSize, int chunkOverlap) {
        this.tokenizer = Objects.requireNonNull(tokenizer, "tokenizer");
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize must be > 0");
        }
        if (chunkOverlap < 0 || chunkOverlap >= chunkSize) {
            throw new IllegalArgumentException("chunkOverlap must satisfy 0 <= chunkOverlap < chunkSize");
        }
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
    }

    @Override
    public List<Chunk> split(String text) {
        Objects.requireNonNull(text, "text");
        if (text.isEmpty()) {
            return List.of();
        }
        return ChunkMerger.merge(text, atoms(text), chunkSize, chunkOverlap);
    }

    private List<Piece> atoms(String text) {
        List<Piece> out = new ArrayList<>();
        Matcher m = WORD.matcher(text);
        while (m.find()) {
            out.add(new Piece(m.start(), m.end(), tokenizer.countTokens(m.group())));
        }
        return out;
    }
}

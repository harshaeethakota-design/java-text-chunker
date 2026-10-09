package io.github.harshaeethakota.textchunker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Recursively splits on a prioritized list of separators, then merges the
 * pieces into token-budgeted chunks with overlap.
 *
 * <p>Splitting tries each separator in order and uses the first one present in
 * the text (default: {@code "\n\n"}, {@code "\n"}, {@code " "}, {@code ""}),
 * so paragraphs are preferred over lines, lines over words, words over
 * characters. Pieces still larger than {@code chunkSize} are split again with
 * the remaining separators. The merged chunks behave exactly like
 * {@link FixedSizeSplitter}'s: at most {@code chunkSize} tokens each, roughly
 * {@code chunkOverlap} tokens of trailing context carried over.
 *
 * <p>Honest limit: a piece with no usable separator left (or a single
 * separator-free run) that exceeds {@code chunkSize} is emitted oversized.
 */
public final class RecursiveCharacterSplitter implements TextSplitter {

    private static final List<String> DEFAULT_SEPARATORS = List.of("\n\n", "\n", " ", "");

    private final Tokenizer tokenizer;
    private final int chunkSize;
    private final int chunkOverlap;
    private final List<String> separators;

    public RecursiveCharacterSplitter(Tokenizer tokenizer, int chunkSize, int chunkOverlap) {
        this(tokenizer, chunkSize, chunkOverlap, DEFAULT_SEPARATORS);
    }

    public RecursiveCharacterSplitter(
            Tokenizer tokenizer, int chunkSize, int chunkOverlap, List<String> separators) {
        this.tokenizer = Objects.requireNonNull(tokenizer, "tokenizer");
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize must be > 0");
        }
        if (chunkOverlap < 0 || chunkOverlap >= chunkSize) {
            throw new IllegalArgumentException("chunkOverlap must satisfy 0 <= chunkOverlap < chunkSize");
        }
        Objects.requireNonNull(separators, "separators");
        if (separators.isEmpty()) {
            throw new IllegalArgumentException("separators must not be empty");
        }
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
        this.separators = List.copyOf(separators);
    }

    @Override
    public List<Chunk> split(String text) {
        Objects.requireNonNull(text, "text");
        if (text.isEmpty()) {
            return List.of();
        }
        List<Piece> pieces = splitRecursively(text, 0, separators);
        return ChunkMerger.merge(text, pieces, chunkSize, chunkOverlap);
    }

    private List<Piece> splitRecursively(String text, int baseOffset, List<String> seps) {
        String chosen = null;
        List<String> rest = null;
        for (int i = 0; i < seps.size(); i++) {
            if (!seps.get(i).isEmpty() && text.contains(seps.get(i))) {
                chosen = seps.get(i);
                rest = seps.subList(i + 1, seps.size());
                break;
            }
        }
        if (chosen == null) {
            // No separator present (or only "" remains): split into characters,
            // or keep whole if it already fits.
            if (seps.contains("")) {
                return splitIntoChars(text, baseOffset);
            }
            return List.of(piece(text, baseOffset));
        }
        List<Piece> out = new ArrayList<>();
        int start = 0;
        int idx;
        while ((idx = text.indexOf(chosen, start)) >= 0) {
            if (idx > start) {
                out.addAll(splitPiece(text.substring(start, idx), baseOffset + start, rest));
            }
            start = idx + chosen.length();
        }
        if (start < text.length()) {
            out.addAll(splitPiece(text.substring(start), baseOffset + start, rest));
        }
        return out;
    }

    private List<Piece> splitPiece(String part, int offset, List<String> rest) {
        if (rest.isEmpty() || tokenizer.countTokens(part) <= chunkSize) {
            return List.of(piece(part, offset));
        }
        return splitRecursively(part, offset, rest);
    }

    private List<Piece> splitIntoChars(String text, int baseOffset) {
        List<Piece> out = new ArrayList<>(text.length());
        for (int i = 0; i < text.length(); i++) {
            out.add(new Piece(baseOffset + i, baseOffset + i + 1,
                    tokenizer.countTokens(text.substring(i, i + 1))));
        }
        return out;
    }

    private Piece piece(String text, int offset) {
        return new Piece(offset, offset + text.length(), tokenizer.countTokens(text));
    }
}

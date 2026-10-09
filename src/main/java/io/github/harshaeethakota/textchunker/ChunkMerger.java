package io.github.harshaeethakota.textchunker;

import java.util.ArrayList;
import java.util.List;

/**
 * Greedily merges small pieces into chunks of at most {@code chunkSize}
 * tokens, carrying {@code chunkOverlap} tokens of trailing context into each
 * new chunk.
 *
 * <p>Chunk text is always {@code document.substring(firstPiece.start,
 * lastPiece.end)}, so chunks are exact substrings of the source. Overlap is
 * approximate at piece granularity: the merger carries whole trailing pieces
 * until roughly {@code chunkOverlap} tokens are covered.
 */
final class ChunkMerger {

    private ChunkMerger() {}

    static List<Chunk> merge(
            String document, List<Piece> pieces, int chunkSize, int chunkOverlap) {
        List<Chunk> chunks = new ArrayList<>();
        List<Piece> current = new ArrayList<>();
        int currentTokens = 0;
        int index = 0;

        for (Piece piece : pieces) {
            if (!current.isEmpty() && currentTokens + piece.tokenCount() > chunkSize) {
                chunks.add(emit(document, current, index++));
                current = overlapTail(current, chunkOverlap);
                currentTokens = current.stream().mapToInt(Piece::tokenCount).sum();
            }
            current.add(piece);
            currentTokens += piece.tokenCount();
        }
        if (!current.isEmpty()) {
            chunks.add(emit(document, current, index));
        }
        return chunks;
    }

    private static List<Piece> overlapTail(List<Piece> pieces, int overlapTokens) {
        List<Piece> tail = new ArrayList<>();
        if (overlapTokens <= 0) {
            return tail;
        }
        int tokens = 0;
        for (int i = pieces.size() - 1; i >= 0 && tokens < overlapTokens; i--) {
            tail.add(0, pieces.get(i));
            tokens += pieces.get(i).tokenCount();
        }
        return tail;
    }

    private static Chunk emit(String document, List<Piece> pieces, int index) {
        int start = pieces.get(0).startOffset();
        int end = pieces.get(pieces.size() - 1).endOffset();
        String text = document.substring(start, end);
        int tokens = pieces.stream().mapToInt(Piece::tokenCount).sum();
        return new Chunk(text, index, start, end, tokens);
    }
}

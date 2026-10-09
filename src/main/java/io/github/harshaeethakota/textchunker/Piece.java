package io.github.harshaeethakota.textchunker;

/**
 * An indivisible slice of the source document: char offsets plus its token
 * count. Package-private; chunks are what the public API returns.
 */
record Piece(int startOffset, int endOffset, int tokenCount) {}

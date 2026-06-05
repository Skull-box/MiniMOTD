/*
 * This file is part of MiniMOTD, licensed under the MIT License.
 *
 * Copyright (c) 2020-2025 Jason Penilla
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package xyz.jpenilla.minimotd.common;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Allows external plugins to override the MOTD that MiniMOTD would otherwise build from its
 * configuration. Register an implementation via
 * {@link MiniMOTD#registerMOTDProvider(MOTDProvider, int)}.
 *
 * <p>Providers are consulted on every ping, in descending priority order. For each of the two
 * MOTD lines, the first provider that supplies a non-{@code null} value wins; any line left
 * {@code null} (or a provider that returns {@code null} entirely) falls back to MiniMOTD's
 * configured line. This gives line-level granularity: a provider may override only line 1, only
 * line 2, both, or abstain completely.</p>
 */
@NullMarked
@FunctionalInterface
public interface MOTDProvider {

  /**
   * Called each time a MOTD is built.
   *
   * @param context information about the ping currently being handled
   * @return a {@link Result} overriding one or both lines, or {@code null} to abstain entirely
   *     (MiniMOTD's configured MOTD is used)
   */
  @Nullable Result provide(Context context);

  /**
   * Context passed to a provider for a single ping.
   *
   * @param playerCount the (already modified) player count for this ping
   */
  record Context(PingResponse.PlayerCount playerCount) {
  }

  /**
   * The lines a provider wishes to override. Each line is a MiniMessage string and supports the
   * same placeholders as the config (e.g. {@code <online_players>}, {@code <max_players>}). A
   * {@code null} line means "keep MiniMOTD's configured line for this slot".
   *
   * @param line1 the first MOTD line, or {@code null} to keep MiniMOTD's line
   * @param line2 the second MOTD line, or {@code null} to keep MiniMOTD's line
   */
  record Result(@Nullable String line1, @Nullable String line2) {

    private static final Result EMPTY = new Result(null, null);

    /**
     * Creates a result that overrides neither line.
     *
     * @return a result that overrides neither line
     */
    public static Result empty() {
      return EMPTY;
    }

    /**
     * Creates a result overriding the given lines.
     *
     * @param line1 the first line, or {@code null} to keep MiniMOTD's line
     * @param line2 the second line, or {@code null} to keep MiniMOTD's line
     * @return a result overriding the given lines
     */
    public static Result of(final @Nullable String line1, final @Nullable String line2) {
      return new Result(line1, line2);
    }

    /**
     * Creates a result overriding only the first line.
     *
     * @param line1 the first line
     * @return a result overriding only the first line
     */
    public static Result line1(final @Nullable String line1) {
      return new Result(line1, null);
    }

    /**
     * Creates a result overriding only the second line.
     *
     * @param line2 the second line
     * @return a result overriding only the second line
     */
    public static Result line2(final @Nullable String line2) {
      return new Result(null, line2);
    }
  }
}

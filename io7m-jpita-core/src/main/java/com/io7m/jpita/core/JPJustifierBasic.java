/*
 * Copyright © 2016 <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jpita.core;

import com.io7m.jnull.NullCheck;
import com.io7m.jranges.RangeCheck;
import org.valid4j.Assertive;

import java.util.ArrayList;
import java.util.List;

/**
 * An basic aligner that fully justifies text. The algorithm used is a simple
 * greedy formatter that avoids the {@code O(n²)} performance of a full dynamic
 * programming approach.
 */

public final class JPJustifierBasic implements JPAlignerType
{
  /**
   * An instance of the {@link SpaceTextDecisionType} that avoids justifying
   * lines if more than half of the line would be space.
   */

  public static final SpaceTextDecisionType JUSTIFY_UNDER_HALF =
    (space, text) -> space < text / 2;

  /**
   * An instance of the {@link SpaceTextDecisionType} that always justifies
   * lines.
   */

  public static final SpaceTextDecisionType JUSTIFY_ALWAYS =
    (space, text) -> true;

  private final SpaceTextDecisionType decider;
  private final int width;
  private final List<String> line_words;
  private final List<String> lines_formatted;
  private final StringBuilder line_buffer;
  private final JPOverflowBehaviour overflow;
  private int line_words_sum;

  private JPJustifierBasic(
    final JPOverflowBehaviour in_overflow,
    final SpaceTextDecisionType in_decider,
    final int in_width)
  {
    this.overflow = NullCheck.notNull(in_overflow);
    this.decider = NullCheck.notNull(in_decider);
    this.width = RangeCheck.checkGreaterInteger(
      in_width, "Width", 0, "Minimum width");
    this.line_words = new ArrayList<>(16);
    this.line_words_sum = 0;
    this.lines_formatted = new ArrayList<>(16);
    this.line_buffer = new StringBuilder(this.width);
  }

  /**
   * Construct a new justifier.
   *
   * @param in_overflow The behaviour for words that are of a greater length
   *                    than the maximum width and therefore cannot fit even
   *                    when placed on an otherwise blank line
   * @param in_decider  A function that decides whether or not lines should be
   *                    justified
   * @param in_width    The maximum width in characters
   *
   * @return A new justifier
   */

  public static JPAlignerType create(
    final JPOverflowBehaviour in_overflow,
    final SpaceTextDecisionType in_decider,
    final int in_width)
  {
    return new JPJustifierBasic(in_overflow, in_decider, in_width);
  }

  private static String justifyLine(
    final StringBuilder buffer,
    final SpaceTextDecisionType in_decider,
    final int max_width,
    final List<String> words)
  {
    final int word_count = words.size();

    /**
     * Inserting hard line breaks can result in lines with no words.
     */

    if (word_count == 0) {
      return "";
    }

    /**
     * Don't format single words.
     */

    if (word_count == 1) {
      return words.get(0);
    }

    Assertive.require(word_count > 1);

    /**
     * Decide how much of the line will be text.
     */

    final int text = JPJustifierBasic.sumLengths(words);

    /**
     * Decide how much of the line will be space.
     */

    final int space = max_width - text;

    /**
     * Allow the decider to cancel justification.
     */

    if (!in_decider.shouldJustify(space, text)) {
      return JPJustifierBasic.unjustified(buffer, words);
    }

    /**
     * Each word will have at least {@code each} spaces inserted between
     * it and the next word. There will be {@code rest} spaces left over.
     */

    final int gaps = word_count - 1;
    final int each = space / gaps;
    final int rest = space % gaps;

    final int all = text + (each * gaps) + rest;
    Assertive.require(all == max_width);

    /**
     * Assign {@code each} spaces to each gap.
     */

    final int[] spaces = new int[gaps];
    for (int index = 0; index < spaces.length; ++index) {
      spaces[index] = each;
    }

    /**
     * Now, distribute the remaining spaces over the gaps.
     */

    {
      int rest_distrib = rest;
      int space_index = 0;
      while (rest_distrib > 0) {
        spaces[space_index] += 1;
        space_index = (space_index + 1) % spaces.length;
        --rest_distrib;
      }
    }

    /**
     * Build the line.
     */

    buffer.setLength(0);
    for (int index = 0; index < word_count - 1; ++index) {
      buffer.append(words.get(index));
      JPJustifierBasic.spaces(buffer, spaces[index]);
    }

    buffer.append(words.get(word_count - 1));
    return buffer.toString();
  }

  private static String unjustified(
    final StringBuilder buffer,
    final List<String> words)
  {
    final int word_count = words.size();
    buffer.setLength(0);
    for (int index = 0; index < word_count; ++index) {
      buffer.append(words.get(index));
      if (index + 1 < word_count) {
        buffer.append(" ");
      }
    }
    return buffer.toString();
  }

  private static void spaces(
    final StringBuilder buffer,
    final int count)
  {
    for (int index = 0; index < count; ++index) {
      buffer.append(" ");
    }
  }

  private static int sumLengths(final List<String> words)
  {
    int sum = 0;
    for (int index = 0; index < words.size(); ++index) {
      sum += words.get(index).length();
    }
    return sum;
  }

  @Override
  public void addWord(final String w)
  {
    NullCheck.notNull(w);

    final String wt = w.trim();
    if (!this.canFit(wt)) {
      if (!this.line_words.isEmpty()) {
        this.finishLine();
      }

      if (!this.couldEverFit(wt)) {
        Assertive.require(this.line_buffer.length() == 0);
        Assertive.require(this.line_words.isEmpty());
        Assertive.require(this.line_words_sum == 0);
        Assertive.require(this.width <= wt.length());

        switch (this.overflow) {
          case OVERFLOW_TRUNCATE: {
            final String wtt = wt.substring(0, this.width - 1) + "…";
            this.line_words_sum = wtt.length() + 1;
            this.line_words.add(wtt);
            this.finishLine();
            return;
          }
          case OVERFLOW_ANYWAY: {
            this.lines_formatted.add(wt);
            return;
          }
        }
      }
    }

    this.line_words_sum += wt.length() + 1;
    this.line_words.add(wt);
  }

  @Override
  public void breakLine()
  {
    this.finishLine();
  }

  @Override
  public List<String> finish()
  {
    if (this.line_words_sum > 0) {
      this.finishLine();
    }

    final List<String> r = new ArrayList<>(this.lines_formatted);
    this.line_buffer.setLength(80);
    this.line_buffer.trimToSize();
    this.line_buffer.setLength(0);

    this.line_words.clear();
    this.line_words_sum = 0;
    this.lines_formatted.clear();
    return r;
  }

  private boolean couldEverFit(final CharSequence wt)
  {
    return wt.length() < this.width;
  }

  private void finishLine()
  {
    this.lines_formatted.add(JPJustifierBasic.justifyLine(
      this.line_buffer, this.decider, this.width, this.line_words));
    this.line_buffer.setLength(0);
    this.line_words_sum = 0;
    this.line_words.clear();
  }

  private boolean canFit(final CharSequence word)
  {
    return this.line_words_sum + word.length() + 1 < this.width;
  }

  /**
   * A function that, when evaluated, yields {@code true} iff a line with the
   * given number of space and text characters should be justified.
   */

  public interface SpaceTextDecisionType
  {
    /**
     * Decide whether or not to justify text.
     *
     * @param space The number of characters on the line that would be
     *              whitespace
     * @param text  The number of characters on the line that would be text
     *
     * @return {@code true} iff the line should be justified
     */

    boolean shouldJustify(
      int space,
      int text);
  }
}

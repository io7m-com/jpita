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

import java.util.ArrayList;
import java.util.List;

/**
 * An basic aligner that left-aligns text.
 */

public final class JPAlignerBasic implements JPAlignerType
{
  private final int width;
  private final List<String> lines_formatted;
  private final StringBuilder line_buffer;

  private JPAlignerBasic(
    final int in_width)
  {
    this.width = RangeCheck.checkGreaterInteger(
      in_width, "Width", 0, "Minimum width");
    this.lines_formatted = new ArrayList<>(16);
    this.line_buffer = new StringBuilder(this.width);
  }

  /**
   * Construct a new aligner.
   *
   * @param in_width The maximum width in characters
   *
   * @return A new aligner
   */

  public static JPAlignerType create(
    final int in_width)
  {
    return new JPAlignerBasic(in_width);
  }

  @Override
  public void addWord(final String w)
  {
    NullCheck.notNull(w, "Word");

    final String wt = w.trim();
    if (this.line_buffer.length() + wt.length() + 1 > this.width) {
      if (this.line_buffer.length() > 0) {
        this.lines_formatted.add(this.line_buffer.toString().trim());
        this.line_buffer.setLength(0);
      }
    }

    this.line_buffer.append(w);
    this.line_buffer.append(" ");
  }

  @Override
  public void breakLine()
  {
    this.lines_formatted.add(this.line_buffer.toString().trim());
    this.line_buffer.setLength(0);
  }

  @Override
  public List<String> finish()
  {
    if (this.line_buffer.length() > 0) {
      this.lines_formatted.add(this.line_buffer.toString().trim());
      this.line_buffer.setLength(80);
      this.line_buffer.trimToSize();
      this.line_buffer.setLength(0);
    }

    final List<String> rs = new ArrayList<>(this.lines_formatted);
    this.lines_formatted.clear();
    return rs;
  }

}

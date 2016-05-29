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

package com.io7m.jpita.tests.core;

import com.io7m.jpita.core.JPAlignerBasic;
import com.io7m.jpita.core.JPAlignerType;
import com.io7m.jpita.core.JPJustifierBasic;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public final class JPAlignerBasicTest
{
  @Test
  public void testOneWord()
  {
    final int width = 80;
    final JPAlignerType a = JPAlignerBasic.create(width);
    a.addWord("One");
    final List<String> rs = a.finish();
    JPTestUtilities.show(width, rs);

    Assert.assertEquals(1L, (long) rs.size());
    Assert.assertTrue(rs.get(0).startsWith("One"));
    Assert.assertTrue(rs.get(0).length() <= width);
  }

  @Test
  public void testWords()
    throws Exception
  {
    final int width = 60;
    final JPAlignerType a = JPAlignerBasic.create(width);
    JPTestUtilities.addAll(
      a, JPTestUtilities.resourceAsWords(
        JPAlignerBasicTest.class, "lorem.txt"));
    final List<String> rs = a.finish();
    JPTestUtilities.show(width, rs);

    Assert.assertEquals(9L, (long) rs.size());
    for (int index = 0; index < rs.size(); ++index) {
      final String line = rs.get(index);
      Assert.assertTrue(line.length() < width);
      Assert.assertFalse(Character.isWhitespace(line.codePointAt(0)));
      Assert.assertFalse(Character.isWhitespace(line.codePointAt(line.length() - 1)));
    }
  }
}

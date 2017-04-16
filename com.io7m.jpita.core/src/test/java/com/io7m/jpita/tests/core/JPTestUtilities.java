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

import com.io7m.jpita.core.JPAlignerType;
import com.io7m.junreachable.UnreachableCodeException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import java.util.List;

final class JPTestUtilities
{
  private JPTestUtilities()
  {
    throw new UnreachableCodeException();
  }

  static List<String> resourceAsWords(
    final Class<?> c,
    final String name)
    throws IOException
  {
    try (final InputStream is = c.getResourceAsStream(name)) {
      final byte[] r = IOUtils.toByteArray(is);
      final String s = new String(r, StandardCharsets.UTF_8);
      final String[] w = s.split("\\s+");
      final List<String> rs = new ArrayList<>(w.length);
      for (int index = 0; index < w.length; ++index) {
        rs.add(w[index].trim());
      }
      return rs;
    }
  }

  static void addAll(
    final JPAlignerType a,
    final List<String> s)
  {
    for (int index = 0; index < s.size(); ++index) {
      a.addWord(s.get(index));
    }
  }

  static void show(
    final int width,
    final List<String> rs)
  {
    for (int index = 0; index < rs.size(); ++index) {
      JPTestUtilities.ruler(width);
      System.out.print(rs.get(index));
      System.out.println();
    }
  }

  static void ruler(final int width)
  {
    for (int x = 0; x < width; ++x) {
      System.out.print("-");
    }
    System.out.println();
  }
}

/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.fox7.schematoolkit.bonxai.parser;
import java.util.*;
import eu.fox7.schematoolkit.bonxai.om.*;
import eu.fox7.schematoolkit.bonxai.om.Element;
import eu.fox7.schematoolkit.bonxai.om.ElementRef;
import eu.fox7.schematoolkit.bonxai.om.Annotation;
import eu.fox7.schematoolkit.common.*;

public class BonxaiJJParserTokenManager implements BonxaiJJParserConstants
{
    // Required by SetState
    void backup(int n) { input_stream.backup(n); }
  public  java.io.PrintStream debugStream = System.out;
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjStopStringLiteralDfa_3(int pos, long active0)
{
   switch (pos)
   {
      default :
         return -1;
   }
}
private final int jjStartNfa_3(int pos, long active0)
{
   return jjMoveNfa_3(jjStopStringLiteralDfa_3(pos, active0), pos + 1);
}
private final int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private final int jjStartNfaWithStates_3(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_3(state, pos + 1);
}
private final int jjMoveStringLiteralDfa0_3()
{
   switch(curChar)
   {
      case 34:
         return jjStopAtPos(0, 62);
      default :
         return jjMoveNfa_3(0, 0);
   }
}
private final void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private final void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private final void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}
private final void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}
private final void jjCheckNAddStates(int start)
{
   jjCheckNAdd(jjnextStates[start]);
   jjCheckNAdd(jjnextStates[start + 1]);
}
static final long[] jjbitVec0 = {
   0x0L, 0x0L, 0xfffff00000000000L, 0x7fffffL
};
static final long[] jjbitVec2 = {
   0x0L, 0x0L, 0x0L, 0xff7fffffff7fffffL
};
static final long[] jjbitVec3 = {
   0x7ff3ffffffffffffL, 0x7ffffffffffffdfeL, 0xffffffffffffffffL, 0xfc31ffffffffe00fL
};
static final long[] jjbitVec4 = {
   0xffffffL, 0xffffffffffff0000L, 0xf80001ffffffffffL, 0x3L
};
static final long[] jjbitVec5 = {
   0x0L, 0x0L, 0xfffffffbffffd740L, 0xffffd547f7fffL
};
static final long[] jjbitVec6 = {
   0xffffffffffffdffeL, 0xffffffffdffeffffL, 0xffffffffffff0003L, 0x33fcfffffff199fL
};
static final long[] jjbitVec7 = {
   0xfffe000000000000L, 0xfffffffe027fffffL, 0x7fL, 0x707ffffff0000L
};
static final long[] jjbitVec8 = {
   0x7fffffe00000000L, 0xfffe0000000007feL, 0x7cffffffffffffffL, 0x60002f7fffL
};
static final long[] jjbitVec9 = {
   0x23ffffffffffffe0L, 0x3ff000000L, 0x3c5fdfffff99fe0L, 0x30003b0000000L
};
static final long[] jjbitVec10 = {
   0x36dfdfffff987e0L, 0x1c00005e000000L, 0x23edfdfffffbafe0L, 0x100000000L
};
static final long[] jjbitVec11 = {
   0x23cdfdfffff99fe0L, 0x3b0000000L, 0x3bfc718d63dc7e0L, 0x0L
};
static final long[] jjbitVec12 = {
   0x3effdfffffddfe0L, 0x300000000L, 0x3effdfffffddfe0L, 0x340000000L
};
static final long[] jjbitVec13 = {
   0x3fffdfffffddfe0L, 0x300000000L, 0x0L, 0x0L
};
static final long[] jjbitVec14 = {
   0xd7ffffffffffeL, 0x3fL, 0x200d6caefef02596L, 0x1fL
};
static final long[] jjbitVec15 = {
   0x0L, 0x3fffffffeffL, 0x0L, 0x0L
};
static final long[] jjbitVec16 = {
   0x0L, 0x0L, 0xffffffff00000000L, 0x7fffffffff003fL
};
static final long[] jjbitVec17 = {
   0x500000000007daedL, 0x2c62ab82315001L, 0xf580c90040000000L, 0x201080000000007L
};
static final long[] jjbitVec18 = {
   0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffff0fffffffL, 0x3ffffffffffffffL
};
static final long[] jjbitVec19 = {
   0xffffffff3f3fffffL, 0x3fffffffaaff3f3fL, 0x5fdfffffffffffffL, 0x1fdc1fff0fcf1fdcL
};
static final long[] jjbitVec20 = {
   0x4c4000000000L, 0x0L, 0x7L, 0x0L
};
static final long[] jjbitVec21 = {
   0x0L, 0xfffffffffffffffeL, 0xfffffffe001fffffL, 0x7ffffffffffffffL
};
static final long[] jjbitVec22 = {
   0x1fffffffffe0L, 0x0L, 0x0L, 0x0L
};
static final long[] jjbitVec23 = {
   0xffffffffffffffffL, 0xffffffffffffffffL, 0xfffffffffL, 0x0L
};
private final int jjMoveNfa_3(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 1;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  kind = 61;
                  jjstateSet[jjnewStateCnt++] = 0;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  kind = 61;
                  jjstateSet[jjnewStateCnt++] = 0;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int hiByte = (int)(curChar >> 8);
         int i1 = hiByte >> 6;
         long l1 = 1L << (hiByte & 077);
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 61)
                     kind = 61;
                  jjstateSet[jjnewStateCnt++] = 0;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 1 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
private final int jjStopStringLiteralDfa_0(int pos, long active0)
{
   switch (pos)
   {
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
private final int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
private final int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 34:
         return jjStopAtPos(0, 5);
      case 38:
         return jjStopAtPos(0, 44);
      case 40:
         return jjStopAtPos(0, 34);
      case 41:
         return jjStopAtPos(0, 35);
      case 42:
         return jjStopAtPos(0, 47);
      case 43:
         return jjStopAtPos(0, 48);
      case 44:
         return jjStopAtPos(0, 41);
      case 46:
         return jjStopAtPos(0, 51);
      case 47:
         jjmatchedKind = 46;
         return jjMoveStringLiteralDfa1_0(0x200000000000L);
      case 58:
         return jjStopAtPos(0, 50);
      case 61:
         return jjStopAtPos(0, 40);
      case 63:
         return jjStopAtPos(0, 49);
      case 64:
         return jjStopAtPos(0, 42);
      case 91:
         return jjStopAtPos(0, 36);
      case 93:
         return jjStopAtPos(0, 37);
      case 97:
         return jjMoveStringLiteralDfa1_0(0x10000000e00000L);
      case 99:
         return jjMoveStringLiteralDfa1_0(0x8000L);
      case 100:
         return jjMoveStringLiteralDfa1_0(0x3200L);
      case 101:
         return jjMoveStringLiteralDfa1_0(0x1a000000L);
      case 102:
         return jjMoveStringLiteralDfa1_0(0x100000000L);
      case 103:
         return jjMoveStringLiteralDfa1_0(0x180100L);
      case 105:
         return jjMoveStringLiteralDfa1_0(0x4000L);
      case 107:
         return jjMoveStringLiteralDfa1_0(0x60000L);
      case 108:
         return jjMoveStringLiteralDfa1_0(0x40000000L);
      case 109:
         return jjMoveStringLiteralDfa1_0(0x5000000L);
      case 110:
         return jjMoveStringLiteralDfa1_0(0x400L);
      case 114:
         return jjMoveStringLiteralDfa1_0(0x80L);
      case 115:
         return jjMoveStringLiteralDfa1_0(0xa0000000L);
      case 116:
         return jjMoveStringLiteralDfa1_0(0x800L);
      case 117:
         return jjMoveStringLiteralDfa1_0(0x10000L);
      case 123:
         return jjStopAtPos(0, 38);
      case 124:
         return jjStopAtPos(0, 43);
      case 125:
         return jjStopAtPos(0, 39);
      default :
         return jjMoveNfa_0(0, 0);
   }
}
private final int jjMoveStringLiteralDfa1_0(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 47:
         if ((active0 & 0x200000000000L) != 0L)
            return jjStopAtPos(1, 45);
         break;
      case 97:
         return jjMoveStringLiteralDfa2_0(active0, 0x40002400L);
      case 101:
         return jjMoveStringLiteralDfa2_0(active0, 0x61200L);
      case 105:
         return jjMoveStringLiteralDfa2_0(active0, 0x105000000L);
      case 107:
         return jjMoveStringLiteralDfa2_0(active0, 0x80000000L);
      case 108:
         return jjMoveStringLiteralDfa2_0(active0, 0x18000000L);
      case 109:
         return jjMoveStringLiteralDfa2_0(active0, 0x2004000L);
      case 110:
         return jjMoveStringLiteralDfa2_0(active0, 0x10000000010000L);
      case 111:
         return jjMoveStringLiteralDfa2_0(active0, 0x8080L);
      case 114:
         return jjMoveStringLiteralDfa2_0(active0, 0x180100L);
      case 116:
         return jjMoveStringLiteralDfa2_0(active0, 0x20e00000L);
      case 121:
         return jjMoveStringLiteralDfa2_0(active0, 0x800L);
      default :
         break;
   }
   return jjStartNfa_0(0, active0);
}
private final int jjMoveStringLiteralDfa2_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(0, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(1, active0);
      return 2;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa3_0(active0, 0x100L);
      case 101:
         return jjMoveStringLiteralDfa3_0(active0, 0x18000000L);
      case 102:
         return jjMoveStringLiteralDfa3_0(active0, 0x1200L);
      case 105:
         return jjMoveStringLiteralDfa3_0(active0, 0x80010000L);
      case 109:
         return jjMoveStringLiteralDfa3_0(active0, 0x400L);
      case 110:
         return jjMoveStringLiteralDfa3_0(active0, 0x10000000008000L);
      case 111:
         return jjMoveStringLiteralDfa3_0(active0, 0x180080L);
      case 112:
         return jjMoveStringLiteralDfa3_0(active0, 0x2004800L);
      case 114:
         return jjMoveStringLiteralDfa3_0(active0, 0x20000000L);
      case 115:
         return jjMoveStringLiteralDfa3_0(active0, 0x4000000L);
      case 116:
         return jjMoveStringLiteralDfa3_0(active0, 0xe02000L);
      case 120:
         if ((active0 & 0x40000000L) != 0L)
            return jjStopAtPos(2, 30);
         return jjMoveStringLiteralDfa3_0(active0, 0x101000000L);
      case 121:
         if ((active0 & 0x20000L) != 0L)
         {
            jjmatchedKind = 17;
            jjmatchedPos = 2;
         }
         return jjMoveStringLiteralDfa3_0(active0, 0x40000L);
      default :
         break;
   }
   return jjStartNfa_0(1, active0);
}
private final int jjMoveStringLiteralDfa3_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(1, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(2, active0);
      return 3;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa4_0(active0, 0x3200L);
      case 101:
         if ((active0 & 0x800L) != 0L)
            return jjStopAtPos(3, 11);
         return jjMoveStringLiteralDfa4_0(active0, 0x101000400L);
      case 105:
         return jjMoveStringLiteralDfa4_0(active0, 0x20000000L);
      case 109:
         return jjMoveStringLiteralDfa4_0(active0, 0x18000100L);
      case 111:
         return jjMoveStringLiteralDfa4_0(active0, 0x10000000004000L);
      case 112:
         if ((active0 & 0x80000000L) != 0L)
            return jjStopAtPos(3, 31);
         break;
      case 113:
         return jjMoveStringLiteralDfa4_0(active0, 0x10000L);
      case 114:
         return jjMoveStringLiteralDfa4_0(active0, 0xe40000L);
      case 115:
         return jjMoveStringLiteralDfa4_0(active0, 0x4008000L);
      case 116:
         return jjMoveStringLiteralDfa4_0(active0, 0x2000080L);
      case 117:
         return jjMoveStringLiteralDfa4_0(active0, 0x180000L);
      default :
         break;
   }
   return jjStartNfa_0(2, active0);
}
private final int jjMoveStringLiteralDfa4_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(2, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(3, active0);
      return 4;
   }
   switch(curChar)
   {
      case 99:
         return jjMoveStringLiteralDfa5_0(active0, 0x20000000L);
      case 100:
         if ((active0 & 0x1000000L) != 0L)
            return jjStopAtPos(4, 24);
         else if ((active0 & 0x100000000L) != 0L)
            return jjStopAtPos(4, 32);
         break;
      case 101:
         return jjMoveStringLiteralDfa5_0(active0, 0x18040000L);
      case 105:
         return jjMoveStringLiteralDfa5_0(active0, 0x4e00000L);
      case 109:
         return jjMoveStringLiteralDfa5_0(active0, 0x100L);
      case 112:
         if ((active0 & 0x100000L) != 0L)
         {
            jjmatchedKind = 20;
            jjmatchedPos = 4;
         }
         return jjMoveStringLiteralDfa5_0(active0, 0x80000L);
      case 114:
         return jjMoveStringLiteralDfa5_0(active0, 0x4000L);
      case 115:
         if ((active0 & 0x80L) != 0L)
            return jjStopAtPos(4, 7);
         return jjMoveStringLiteralDfa5_0(active0, 0x400L);
      case 116:
         return jjMoveStringLiteralDfa5_0(active0, 0x1000000000a000L);
      case 117:
         return jjMoveStringLiteralDfa5_0(active0, 0x11200L);
      case 121:
         if ((active0 & 0x2000000L) != 0L)
            return jjStopAtPos(4, 25);
         break;
      default :
         break;
   }
   return jjStartNfa_0(3, active0);
}
private final int jjMoveStringLiteralDfa5_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(3, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(4, active0);
      return 5;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa6_0(active0, 0x10000000000100L);
      case 98:
         return jjMoveStringLiteralDfa6_0(active0, 0xe00000L);
      case 101:
         if ((active0 & 0x10000L) != 0L)
            return jjStopAtPos(5, 16);
         break;
      case 102:
         return jjMoveStringLiteralDfa6_0(active0, 0x40000L);
      case 108:
         return jjMoveStringLiteralDfa6_0(active0, 0x1200L);
      case 110:
         return jjMoveStringLiteralDfa6_0(active0, 0x1c000000L);
      case 112:
         return jjMoveStringLiteralDfa6_0(active0, 0x400L);
      case 114:
         return jjMoveStringLiteralDfa6_0(active0, 0x8000L);
      case 115:
         if ((active0 & 0x80000L) != 0L)
            return jjStopAtPos(5, 19);
         break;
      case 116:
         if ((active0 & 0x4000L) != 0L)
            return jjStopAtPos(5, 14);
         else if ((active0 & 0x20000000L) != 0L)
            return jjStopAtPos(5, 29);
         break;
      case 121:
         return jjMoveStringLiteralDfa6_0(active0, 0x2000L);
      default :
         break;
   }
   return jjStartNfa_0(4, active0);
}
private final int jjMoveStringLiteralDfa6_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(4, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(5, active0);
      return 6;
   }
   switch(curChar)
   {
      case 32:
         if ((active0 & 0x40000L) != 0L)
            return jjStopAtPos(6, 18);
         break;
      case 97:
         return jjMoveStringLiteralDfa7_0(active0, 0x8400L);
      case 103:
         if ((active0 & 0x4000000L) != 0L)
            return jjStopAtPos(6, 26);
         break;
      case 112:
         return jjMoveStringLiteralDfa7_0(active0, 0x2000L);
      case 114:
         if ((active0 & 0x100L) != 0L)
            return jjStopAtPos(6, 8);
         break;
      case 116:
         if ((active0 & 0x1000L) != 0L)
         {
            jjmatchedKind = 12;
            jjmatchedPos = 6;
         }
         else if ((active0 & 0x10000000L) != 0L)
         {
            jjmatchedKind = 28;
            jjmatchedPos = 6;
         }
         return jjMoveStringLiteralDfa7_0(active0, 0x10000008000200L);
      case 117:
         return jjMoveStringLiteralDfa7_0(active0, 0xe00000L);
      default :
         break;
   }
   return jjStartNfa_0(5, active0);
}
private final int jjMoveStringLiteralDfa7_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(5, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(6, active0);
      return 7;
   }
   switch(curChar)
   {
      case 32:
         return jjMoveStringLiteralDfa8_0(active0, 0x200L);
      case 99:
         return jjMoveStringLiteralDfa8_0(active0, 0x400L);
      case 101:
         return jjMoveStringLiteralDfa8_0(active0, 0x2000L);
      case 105:
         return jjMoveStringLiteralDfa8_0(active0, 0x10000000008000L);
      case 114:
         return jjMoveStringLiteralDfa8_0(active0, 0x8000000L);
      case 116:
         return jjMoveStringLiteralDfa8_0(active0, 0xe00000L);
      default :
         break;
   }
   return jjStartNfa_0(6, active0);
}
private final int jjMoveStringLiteralDfa8_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(6, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(7, active0);
      return 8;
   }
   switch(curChar)
   {
      case 101:
         if ((active0 & 0x400L) != 0L)
            return jjStopAtPos(8, 10);
         else if ((active0 & 0x800000L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 8;
         }
         return jjMoveStringLiteralDfa9_0(active0, 0x8600000L);
      case 110:
         return jjMoveStringLiteralDfa9_0(active0, 0x8200L);
      case 111:
         return jjMoveStringLiteralDfa9_0(active0, 0x10000000000000L);
      case 115:
         return jjMoveStringLiteralDfa9_0(active0, 0x2000L);
      default :
         break;
   }
   return jjStartNfa_0(7, active0);
}
private final int jjMoveStringLiteralDfa9_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(7, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(8, active0);
      return 9;
   }
   switch(curChar)
   {
      case 32:
         if ((active0 & 0x2000L) != 0L)
            return jjStopAtPos(9, 13);
         break;
      case 45:
         return jjMoveStringLiteralDfa10_0(active0, 0x200000L);
      case 97:
         return jjMoveStringLiteralDfa10_0(active0, 0x200L);
      case 102:
         if ((active0 & 0x8000000L) != 0L)
            return jjStopAtPos(9, 27);
         break;
      case 110:
         if ((active0 & 0x10000000000000L) != 0L)
            return jjStopAtPos(9, 52);
         break;
      case 114:
         return jjMoveStringLiteralDfa10_0(active0, 0x400000L);
      case 116:
         return jjMoveStringLiteralDfa10_0(active0, 0x8000L);
      default :
         break;
   }
   return jjStartNfa_0(8, active0);
}
private final int jjMoveStringLiteralDfa10_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(8, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(9, active0);
      return 10;
   }
   switch(curChar)
   {
      case 101:
         return jjMoveStringLiteralDfa11_0(active0, 0x400000L);
      case 103:
         return jjMoveStringLiteralDfa11_0(active0, 0x200000L);
      case 109:
         return jjMoveStringLiteralDfa11_0(active0, 0x200L);
      case 115:
         if ((active0 & 0x8000L) != 0L)
            return jjStopAtPos(10, 15);
         break;
      default :
         break;
   }
   return jjStartNfa_0(9, active0);
}
private final int jjMoveStringLiteralDfa11_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(9, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(10, active0);
      return 11;
   }
   switch(curChar)
   {
      case 101:
         return jjMoveStringLiteralDfa12_0(active0, 0x200L);
      case 102:
         if ((active0 & 0x400000L) != 0L)
            return jjStopAtPos(11, 22);
         break;
      case 114:
         return jjMoveStringLiteralDfa12_0(active0, 0x200000L);
      default :
         break;
   }
   return jjStartNfa_0(10, active0);
}
private final int jjMoveStringLiteralDfa12_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(10, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(11, active0);
      return 12;
   }
   switch(curChar)
   {
      case 111:
         return jjMoveStringLiteralDfa13_0(active0, 0x200000L);
      case 115:
         return jjMoveStringLiteralDfa13_0(active0, 0x200L);
      default :
         break;
   }
   return jjStartNfa_0(11, active0);
}
private final int jjMoveStringLiteralDfa13_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(11, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(12, active0);
      return 13;
   }
   switch(curChar)
   {
      case 112:
         return jjMoveStringLiteralDfa14_0(active0, 0x200L);
      case 117:
         return jjMoveStringLiteralDfa14_0(active0, 0x200000L);
      default :
         break;
   }
   return jjStartNfa_0(12, active0);
}
private final int jjMoveStringLiteralDfa14_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(12, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(13, active0);
      return 14;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa15_0(active0, 0x200L);
      case 112:
         if ((active0 & 0x200000L) != 0L)
            return jjStopAtPos(14, 21);
         break;
      default :
         break;
   }
   return jjStartNfa_0(13, active0);
}
private final int jjMoveStringLiteralDfa15_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(13, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(14, active0);
      return 15;
   }
   switch(curChar)
   {
      case 99:
         return jjMoveStringLiteralDfa16_0(active0, 0x200L);
      default :
         break;
   }
   return jjStartNfa_0(14, active0);
}
private final int jjMoveStringLiteralDfa16_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(14, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(15, active0);
      return 16;
   }
   switch(curChar)
   {
      case 101:
         if ((active0 & 0x200L) != 0L)
            return jjStopAtPos(16, 9);
         break;
      default :
         break;
   }
   return jjStartNfa_0(15, active0);
}
static final long[] jjbitVec24 = {
   0xfffffffffffffffeL, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL
};
static final long[] jjbitVec25 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
static final long[] jjbitVec26 = {
   0x0L, 0xffffffffffffc000L, 0x7fffffffL, 0x0L
};
static final long[] jjbitVec27 = {
   0x3fe00000080L, 0x0L, 0x0L, 0x0L
};
static final long[] jjbitVec28 = {
   0xffffffffffffffffL, 0xffffffffffffffffL, 0x3fffffffffL, 0x0L
};
static final long[] jjbitVec29 = {
   0xffffffffffffffffL, 0x30000003fL, 0x0L, 0x0L
};
static final long[] jjbitVec30 = {
   0x0L, 0x0L, 0x78L, 0x0L
};
static final long[] jjbitVec31 = {
   0x0L, 0x0L, 0xbbfffffbfffe0000L, 0x16L
};
static final long[] jjbitVec32 = {
   0x0L, 0x100000007f800L, 0x0L, 0x3d9fffc00000L
};
static final long[] jjbitVec33 = {
   0xd00000000000000eL, 0xc001e3fffL, 0xd00000000000000eL, 0xc0080399fL
};
static final long[] jjbitVec34 = {
   0xd000000000000004L, 0x3000000003987L, 0xd00000000000000eL, 0x3bbfL
};
static final long[] jjbitVec35 = {
   0xd00000000000000eL, 0xc0398fL, 0xc00000000000000cL, 0x803dc7L
};
static final long[] jjbitVec36 = {
   0xc00000000000000eL, 0x603ddfL, 0xc00000000000000cL, 0x603ddfL
};
static final long[] jjbitVec37 = {
   0xc00000000000000cL, 0x803dcfL, 0x0L, 0x0L
};
static final long[] jjbitVec38 = {
   0x7f2000000000000L, 0x7f80L, 0x1bf2000000000000L, 0x3f00L
};
static final long[] jjbitVec39 = {
   0xc2a0000003000000L, 0xfffe000000000000L, 0x2fe3ffffebf0fdfL, 0x0L
};
static final long[] jjbitVec40 = {
   0x0L, 0x0L, 0x0L, 0x21fff0000L
};
static final long[] jjbitVec41 = {
   0xfc0000000000L, 0x0L, 0x6000000L, 0x0L
};
static final long[] jjbitVec42 = {
   0x0L, 0x0L, 0x80000000000000L, 0x0L
};
static final long[] jjbitVec43 = {
   0x0L, 0x0L, 0x0L, 0x30000L
};
static final long[] jjbitVec44 = {
   0x0L, 0x0L, 0x80L, 0x0L
};
static final long[] jjbitVec45 = {
   0x0L, 0x1L, 0x0L, 0x0L
};
static final long[] jjbitVec46 = {
   0x0L, 0x40L, 0x0L, 0x40L
};
static final long[] jjbitVec47 = {
   0x3e000000000020L, 0x0L, 0x60000000L, 0x7000000000000000L
};
private final int jjMoveNfa_0(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 9;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 33)
                        kind = 33;
                     jjCheckNAdd(5);
                  }
                  else if (curChar == 35)
                     jjCheckNAddStates(0, 2);
                  break;
               case 1:
                  if ((0xffffffffffffdbffL & l) != 0L)
                     jjCheckNAddStates(0, 2);
                  break;
               case 2:
                  if ((0x2400L & l) != 0L && kind > 6)
                     kind = 6;
                  break;
               case 3:
                  if (curChar == 10 && kind > 6)
                     kind = 6;
                  break;
               case 4:
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 3;
                  break;
               case 5:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 33)
                     kind = 33;
                  jjCheckNAdd(5);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 1:
                  jjAddStates(0, 2);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int hiByte = (int)(curChar >> 8);
         int i1 = hiByte >> 6;
         long l1 = 1L << (hiByte & 077);
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if (jjCanMove_2(hiByte, i1, i2, l1, l2))
                  {
                     if (kind > 55)
                        kind = 55;
                  }
                  if (jjCanMove_3(hiByte, i1, i2, l1, l2))
                  {
                     if (kind > 56)
                        kind = 56;
                  }
                  if (jjCanMove_4(hiByte, i1, i2, l1, l2))
                  {
                     if (kind > 57)
                        kind = 57;
                  }
                  break;
               case 1:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjAddStates(0, 2);
                  break;
               case 6:
                  if (jjCanMove_2(hiByte, i1, i2, l1, l2) && kind > 55)
                     kind = 55;
                  break;
               case 7:
                  if (jjCanMove_3(hiByte, i1, i2, l1, l2) && kind > 56)
                     kind = 56;
                  break;
               case 8:
                  if (jjCanMove_4(hiByte, i1, i2, l1, l2) && kind > 57)
                     kind = 57;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 9 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
private final int jjMoveStringLiteralDfa0_2()
{
   return jjMoveNfa_2(0, 0);
}
private final int jjMoveNfa_2(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 1;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x3ffc00000000000L & l) == 0L)
                     break;
                  kind = 60;
                  jjstateSet[jjnewStateCnt++] = 0;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  kind = 60;
                  jjstateSet[jjnewStateCnt++] = 0;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int hiByte = (int)(curChar >> 8);
         int i1 = hiByte >> 6;
         long l1 = 1L << (hiByte & 077);
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 60)
                     kind = 60;
                  jjstateSet[jjnewStateCnt++] = 0;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 1 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
private final int jjStopStringLiteralDfa_1(int pos, long active0)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0x10000000000000L) != 0L)
         {
            jjmatchedKind = 58;
            return 15;
         }
         return -1;
      case 1:
         if ((active0 & 0x10000000000000L) != 0L)
         {
            jjmatchedKind = 58;
            jjmatchedPos = 1;
            return 15;
         }
         return -1;
      case 2:
         if ((active0 & 0x10000000000000L) != 0L)
         {
            jjmatchedKind = 58;
            jjmatchedPos = 2;
            return 15;
         }
         return -1;
      case 3:
         if ((active0 & 0x10000000000000L) != 0L)
         {
            jjmatchedKind = 58;
            jjmatchedPos = 3;
            return 15;
         }
         return -1;
      case 4:
         if ((active0 & 0x10000000000000L) != 0L)
         {
            jjmatchedKind = 58;
            jjmatchedPos = 4;
            return 15;
         }
         return -1;
      case 5:
         if ((active0 & 0x10000000000000L) != 0L)
         {
            jjmatchedKind = 58;
            jjmatchedPos = 5;
            return 15;
         }
         return -1;
      case 6:
         if ((active0 & 0x10000000000000L) != 0L)
         {
            jjmatchedKind = 58;
            jjmatchedPos = 6;
            return 15;
         }
         return -1;
      case 7:
         if ((active0 & 0x10000000000000L) != 0L)
         {
            jjmatchedKind = 58;
            jjmatchedPos = 7;
            return 15;
         }
         return -1;
      case 8:
         if ((active0 & 0x10000000000000L) != 0L)
         {
            jjmatchedKind = 58;
            jjmatchedPos = 8;
            return 15;
         }
         return -1;
      default :
         return -1;
   }
}
private final int jjStartNfa_1(int pos, long active0)
{
   return jjMoveNfa_1(jjStopStringLiteralDfa_1(pos, active0), pos + 1);
}
private final int jjStartNfaWithStates_1(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_1(state, pos + 1);
}
private final int jjMoveStringLiteralDfa0_1()
{
   switch(curChar)
   {
      case 34:
         return jjStopAtPos(0, 5);
      case 38:
         return jjStopAtPos(0, 44);
      case 40:
         return jjStopAtPos(0, 34);
      case 41:
         return jjStopAtPos(0, 35);
      case 42:
         return jjStopAtPos(0, 47);
      case 43:
         return jjStopAtPos(0, 48);
      case 44:
         return jjStopAtPos(0, 41);
      case 46:
         return jjStopAtPos(0, 51);
      case 47:
         jjmatchedKind = 46;
         return jjMoveStringLiteralDfa1_1(0x200000000000L);
      case 58:
         return jjStopAtPos(0, 50);
      case 61:
         return jjStopAtPos(0, 40);
      case 63:
         return jjStopAtPos(0, 49);
      case 64:
         return jjStopAtPos(0, 42);
      case 91:
         return jjStopAtPos(0, 36);
      case 93:
         return jjStopAtPos(0, 37);
      case 97:
         return jjMoveStringLiteralDfa1_1(0x10000000000000L);
      case 123:
         return jjStopAtPos(0, 38);
      case 124:
         return jjStopAtPos(0, 43);
      case 125:
         return jjStopAtPos(0, 39);
      default :
         return jjMoveNfa_1(0, 0);
   }
}
private final int jjMoveStringLiteralDfa1_1(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_1(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 47:
         if ((active0 & 0x200000000000L) != 0L)
            return jjStopAtPos(1, 45);
         break;
      case 110:
         return jjMoveStringLiteralDfa2_1(active0, 0x10000000000000L);
      default :
         break;
   }
   return jjStartNfa_1(0, active0);
}
private final int jjMoveStringLiteralDfa2_1(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_1(0, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_1(1, active0);
      return 2;
   }
   switch(curChar)
   {
      case 110:
         return jjMoveStringLiteralDfa3_1(active0, 0x10000000000000L);
      default :
         break;
   }
   return jjStartNfa_1(1, active0);
}
private final int jjMoveStringLiteralDfa3_1(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_1(1, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_1(2, active0);
      return 3;
   }
   switch(curChar)
   {
      case 111:
         return jjMoveStringLiteralDfa4_1(active0, 0x10000000000000L);
      default :
         break;
   }
   return jjStartNfa_1(2, active0);
}
private final int jjMoveStringLiteralDfa4_1(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_1(2, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_1(3, active0);
      return 4;
   }
   switch(curChar)
   {
      case 116:
         return jjMoveStringLiteralDfa5_1(active0, 0x10000000000000L);
      default :
         break;
   }
   return jjStartNfa_1(3, active0);
}
private final int jjMoveStringLiteralDfa5_1(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_1(3, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_1(4, active0);
      return 5;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa6_1(active0, 0x10000000000000L);
      default :
         break;
   }
   return jjStartNfa_1(4, active0);
}
private final int jjMoveStringLiteralDfa6_1(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_1(4, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_1(5, active0);
      return 6;
   }
   switch(curChar)
   {
      case 116:
         return jjMoveStringLiteralDfa7_1(active0, 0x10000000000000L);
      default :
         break;
   }
   return jjStartNfa_1(5, active0);
}
private final int jjMoveStringLiteralDfa7_1(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_1(5, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_1(6, active0);
      return 7;
   }
   switch(curChar)
   {
      case 105:
         return jjMoveStringLiteralDfa8_1(active0, 0x10000000000000L);
      default :
         break;
   }
   return jjStartNfa_1(6, active0);
}
private final int jjMoveStringLiteralDfa8_1(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_1(6, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_1(7, active0);
      return 8;
   }
   switch(curChar)
   {
      case 111:
         return jjMoveStringLiteralDfa9_1(active0, 0x10000000000000L);
      default :
         break;
   }
   return jjStartNfa_1(7, active0);
}
private final int jjMoveStringLiteralDfa9_1(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_1(7, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_1(8, active0);
      return 9;
   }
   switch(curChar)
   {
      case 110:
         if ((active0 & 0x10000000000000L) != 0L)
            return jjStartNfaWithStates_1(9, 52, 15);
         break;
      default :
         break;
   }
   return jjStartNfa_1(8, active0);
}
private final int jjMoveNfa_1(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 15;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 15:
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 58)
                        kind = 58;
                     jjCheckNAdd(10);
                  }
                  else if (curChar == 58)
                     jjstateSet[jjnewStateCnt++] = 14;
                  break;
               case 0:
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 33)
                        kind = 33;
                     jjCheckNAdd(5);
                  }
                  else if (curChar == 35)
                     jjCheckNAddStates(0, 2);
                  break;
               case 1:
                  if ((0xffffffffffffdbffL & l) != 0L)
                     jjCheckNAddStates(0, 2);
                  break;
               case 2:
                  if ((0x2400L & l) != 0L && kind > 6)
                     kind = 6;
                  break;
               case 3:
                  if (curChar == 10 && kind > 6)
                     kind = 6;
                  break;
               case 4:
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 3;
                  break;
               case 5:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 33)
                     kind = 33;
                  jjCheckNAdd(5);
                  break;
               case 10:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 58)
                     kind = 58;
                  jjCheckNAdd(10);
                  break;
               case 12:
                  if (curChar == 58)
                     jjstateSet[jjnewStateCnt++] = 14;
                  break;
               case 13:
                  if (curChar == 47 && kind > 59)
                     kind = 59;
                  break;
               case 14:
                  if (curChar == 47)
                     jjstateSet[jjnewStateCnt++] = 13;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 15:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(11, 12);
                  if ((0x7fffffe07fffffeL & l) != 0L)
                  {
                     if (kind > 58)
                        kind = 58;
                     jjCheckNAdd(10);
                  }
                  break;
               case 0:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 58)
                     kind = 58;
                  jjCheckNAddStates(3, 5);
                  break;
               case 1:
                  jjAddStates(0, 2);
                  break;
               case 10:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 58)
                     kind = 58;
                  jjCheckNAdd(10);
                  break;
               case 11:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(11, 12);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int hiByte = (int)(curChar >> 8);
         int i1 = hiByte >> 6;
         long l1 = 1L << (hiByte & 077);
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 15:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                  {
                     if (kind > 58)
                        kind = 58;
                     jjCheckNAdd(10);
                  }
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(11, 12);
                  break;
               case 0:
                  if (jjCanMove_2(hiByte, i1, i2, l1, l2))
                  {
                     if (kind > 55)
                        kind = 55;
                  }
                  if (jjCanMove_3(hiByte, i1, i2, l1, l2))
                  {
                     if (kind > 56)
                        kind = 56;
                  }
                  if (jjCanMove_4(hiByte, i1, i2, l1, l2))
                  {
                     if (kind > 57)
                        kind = 57;
                  }
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                  {
                     if (kind > 58)
                        kind = 58;
                     jjCheckNAddStates(3, 5);
                  }
                  break;
               case 1:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjAddStates(0, 2);
                  break;
               case 6:
                  if (jjCanMove_2(hiByte, i1, i2, l1, l2) && kind > 55)
                     kind = 55;
                  break;
               case 7:
                  if (jjCanMove_3(hiByte, i1, i2, l1, l2) && kind > 56)
                     kind = 56;
                  break;
               case 8:
                  if (jjCanMove_4(hiByte, i1, i2, l1, l2) && kind > 57)
                     kind = 57;
                  break;
               case 9:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 58)
                     kind = 58;
                  jjCheckNAddStates(3, 5);
                  break;
               case 10:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 58)
                     kind = 58;
                  jjCheckNAdd(10);
                  break;
               case 11:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(11, 12);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 15 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   1, 2, 4, 10, 11, 12, 
};
private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2)
{
   switch(hiByte)
   {
      case 0:
         return ((jjbitVec2[i2] & l2) != 0L);
      case 1:
         return ((jjbitVec3[i2] & l2) != 0L);
      case 2:
         return ((jjbitVec4[i2] & l2) != 0L);
      case 3:
         return ((jjbitVec5[i2] & l2) != 0L);
      case 4:
         return ((jjbitVec6[i2] & l2) != 0L);
      case 5:
         return ((jjbitVec7[i2] & l2) != 0L);
      case 6:
         return ((jjbitVec8[i2] & l2) != 0L);
      case 9:
         return ((jjbitVec9[i2] & l2) != 0L);
      case 10:
         return ((jjbitVec10[i2] & l2) != 0L);
      case 11:
         return ((jjbitVec11[i2] & l2) != 0L);
      case 12:
         return ((jjbitVec12[i2] & l2) != 0L);
      case 13:
         return ((jjbitVec13[i2] & l2) != 0L);
      case 14:
         return ((jjbitVec14[i2] & l2) != 0L);
      case 15:
         return ((jjbitVec15[i2] & l2) != 0L);
      case 16:
         return ((jjbitVec16[i2] & l2) != 0L);
      case 17:
         return ((jjbitVec17[i2] & l2) != 0L);
      case 30:
         return ((jjbitVec18[i2] & l2) != 0L);
      case 31:
         return ((jjbitVec19[i2] & l2) != 0L);
      case 33:
         return ((jjbitVec20[i2] & l2) != 0L);
      case 48:
         return ((jjbitVec21[i2] & l2) != 0L);
      case 49:
         return ((jjbitVec22[i2] & l2) != 0L);
      case 215:
         return ((jjbitVec23[i2] & l2) != 0L);
      default : 
         if ((jjbitVec0[i1] & l1) != 0L)
            return true;
         return false;
   }
}
private static final boolean jjCanMove_1(int hiByte, int i1, int i2, long l1, long l2)
{
   switch(hiByte)
   {
      case 0:
         return ((jjbitVec25[i2] & l2) != 0L);
      default : 
         if ((jjbitVec24[i1] & l1) != 0L)
            return true;
         return false;
   }
}
private static final boolean jjCanMove_2(int hiByte, int i1, int i2, long l1, long l2)
{
   switch(hiByte)
   {
      case 48:
         return ((jjbitVec27[i2] & l2) != 0L);
      case 159:
         return ((jjbitVec28[i2] & l2) != 0L);
      default : 
         if ((jjbitVec26[i1] & l1) != 0L)
            return true;
         return false;
   }
}
private static final boolean jjCanMove_3(int hiByte, int i1, int i2, long l1, long l2)
{
   switch(hiByte)
   {
      case 3:
         return ((jjbitVec29[i2] & l2) != 0L);
      case 4:
         return ((jjbitVec30[i2] & l2) != 0L);
      case 5:
         return ((jjbitVec31[i2] & l2) != 0L);
      case 6:
         return ((jjbitVec32[i2] & l2) != 0L);
      case 9:
         return ((jjbitVec33[i2] & l2) != 0L);
      case 10:
         return ((jjbitVec34[i2] & l2) != 0L);
      case 11:
         return ((jjbitVec35[i2] & l2) != 0L);
      case 12:
         return ((jjbitVec36[i2] & l2) != 0L);
      case 13:
         return ((jjbitVec37[i2] & l2) != 0L);
      case 14:
         return ((jjbitVec38[i2] & l2) != 0L);
      case 15:
         return ((jjbitVec39[i2] & l2) != 0L);
      case 32:
         return ((jjbitVec40[i2] & l2) != 0L);
      case 48:
         return ((jjbitVec41[i2] & l2) != 0L);
      default : 
         return false;
   }
}
private static final boolean jjCanMove_4(int hiByte, int i1, int i2, long l1, long l2)
{
   switch(hiByte)
   {
      case 0:
         return ((jjbitVec42[i2] & l2) != 0L);
      case 2:
         return ((jjbitVec43[i2] & l2) != 0L);
      case 3:
         return ((jjbitVec44[i2] & l2) != 0L);
      case 6:
         return ((jjbitVec45[i2] & l2) != 0L);
      case 14:
         return ((jjbitVec46[i2] & l2) != 0L);
      case 48:
         return ((jjbitVec47[i2] & l2) != 0L);
      default : 
         return false;
   }
}
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, null, null, "\162\157\157\164\163", 
"\147\162\141\155\155\141\162", "\144\145\146\141\165\154\164\40\156\141\155\145\163\160\141\143\145", 
"\156\141\155\145\163\160\141\143\145", "\164\171\160\145", "\144\145\146\141\165\154\164", 
"\144\141\164\141\164\171\160\145\163\40", "\151\155\160\157\162\164", "\143\157\156\163\164\162\141\151\156\164\163", 
"\165\156\151\161\165\145", "\153\145\171", "\153\145\171\162\145\146\40", "\147\162\157\165\160\163", 
"\147\162\157\165\160", "\141\164\164\162\151\142\165\164\145\55\147\162\157\165\160", 
"\141\164\164\162\151\142\165\164\145\162\145\146", "\141\164\164\162\151\142\165\164\145", "\155\151\170\145\144", 
"\145\155\160\164\171", "\155\151\163\163\151\156\147", "\145\154\145\155\145\156\164\162\145\146", 
"\145\154\145\155\145\156\164", "\163\164\162\151\143\164", "\154\141\170", "\163\153\151\160", 
"\146\151\170\145\144", null, "\50", "\51", "\133", "\135", "\173", "\175", "\75", "\54", "\100", 
"\174", "\46", "\57\57", "\57", "\52", "\53", "\77", "\72", "\56", 
"\141\156\156\157\164\141\164\151\157\156", null, null, null, null, null, null, null, null, null, null, };
public static final String[] lexStateNames = {
   "DEFAULT", 
   "NAME", 
   "URIX", 
   "QUOTATIONX", 
};
public static final int[] jjnewLexState = {
   -1, -1, -1, -1, -1, 3, -1, 1, -1, 1, 1, 1, -1, 1, -1, -1, 1, 1, 1, -1, 1, 1, 1, 1, -1, 
   -1, -1, 1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
   -1, -1, -1, -1, -1, -1, -1, -1, -1, 2, -1, -1, 0, 
};
static final long[] jjtoToken = {
   0x379fffffffffff81L, 
};
static final long[] jjtoSkip = {
   0x400000000000007eL, 
};
static final long[] jjtoSpecial = {
   0x40L, 
};
static final long[] jjtoMore = {
   0x800000000000000L, 
};
protected CharStream input_stream;
private final int[] jjrounds = new int[15];
private final int[] jjstateSet = new int[30];
protected char curChar;
public BonxaiJJParserTokenManager(CharStream stream){
   input_stream = stream;
}
public BonxaiJJParserTokenManager(CharStream stream, int lexState){
   this(stream);
   SwitchTo(lexState);
}
public void ReInit(CharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
private final void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 15; i-- > 0;)
      jjrounds[i] = 0x80000000;
}
public void ReInit(CharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}
public void SwitchTo(int lexState)
{
   if (lexState >= 4 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

protected Token jjFillToken()
{
   Token t = Token.newToken(jjmatchedKind);
   t.kind = jjmatchedKind;
   String im = jjstrLiteralImages[jjmatchedKind];
   t.image = (im == null) ? input_stream.GetImage() : im;
   t.beginLine = input_stream.getBeginLine();
   t.beginColumn = input_stream.getBeginColumn();
   t.endLine = input_stream.getEndLine();
   t.endColumn = input_stream.getEndColumn();
   return t;
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

public Token getNextToken() 
{
  int kind;
  Token specialToken = null;
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {   
   try   
   {     
      curChar = input_stream.BeginToken();
   }     
   catch(java.io.IOException e)
   {        
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      matchedToken.specialToken = specialToken;
      return matchedToken;
   }

   for (;;)
   {
     switch(curLexState)
     {
       case 0:
         try { input_stream.backup(0);
            while (curChar <= 32 && (0x100002600L & (1L << curChar)) != 0L)
               curChar = input_stream.BeginToken();
         }
         catch (java.io.IOException e1) { continue EOFLoop; }
         jjmatchedKind = 0x7fffffff;
         jjmatchedPos = 0;
         curPos = jjMoveStringLiteralDfa0_0();
         break;
       case 1:
         try { input_stream.backup(0);
            while (curChar <= 32 && (0x100002600L & (1L << curChar)) != 0L)
               curChar = input_stream.BeginToken();
         }
         catch (java.io.IOException e1) { continue EOFLoop; }
         jjmatchedKind = 0x7fffffff;
         jjmatchedPos = 0;
         curPos = jjMoveStringLiteralDfa0_1();
         break;
       case 2:
         jjmatchedKind = 0x7fffffff;
         jjmatchedPos = 0;
         curPos = jjMoveStringLiteralDfa0_2();
         break;
       case 3:
         jjmatchedKind = 0x7fffffff;
         jjmatchedPos = 0;
         curPos = jjMoveStringLiteralDfa0_3();
         break;
     }
     if (jjmatchedKind != 0x7fffffff)
     {
        if (jjmatchedPos + 1 < curPos)
           input_stream.backup(curPos - jjmatchedPos - 1);
        if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
        {
           matchedToken = jjFillToken();
           matchedToken.specialToken = specialToken;
       if (jjnewLexState[jjmatchedKind] != -1)
         curLexState = jjnewLexState[jjmatchedKind];
           return matchedToken;
        }
        else if ((jjtoSkip[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
        {
           if ((jjtoSpecial[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
           {
              matchedToken = jjFillToken();
              if (specialToken == null)
                 specialToken = matchedToken;
              else
              {
                 matchedToken.specialToken = specialToken;
                 specialToken = (specialToken.next = matchedToken);
              }
           }
         if (jjnewLexState[jjmatchedKind] != -1)
           curLexState = jjnewLexState[jjmatchedKind];
           continue EOFLoop;
        }
      if (jjnewLexState[jjmatchedKind] != -1)
        curLexState = jjnewLexState[jjmatchedKind];
        curPos = 0;
        jjmatchedKind = 0x7fffffff;
        try {
           curChar = input_stream.readChar();
           continue;
        }
        catch (java.io.IOException e1) { }
     }
     int error_line = input_stream.getEndLine();
     int error_column = input_stream.getEndColumn();
     String error_after = null;
     boolean EOFSeen = false;
     try { input_stream.readChar(); input_stream.backup(1); }
     catch (java.io.IOException e1) {
        EOFSeen = true;
        error_after = curPos <= 1 ? "" : input_stream.GetImage();
        if (curChar == '\n' || curChar == '\r') {
           error_line++;
           error_column = 0;
        }
        else
           error_column++;
     }
     if (!EOFSeen) {
        input_stream.backup(1);
        error_after = curPos <= 1 ? "" : input_stream.GetImage();
     }
     throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
   }
  }
}

}

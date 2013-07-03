/*
* Highlighter.java - The Highlighter is the texteara painter
*
* Copyright (C) 2004, 2010 Matthieu Casanova
* Copyright 2012 Matthias Niewerth
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package eu.fox7.jedit;

import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaExtension;

import java.awt.*;
import java.util.Collection;

/**
 * The Highlighter is the TextAreaExtension that will look for some String to
 * highlightList in the textarea and draw a rectangle in it's background.
 *
 * @author Matthieu Casanova
 */
class Highlighter extends TextAreaExtension {
	private final JEditTextArea textArea;
	private final HighlightManager highlightManager = BonXaiPlugin.getHighlightManager();
	private FontMetrics fm;

//	private final HighlightManager highlightManager;
	private AlphaComposite blend;
	private float alpha=(float) 0.5;

	public static final int MAX_LINE_LENGTH = 10000;

	Highlighter(JEditTextArea textArea) {
//		alpha = ((float)jEdit.getIntegerProperty(HighlightOptionPane.PROP_ALPHA, 50)) / 100f;
		blend = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
//		highlightManager = HighlightManagerTableModel.getManager();
		this.textArea = textArea;
	}
	
	public void setAlphaComposite(float alpha)
	{
		if (this.alpha != alpha)
		{
			this.alpha = alpha;
			blend = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		}
	} 

	@Override
	public void paintScreenLineRange(Graphics2D gfx, int firstLine, int lastLine, int[] physicalLines, int[] start, int[] end, int y, int lineHeight)
	{
		fm = textArea.getPainter().getFontMetrics();
//		if (highlightManager.isHighlightEnable() &&
//		    highlightManager.countHighlights() != 0)
			super.paintScreenLineRange(gfx, firstLine, lastLine, physicalLines, start, end, y, lineHeight);
	} 

	/**
	 * Called by the text area when the extension is to paint a
	 * screen line which has an associated physical line number in
	 * the buffer. Note that since one physical line may consist of
	 * several screen lines due to soft wrap, the start and end
	 * offsets of the screen line are passed in as well.
	 *
	 * @param gfx The graphics context
	 * @param screenLine The screen line number
	 * @param physicalLine The physical line number
	 * @param start The offset where the screen line begins, from
	 * the start of the buffer
	 * @param end The offset where the screen line ends, from the
	 * start of the buffer
	 * @param y The y co-ordinate of the top of the line's
	 * bounding box
	 * @since jEdit 4.0pre4
	 */
	@Override
	public void paintValidLine(Graphics2D gfx,
				   int screenLine,
				   int physicalLine,
				   int start,
				   int end,
				   int y)
	{
//		System.err.println("paintValidLine: " + screenLine + " " + physicalLine);
		JEditBuffer buffer = textArea.getBuffer();
		int lineStartOffset = buffer.getLineStartOffset(physicalLine);
//		int lineEndOffset = buffer.getLineEndOffset(physicalLine);
//		int length = buffer.getLineLength(physicalLine);

		int screenToPhysicalOffset = start - lineStartOffset;
		if (screenToPhysicalOffset!=0)
			System.err.print("screenToPhysicalOffset: " + screenToPhysicalOffset);


//			highlightManager.getReadLock();
		Collection<Highlight> highlights = highlightManager.getHighlights(buffer, start, end-1);
		for (Highlight highlight: highlights)
			highlight(highlight, buffer, gfx, physicalLine, y, screenToPhysicalOffset);
	}
	
	private void highlight(Highlight highlight,
			       JEditBuffer buffer,
			       Graphics2D gfx,
			       int physicalLine,
			       int y,
			       int screenToPhysicalOffset) {
			int startOffset = Math.max(highlight.getStart(), buffer.getLineStartOffset(physicalLine));
			int endOffset = Math.min(highlight.getEnd(), buffer.getLineEndOffset(physicalLine)-1);
			_highlight(highlight.getColor(), gfx, physicalLine, startOffset, endOffset, y, true);
	}

	private void _highlight(Color highlightColor,
				Graphics2D gfx,
				int physicalLine,
				int startOffset,
				int endOffset,
				int y,
				boolean filled)
	{
		Point p = textArea.offsetToXY(startOffset);
		if (p == null)
		{
			// The start offset was not visible
			return;
		}
		int startX = p.x;

		p = textArea.offsetToXY(endOffset);
		if (p == null)
		{
			// The end offset was not visible
			return;
		}
		int endX = p.x;
		
//		System.err.println("startX: "+startX +" endX: "+endX);
		Color oldColor = gfx.getColor();
		Composite oldComposite = gfx.getComposite();
		gfx.setColor(highlightColor);
		gfx.setComposite(blend);
//		if (filled)
//		System.err.println("Color: " + highlightColor);
//		System.err.println("fillRoundRect: " + startX +" "+ y +" "+ (endX - startX) +" "+ (fm.getHeight() - 1));
		gfx.fillRoundRect(startX, y, endX - startX, fm.getHeight() - 1, 5, 5);

//		if (square)
//		{
//			gfx.setColor(squareColor);
//			gfx.drawRoundRect(startX, y, endX - startX, fm.getHeight() - 1,5,5);
//		}
//		else if (!filled)
//		{
//			gfx.drawRoundRect(startX, y, endX - startX, fm.getHeight() - 1,5,5);
//		}

		gfx.setColor(oldColor);
		gfx.setComposite(oldComposite);
	}
	
	@Override
	public String getToolTipText(int x, int y) {
		int offset = textArea.xyToOffset(x, y);
		Point p = textArea.offsetToXY(offset);
		if (p.x<(x-20))
			return null;
		return highlightManager.getTooltip(textArea.getBuffer(),offset);
	}
	
}

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

package eu.fox7.util;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

public class FormattedWriter extends FilterWriter {
	public FormattedWriter(Writer out) {
		super(out);
	}

	private String indent = "  ";
	private String currentIndent = "";
	private int maxLineWidth = 80;
	private int lineWidth = 0;
	private int indentLevel = 0;
	private String lineSeparator = System.getProperty("line.separator");
	private StringBuffer lineBuffer = new StringBuffer(200);
	private boolean startOfRow = true;
	private String oldIndent = "";
	
	public void pushIndent() throws IOException {
		++indentLevel;
		currentIndent+=indent;
		if (startOfRow  && lineBuffer.length()==0) {
			this.out.append(currentIndent);
			startOfRow = false;
		}
	}
	
	public void popIndent() {
		--indentLevel;
		currentIndent=currentIndent.substring(0, currentIndent.length() - indent.length());
		if (startOfRow && lineBuffer.length() == 0)
			oldIndent = currentIndent;
	}
	
	public void appendLine(String str) throws IOException {
		this.write(str);
		this.newLine();
	}
	
	public void allowBreak(String str) throws IOException {
		this.write(str);
		this.allowBreak();
	}
	
	public void newLine() throws IOException {
		//flushBuffer
		allowBreak();
		//actually do the linebreak
		doLineBreak();		
	}
	
	public void allowBreak() throws IOException {
		if (lineWidth + lineBuffer.length() > maxLineWidth)
			doLineBreak();
			
		if (startOfRow) {
			this.out.append(oldIndent);
			startOfRow = false;
		}
		this.out.append(lineBuffer);
		lineWidth += lineBuffer.length();
		lineBuffer.setLength(0);
		oldIndent = currentIndent;
	}
	
	private void doLineBreak() throws IOException {
		this.out.append(lineSeparator);
		startOfRow = true;
		this.lineWidth = this.currentIndent.length();
	}

	/* (non-Javadoc)
	 * @see java.io.FilterWriter#close()
	 */
	@Override
	public void close() throws IOException {
		if (lineBuffer.length()>0)
			newLine();
		super.close();
	}

	/* (non-Javadoc)
	 * @see java.io.FilterWriter#flush()
	 */
	@Override
	public void flush() throws IOException {
		super.flush();
	}

	/* (non-Javadoc)
	 * @see java.io.FilterWriter#write(char[], int, int)
	 */
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		throw new IOException("write not supported.");
	}

	/* (non-Javadoc)
	 * @see java.io.FilterWriter#write(int)
	 */
	@Override
	public void write(int c) throws IOException {
		throw new IOException("write not supported.");
	}

	/* (non-Javadoc)
	 * @see java.io.FilterWriter#write(java.lang.String, int, int)
	 */
	@Override
	public void write(String str, int off, int len) throws IOException {
		lineBuffer.append(str.substring(off, off+len));
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#append(char)
	 */
	@Override
	public Writer append(char c) throws IOException {
		lineBuffer.append(c);
		return this;
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#append(java.lang.CharSequence, int, int)
	 */
	@Override
	public Writer append(CharSequence csq, int start, int end)
			throws IOException {
		lineBuffer.append(csq, start, end);
		return this;
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#append(java.lang.CharSequence)
	 */
	@Override
	public Writer append(CharSequence csq) throws IOException {
		lineBuffer.append(csq);
		return this;
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#write(char[])
	 */
	@Override
	public void write(char[] cbuf) throws IOException {
		lineBuffer.append(cbuf);
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#write(java.lang.String)
	 */
	@Override
	public void write(String str) throws IOException {
		lineBuffer.append(str);
	}

}

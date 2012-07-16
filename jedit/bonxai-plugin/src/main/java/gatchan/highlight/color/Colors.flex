/*
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2010 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package gatchan.highlight.color;

%%


%class FlexColorScanner
%unicode
%char
%type ColorToken
%ignorecase


whitespace = [ \t]
ignore = [^] | whitespace
miscToken = [:letter:]+

rgbhexa1 = #[a-fA-F0-9]{6}
rgbhexa2 = 0x[a-fA-F0-9]{6}

%%

<YYINITIAL> 
{  
	{ignore}	{ /* ignore */ }
	{rgbhexa1}	{ return new ColorToken(yychar,yychar + yylength(), "0x"+yytext().substring(1));}
	{rgbhexa2}	{ return new ColorToken(yychar,yychar + yylength(), yytext());}
	AliceBlue	{ return new ColorToken(yychar,yychar + yylength(), "0xF0F8FF"); }
	AntiqueWhite	{ return new ColorToken(yychar,yychar + yylength(), "0xFAEBD7"); }
	Aqua	{ return new ColorToken(yychar,yychar + yylength(), "0x00FFFF"); }
	Aquamarine	{ return new ColorToken(yychar,yychar + yylength(), "0x7FFFD4"); }
	Azure	{ return new ColorToken(yychar,yychar + yylength(), "0xF0FFFF"); }
	Beige	{ return new ColorToken(yychar,yychar + yylength(), "0xF5F5DC"); }
	Bisque	{ return new ColorToken(yychar,yychar + yylength(), "0xFFE4C4"); }
	Black	{ return new ColorToken(yychar,yychar + yylength(), "0x000000"); }
	BlanchedAlmond	{ return new ColorToken(yychar,yychar + yylength(), "0xFFEBCD"); }
	Blue	{ return new ColorToken(yychar,yychar + yylength(), "0x0000FF"); }
	BlueViolet	{ return new ColorToken(yychar,yychar + yylength(), "0x8A2BE2"); }
	Brown	{ return new ColorToken(yychar,yychar + yylength(), "0xA52A2A"); }
	BurlyWood	{ return new ColorToken(yychar,yychar + yylength(), "0xDEB887"); }
	CadetBlue	{ return new ColorToken(yychar,yychar + yylength(), "0x5F9EA0"); }
	Chartreuse	{ return new ColorToken(yychar,yychar + yylength(), "0x7FFF00"); }
	Chocolate	{ return new ColorToken(yychar,yychar + yylength(), "0xD2691E"); }
	Coral	{ return new ColorToken(yychar,yychar + yylength(), "0xFF7F50"); }
	CornflowerBlue	{ return new ColorToken(yychar,yychar + yylength(), "0x6495ED"); }
	Cornsilk	{ return new ColorToken(yychar,yychar + yylength(), "0xFFF8DC"); }
	Crimson	{ return new ColorToken(yychar,yychar + yylength(), "0xDC143C"); }
	Cyan	{ return new ColorToken(yychar,yychar + yylength(), "0x00FFFF"); }
	DarkBlue	{ return new ColorToken(yychar,yychar + yylength(), "0x00008B"); }
	DarkCyan	{ return new ColorToken(yychar,yychar + yylength(), "0x008B8B"); }
	DarkGoldenRod	{ return new ColorToken(yychar,yychar + yylength(), "0xB8860B"); }
	DarkGray	{ return new ColorToken(yychar,yychar + yylength(), "0xA9A9A9"); }
	DarkGrey	{ return new ColorToken(yychar,yychar + yylength(), "0xA9A9A9"); }
	DarkGreen	{ return new ColorToken(yychar,yychar + yylength(), "0x006400"); }
	DarkKhaki	{ return new ColorToken(yychar,yychar + yylength(), "0xBDB76B"); }
	DarkMagenta	{ return new ColorToken(yychar,yychar + yylength(), "0x8B008B"); }
	DarkOliveGreen	{ return new ColorToken(yychar,yychar + yylength(), "0x556B2F"); }
	Darkorange	{ return new ColorToken(yychar,yychar + yylength(), "0xFF8C00"); }
	DarkOrchid	{ return new ColorToken(yychar,yychar + yylength(), "0x9932CC"); }
	DarkRed	{ return new ColorToken(yychar,yychar + yylength(), "0x8B0000"); }
	DarkSalmon	{ return new ColorToken(yychar,yychar + yylength(), "0xE9967A"); }
	DarkSeaGreen	{ return new ColorToken(yychar,yychar + yylength(), "0x8FBC8F"); }
	DarkSlateBlue	{ return new ColorToken(yychar,yychar + yylength(), "0x483D8B"); }
	DarkSlateGray	{ return new ColorToken(yychar,yychar + yylength(), "0x2F4F4F"); }
	DarkSlateGrey	{ return new ColorToken(yychar,yychar + yylength(), "0x2F4F4F"); }
	DarkTurquoise	{ return new ColorToken(yychar,yychar + yylength(), "0x00CED1"); }
	DarkViolet	{ return new ColorToken(yychar,yychar + yylength(), "0x9400D3"); }
	DeepPink	{ return new ColorToken(yychar,yychar + yylength(), "0xFF1493"); }
	DeepSkyBlue	{ return new ColorToken(yychar,yychar + yylength(), "0x00BFFF"); }
	DimGray	{ return new ColorToken(yychar,yychar + yylength(), "0x696969"); }
	DimGrey	{ return new ColorToken(yychar,yychar + yylength(), "0x696969"); }
	DodgerBlue	{ return new ColorToken(yychar,yychar + yylength(), "0x1E90FF"); }
	FireBrick	{ return new ColorToken(yychar,yychar + yylength(), "0xB22222"); }
	FloralWhite	{ return new ColorToken(yychar,yychar + yylength(), "0xFFFAF0"); }
	ForestGreen	{ return new ColorToken(yychar,yychar + yylength(), "0x228B22"); }
	Fuchsia	{ return new ColorToken(yychar,yychar + yylength(), "0xFF00FF"); }
	Gainsboro	{ return new ColorToken(yychar,yychar + yylength(), "0xDCDCDC"); }
	GhostWhite	{ return new ColorToken(yychar,yychar + yylength(), "0xF8F8FF"); }
	Gold	{ return new ColorToken(yychar,yychar + yylength(), "0xFFD700"); }
	GoldenRod	{ return new ColorToken(yychar,yychar + yylength(), "0xDAA520"); }
	Gray	{ return new ColorToken(yychar,yychar + yylength(), "0x808080"); }
	Grey	{ return new ColorToken(yychar,yychar + yylength(), "0x808080"); }
	Green	{ return new ColorToken(yychar,yychar + yylength(), "0x008000"); }
	GreenYellow	{ return new ColorToken(yychar,yychar + yylength(), "0xADFF2F"); }
	HoneyDew	{ return new ColorToken(yychar,yychar + yylength(), "0xF0FFF0"); }
	HotPink	{ return new ColorToken(yychar,yychar + yylength(), "0xFF69B4"); }
	IndianRed	{ return new ColorToken(yychar,yychar + yylength(), "0xCD5C5C"); }
	Indigo	{ return new ColorToken(yychar,yychar + yylength(), "0x4B0082"); }
	Ivory	{ return new ColorToken(yychar,yychar + yylength(), "0xFFFFF0"); }
	Khaki	{ return new ColorToken(yychar,yychar + yylength(), "0xF0E68C"); }
	Lavender	{ return new ColorToken(yychar,yychar + yylength(), "0xE6E6FA"); }
	LavenderBlush	{ return new ColorToken(yychar,yychar + yylength(), "0xFFF0F5"); }
	LawnGreen	{ return new ColorToken(yychar,yychar + yylength(), "0x7CFC00"); }
	LemonChiffon	{ return new ColorToken(yychar,yychar + yylength(), "0xFFFACD"); }
	LightBlue	{ return new ColorToken(yychar,yychar + yylength(), "0xADD8E6"); }
	LightCoral	{ return new ColorToken(yychar,yychar + yylength(), "0xF08080"); }
	LightCyan	{ return new ColorToken(yychar,yychar + yylength(), "0xE0FFFF"); }
	LightGoldenRodYellow	{ return new ColorToken(yychar,yychar + yylength(), "0xFAFAD2"); }
	LightGray	{ return new ColorToken(yychar,yychar + yylength(), "0xD3D3D3"); }
	LightGrey	{ return new ColorToken(yychar,yychar + yylength(), "0xD3D3D3"); }
	LightGreen	{ return new ColorToken(yychar,yychar + yylength(), "0x90EE90"); }
	LightPink	{ return new ColorToken(yychar,yychar + yylength(), "0xFFB6C1"); }
	LightSalmon	{ return new ColorToken(yychar,yychar + yylength(), "0xFFA07A"); }
	LightSeaGreen	{ return new ColorToken(yychar,yychar + yylength(), "0x20B2AA"); }
	LightSkyBlue	{ return new ColorToken(yychar,yychar + yylength(), "0x87CEFA"); }
	LightSlateGray	{ return new ColorToken(yychar,yychar + yylength(), "0x778899"); }
	LightSlateGrey	{ return new ColorToken(yychar,yychar + yylength(), "0x778899"); }
	LightSteelBlue	{ return new ColorToken(yychar,yychar + yylength(), "0xB0C4DE"); }
	LightYellow	{ return new ColorToken(yychar,yychar + yylength(), "0xFFFFE0"); }
	Lime	{ return new ColorToken(yychar,yychar + yylength(), "0x00FF00"); }
	LimeGreen	{ return new ColorToken(yychar,yychar + yylength(), "0x32CD32"); }
	Linen	{ return new ColorToken(yychar,yychar + yylength(), "0xFAF0E6"); }
	Magenta	{ return new ColorToken(yychar,yychar + yylength(), "0xFF00FF"); }
	Maroon	{ return new ColorToken(yychar,yychar + yylength(), "0x800000"); }
	MediumAquaMarine	{ return new ColorToken(yychar,yychar + yylength(), "0x66CDAA"); }
	MediumBlue	{ return new ColorToken(yychar,yychar + yylength(), "0x0000CD"); }
	MediumOrchid	{ return new ColorToken(yychar,yychar + yylength(), "0xBA55D3"); }
	MediumPurple	{ return new ColorToken(yychar,yychar + yylength(), "0x9370D8"); }
	MediumSeaGreen	{ return new ColorToken(yychar,yychar + yylength(), "0x3CB371"); }
	MediumSlateBlue	{ return new ColorToken(yychar,yychar + yylength(), "0x7B68EE"); }
	MediumSpringGreen	{ return new ColorToken(yychar,yychar + yylength(), "0x00FA9A"); }
	MediumTurquoise	{ return new ColorToken(yychar,yychar + yylength(), "0x48D1CC"); }
	MediumVioletRed	{ return new ColorToken(yychar,yychar + yylength(), "0xC71585"); }
	MidnightBlue	{ return new ColorToken(yychar,yychar + yylength(), "0x191970"); }
	MintCream	{ return new ColorToken(yychar,yychar + yylength(), "0xF5FFFA"); }
	MistyRose	{ return new ColorToken(yychar,yychar + yylength(), "0xFFE4E1"); }
	Moccasin	{ return new ColorToken(yychar,yychar + yylength(), "0xFFE4B5"); }
	NavajoWhite	{ return new ColorToken(yychar,yychar + yylength(), "0xFFDEAD"); }
	Navy	{ return new ColorToken(yychar,yychar + yylength(), "0x000080"); }
	OldLace	{ return new ColorToken(yychar,yychar + yylength(), "0xFDF5E6"); }
	Olive	{ return new ColorToken(yychar,yychar + yylength(), "0x808000"); }
	OliveDrab	{ return new ColorToken(yychar,yychar + yylength(), "0x6B8E23"); }
	Orange	{ return new ColorToken(yychar,yychar + yylength(), "0xFFA500"); }
	OrangeRed	{ return new ColorToken(yychar,yychar + yylength(), "0xFF4500"); }
	Orchid	{ return new ColorToken(yychar,yychar + yylength(), "0xDA70D6"); }
	PaleGoldenRod	{ return new ColorToken(yychar,yychar + yylength(), "0xEEE8AA"); }
	PaleGreen	{ return new ColorToken(yychar,yychar + yylength(), "0x98FB98"); }
	PaleTurquoise	{ return new ColorToken(yychar,yychar + yylength(), "0xAFEEEE"); }
	PaleVioletRed	{ return new ColorToken(yychar,yychar + yylength(), "0xD87093"); }
	PapayaWhip	{ return new ColorToken(yychar,yychar + yylength(), "0xFFEFD5"); }
	PeachPuff	{ return new ColorToken(yychar,yychar + yylength(), "0xFFDAB9"); }
	Peru	{ return new ColorToken(yychar,yychar + yylength(), "0xCD853F"); }
	Pink	{ return new ColorToken(yychar,yychar + yylength(), "0xFFC0CB"); }
	Plum	{ return new ColorToken(yychar,yychar + yylength(), "0xDDA0DD"); }
	PowderBlue	{ return new ColorToken(yychar,yychar + yylength(), "0xB0E0E6"); }
	Purple	{ return new ColorToken(yychar,yychar + yylength(), "0x800080"); }
	Red	{ return new ColorToken(yychar,yychar + yylength(), "0xFF0000"); }
	RosyBrown	{ return new ColorToken(yychar,yychar + yylength(), "0xBC8F8F"); }
	RoyalBlue	{ return new ColorToken(yychar,yychar + yylength(), "0x4169E1"); }
	SaddleBrown	{ return new ColorToken(yychar,yychar + yylength(), "0x8B4513"); }
	Salmon	{ return new ColorToken(yychar,yychar + yylength(), "0xFA8072"); }
	SandyBrown	{ return new ColorToken(yychar,yychar + yylength(), "0xF4A460"); }
	SeaGreen	{ return new ColorToken(yychar,yychar + yylength(), "0x2E8B57"); }
	SeaShell	{ return new ColorToken(yychar,yychar + yylength(), "0xFFF5EE"); }
	Sienna	{ return new ColorToken(yychar,yychar + yylength(), "0xA0522D"); }
	Silver	{ return new ColorToken(yychar,yychar + yylength(), "0xC0C0C0"); }
	SkyBlue	{ return new ColorToken(yychar,yychar + yylength(), "0x87CEEB"); }
	SlateBlue	{ return new ColorToken(yychar,yychar + yylength(), "0x6A5ACD"); }
	SlateGray	{ return new ColorToken(yychar,yychar + yylength(), "0x708090"); }
	SlateGrey	{ return new ColorToken(yychar,yychar + yylength(), "0x708090"); }
	Snow	{ return new ColorToken(yychar,yychar + yylength(), "0xFFFAFA"); }
	SpringGreen	{ return new ColorToken(yychar,yychar + yylength(), "0x00FF7F"); }
	SteelBlue	{ return new ColorToken(yychar,yychar + yylength(), "0x4682B4"); }
	Tan	{ return new ColorToken(yychar,yychar + yylength(), "0xD2B48C"); }
	Teal	{ return new ColorToken(yychar,yychar + yylength(), "0x008080"); }
	Thistle	{ return new ColorToken(yychar,yychar + yylength(), "0xD8BFD8"); }
	Tomato	{ return new ColorToken(yychar,yychar + yylength(), "0xFF6347"); }
	Turquoise	{ return new ColorToken(yychar,yychar + yylength(), "0x40E0D0"); }
	Violet	{ return new ColorToken(yychar,yychar + yylength(), "0xEE82EE"); }
	Wheat	{ return new ColorToken(yychar,yychar + yylength(), "0xF5DEB3"); }
	White	{ return new ColorToken(yychar,yychar + yylength(), "0xFFFFFF"); }
	WhiteSmoke	{ return new ColorToken(yychar,yychar + yylength(), "0xF5F5F5"); }
	Yellow	{ return new ColorToken(yychar,yychar + yylength(), "0xFFFF00"); }
	YellowGreen	{ return new ColorToken(yychar,yychar + yylength(), "0x9ACD32"); }
	{miscToken}	{ /* ignored */ }
}

package com.bamboocloud.uros.io;

public final class UrosTags {
    /* Serialize Tags */
    public static final int TagInteger = 'i';
    public static final int TagLong = 'l';
    public static final int TagDouble = 'd';
    public static final int TagNull = 'n';
    public static final int TagEmpty = 'e';
    public static final int TagTrue = 't';
    public static final int TagFalse = 'f';
    public static final int TagNaN = 'N';
    public static final int TagInfinity = 'I';
    public static final int TagDate = 'D';
    public static final int TagTime = 'T';
    public static final int TagUTC = 'Z';
    public static final int TagBytes = 'b';
    public static final int TagUTF8Char = 'u';
    public static final int TagString = 's';
    public static final int TagGuid = 'g';
    public static final int TagList = 'a';
    public static final int TagMap = 'm';
    public static final int TagClass = 'c';
    public static final int TagObject = 'o';
    public static final int TagRef = 'r';
    /* Serialize Marks */
    public static final int TagPos = '+';
    public static final int TagNeg = '-';
    public static final int TagSemicolon = ';';
    public static final int TagOpenbrace = '{';
    public static final int TagClosebrace = '}';
    public static final int TagQuote = '"';
    public static final int TagPoint = '.';
    /* Protocol Tags */
    public static final int TagFunctions = 'F';
    public static final int TagCall = 'C';
    public static final int TagResult = 'R';
    public static final int TagArgument = 'A';
    public static final int TagError = 'E';
    public static final int TagEnd = 'z';
}
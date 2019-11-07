package ru.textanalysis.tawt.ms.interfaces.gama;

import java.util.List;

public interface IGamaParser {
    public void init();

    public List<String> getParserBearingPhrase(String bearingPhrase);

    public List<List<String>> getParserSentence(String sentence);

    public List<List<List<String>>> getParserParagraph(String sentence);

    public List<List<List<List<String>>>> getParserText(String text);
}

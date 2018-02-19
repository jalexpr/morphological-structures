package morphologicalstructures;

import grammeme.MorfologyParameters.TypeOfSpeech;

public class NumberOmoForm extends OmoForm{

    private final String strNumber;
    
    public NumberOmoForm(String strNumber) {
        super(-1, -1, TypeOfSpeech.NUMERAL, 0);
        this.strNumber = strNumber;
    }
    
    @Override
    public String getInitialFormString() {
        return strNumber;
    }
}

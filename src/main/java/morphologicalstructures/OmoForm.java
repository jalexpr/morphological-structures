
package morphologicalstructures;

import java.util.LinkedList;

import grammeme.MorfologyParameters;
import load.BDFormString;
public class OmoForm implements MorfCharacteristicsAccessInterface {

    private final int initialFormKey;
    private final int myFormKey;
    private final byte typeOfSpeech;
    private final long morfCharacteristics;
    private final LinkedList myDependent = new LinkedList<>();
    private final LinkedList myMain = new LinkedList<>();

    public OmoForm(int initialFormKey, int myFormKey, byte typeOfSpeech, long morfCharacteristics) {
        this.initialFormKey = initialFormKey;
        this.typeOfSpeech = typeOfSpeech;
        this.morfCharacteristics = morfCharacteristics;
        this.myFormKey = myFormKey;
    }

    /**
     * Получить String в начальной форме
     * @return
     */
    @Override
    public String getInitialFormString() {
        return BDFormString.getStringById(initialFormKey, true);
    }

    @Override
    public String getMyFormString() {
        return BDFormString.getStringById(myFormKey, false);
    }

    /**
     * Получить часть речи
     * @return
     */
    @Override
    public byte getTypeOfSpeech() {
        return typeOfSpeech;
    }

    /**
     * Получить все морф. характеристики, кроме части речи
     * @return
     */
    @Override
    public long getAllMorfCharacteristics() {
        return morfCharacteristics;
    }

    /**
     * Получить морф. характеристики, кроме части речи
     * @param IDENTIFIER
     * @return
     */
    @Override
    public long getTheMorfCharacteristic(long IDENTIFIER) {
        return morfCharacteristics & IDENTIFIER;
    }

    @Override
    public long getTheMorfCharacteristic(Class clazz) {
        if(clazz.getSimpleName().equals("Voice")) {
            return morfCharacteristics & MorfologyParameters.Voice.IDENTIFIER;
        } else if (clazz.getSimpleName().equals("Time")) {
            return morfCharacteristics & MorfologyParameters.Time.IDENTIFIER;
        }
        return 0;
    }

    @Override
    public String toString() {
        return String.format("initialFormString = %s, typeOfSpeech = %d, morfCharacteristics = %d", getInitialFormString(), typeOfSpeech, morfCharacteristics);
    }
    
    @Override
    public boolean haveMainForm() {
        return !myMain.isEmpty();
    }
    
    @Override
    public boolean haveDependentForm() {
        return !myDependent.isEmpty();
    }
    
    @Override
    public boolean haveCommunication() {
        return haveMainForm() || haveDependentForm();
    }
    
    @Override
    public void addDependentForm(OmoForm mainForm) {
        myMain.add(mainForm);
        mainForm.addMainForm(this);
    }
    
    private void addMainForm(OmoForm dependentForm) {
        myDependent.add(dependentForm);
    }

    @Override
    public int getMyFormKey() {
        return myFormKey;
    }

    @Override
    public int getInitialFormKey() {
        return initialFormKey;
    }
}

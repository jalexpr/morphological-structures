
package morphologicalstructures;

import java.util.LinkedList;
import load.BDFormString;
import static grammeme.MorfologyParametersHelper.*;

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

    @Override
    public long getTheMorfCharacteristics(Long...identifiers) {
        long mask = 0;
        for(long identifier : identifiers) {
            mask |= identifier;
        }
        return morfCharacteristics & mask;
    }

    @Override
    public long getTheMorfCharacteristics(Class...clazzes) {
        long mask = 0;
        for(Class clazz : clazzes) {
            mask |= identifierParametersByClass(clazz);
        }
        return getAllMorfCharacteristicsByMask(mask);
    }

    private long getAllMorfCharacteristicsByMask(Long mask) {
        return morfCharacteristics & mask;
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

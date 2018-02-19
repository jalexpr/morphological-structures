
package morphologicalstructures;

public interface MorfCharacteristicsAccessInterface {
    
    public byte getTypeOfSpeech();
    public long getAllMorfCharacteristics();
    public long getTheMorfCharacteristic(long IDENTIFIER);
    public long getTheMorfCharacteristic(Class clazz);
    public String getInitialFormString();
    public String getMyFormString();
    public int getMyFormKey();
    public int getInitialFormKey();
    @Override
    public String toString();
    public boolean haveMainForm();
    public boolean haveDependentForm();
    public void addDependentForm(OmoForm mainForm);    
    public boolean haveCommunication();
}

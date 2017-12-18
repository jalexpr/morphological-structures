
package morphologicalstructures;

public interface MorfCharacteristicsAccessInterface {
    
    public byte getTypeOfSpeech();
    public long getMorfCharacteristics();
    public long getTheMorfCharacteristic(long IDENTIFIER);
    public String getInitialFormString();
    @Override
    public String toString();
     public boolean haveMainForm();
    public boolean haveDependentForm();
    public void addDependentForm(OmoForm mainForm);    
    public boolean haveCommunication();
}

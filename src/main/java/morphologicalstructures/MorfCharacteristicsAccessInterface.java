
package morphologicalstructures;


public interface MorfCharacteristicsAccessInterface {

    public boolean haveMainForm();
    public boolean haveDependentForm();
    public boolean haveCommunication();
    public byte getTypeOfSpeech();
    public int getMyFormKey();
    public int getInitialFormKey();
    public long getAllMorfCharacteristics();
    public long getTheMorfCharacteristics(Long...IDENTIFIERS);
    public long getTheMorfCharacteristics(Class...clazzes);
    @Override
    public String toString();
    public String getInitialFormString();
    public String getMyFormString();
    public void addDependentForm(OmoForm mainForm);

}

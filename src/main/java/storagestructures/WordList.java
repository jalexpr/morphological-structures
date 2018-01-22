
package storagestructures;

import java.util.LinkedList;

public class WordList extends LinkedList<OmoFormList> {
    
    public boolean isSingleValuedForm() {
        return size() == 1;
    }
}

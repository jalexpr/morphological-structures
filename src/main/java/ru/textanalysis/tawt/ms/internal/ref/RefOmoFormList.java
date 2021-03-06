package ru.textanalysis.tawt.ms.internal.ref;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.textanalysis.tawt.ms.internal.OmoForm;
import ru.textanalysis.tawt.ms.internal.form.Form;
import ru.textanalysis.tawt.ms.storage.OmoFormList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RefOmoFormList {
    private Logger log = LoggerFactory.getLogger(getClass());
    protected List<RefOmoForm> refOmoForms;

    public RefOmoFormList(List<Form> forms) {
        refOmoForms = new LinkedList<>();
        forms.forEach(form -> refOmoForms.add(new RefOmoForm((Form) form)));
    }

//    public RefOmoFormList(List<GetCharacteristics> forms) {
//        refOmoForms = new LinkedList<>();
//        forms.forEach(form -> {
//            if (form instanceof Form) {
//                refOmoForms.add(new RefOmoForm((Form) form));
//            } else if (form instanceof RefOmoForm) {
//                refOmoForms.add((RefOmoForm) form);
//            } else {
//                log.warn("Unknown type: " + form.getClass().getName());
//            }
//        });
//    }

    private RefOmoFormList(RefOmoFormList refOmoFormList) {
        refOmoForms = new ArrayList<>(refOmoFormList.refOmoForms);
    }

    public RefOmoFormList createRefOmoFormList(RefOmoFormList refOmoFormList) {
        return new RefOmoFormList(refOmoFormList);
    }

    public void applyFilter(Predicate<? super RefOmoForm> predicate) {
        refOmoForms = refOmoForms.stream().filter(predicate).collect(Collectors.toList());
    }

    public List<RefOmoForm> getByFilter(Predicate<? super RefOmoForm> predicate) {
        return refOmoForms.stream().filter(predicate).collect(Collectors.toList());
    }

    public List<RefOmoForm> copy() {
        return new ArrayList<>(refOmoForms);
    }

    public Stream<RefOmoForm> stream() {
        return refOmoForms.stream();
    }

    public int size() {
        return refOmoForms.size();
    }

    public OmoFormList getOmoForm() {
        OmoFormList list = new OmoFormList();
        refOmoForms.forEach(refOmoForm ->
                list.add(new OmoForm(refOmoForm.getForm()))
        );
        return list;
    }

    @Override
    public String toString() {
        return refOmoForms.isEmpty() ?
                "\n\t\tRefOmoFormList: null" :
                "\n\t\tRefOmoFormList - " + refOmoForms.get(0).getForm().getInitialFormString() + " :\n" +
                refOmoForms.toString();
    }

    public boolean isDetected() {
        return !refOmoForms.isEmpty();
    }
}
